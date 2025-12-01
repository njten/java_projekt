package core;

import javafx.animation.AnimationTimer;
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import ui.PauseMenuController;
import ui.StartScreenController;

import java.io.*;
import java.util.Optional;

public class Main extends Application {
    private static final int WIDTH = 1000;
    private static final int HEIGHT = 600;

    private static final int DEFAULT_PLAYER_SPEED = 6;
    private static final int FEI_PLAYER_SPEED = 4;
    private static final String SAVE_FILE = "savegame.bin";

    private Stage mainStage;
    private StackPane gameRoot;
    private Canvas canvas;
    private AnimationTimer timer;
    private Game game;
    private InputHandler inputHandler = new InputHandler();
    private StartScreenController.Difficulty lastDifficulty;
    private ScoreManager scoreManager;
    private FeiEmployeeNerf feiEmployeeService;
    private boolean isFeiEmployee = false;

    @Override
    public void start(Stage stage) throws Exception {
        this.mainStage = stage;
        this.scoreManager = new ScoreManager();

        new Thread(() -> {
            feiEmployeeService = new FeiEmployeeNerf();
            feiEmployeeService.loadEmployeeNames();
            Platform.runLater(this::promptForUsername);
        }).start();

        showStartScreen();
    }

    private void promptForUsername() {
        TextInputDialog dialog = new TextInputDialog("");
        dialog.initStyle(StageStyle.UTILITY);
        dialog.setTitle("Ověření");
        dialog.setHeaderText("Pro ověření zadejte své přijmení a jméno.");
        dialog.setContentText("Celé jméno:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent() && !result.get().trim().isEmpty()) {
            String username = result.get();
            if (feiEmployeeService.isFeiEmployee(username)) {
                isFeiEmployee = true;
                System.out.println("Uživatel '" + username + "' byl rozpoznán jako člen katedry informatiky.");
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Ověření úspěšné");
                    alert.setHeaderText("Vítejte!");
                    alert.setContentText("Jako člen katedry informatiky dostanete nerf!");
                    alert.show();
                });
            }
        }
    }

    private void showStartScreen() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/start_screen.fxml"));
        Parent root = loader.load();

        StartScreenController controller = loader.getController();
        controller.setPrimaryStage(mainStage);
        controller.setOnStartGameListener(difficulty -> {
            try {
                startGame(difficulty);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        controller.setScoreManager(scoreManager);
        controller.displayHighScores();

        Scene scene = new Scene(root, WIDTH, HEIGHT);
        mainStage.setTitle("Projekt - Geometry Dash");
        mainStage.setScene(scene);
        mainStage.show();
    }


    private void startGame(StartScreenController.Difficulty difficulty) {
        lastDifficulty = difficulty;
        game = new Game();
        game.setOnGameOver(this::handleGameOver);

        Level level;
        String mapFile;

        switch (difficulty) {
            case EASY: mapFile = "/levels/lvl01.csv"; break;
            case MEDIUM: mapFile = "/levels/lvl02.csv"; break;
            case HARD: mapFile = "/levels/lvl03.csv"; break;
            default: mapFile = "/levels/lvl01.csv";
        }

        level = new Level(mapFile, 100, 300);
        game.reset(level);

        if (isFeiEmployee) {
            game.getPlayer().setSpeed(FEI_PLAYER_SPEED);
        } else {
            game.getPlayer().setSpeed(DEFAULT_PLAYER_SPEED);
        }

        initGameScene();
    }

    private void initGameScene() {
        gameRoot = new StackPane();
        Pane root = new Pane();
        canvas = new Canvas(WIDTH, HEIGHT);
        root.getChildren().add(canvas);
        gameRoot.getChildren().add(root);

        GraphicsContext gc = canvas.getGraphicsContext2D();

        Scene gameScene = new Scene(gameRoot, WIDTH, HEIGHT);
        mainStage.setScene(gameScene);

        gameScene.setOnKeyPressed(this::handleKeyPressed);
        gameScene.setOnKeyReleased(this::handleKeyReleased);

        if (timer != null) timer.stop();
        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (!game.isPaused()) {
                    game.update();
                }
                game.render(gc);
            }
        };
        timer.start();
    }

    private void saveGame() {
        if (game == null) return;
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(SAVE_FILE))) {
            out.writeObject(game);
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Uložení hry");
                alert.setHeaderText(null);
                alert.setContentText("Hra byla uložena!");
                alert.show();
            });
        } catch (IOException e) {
            System.err.println("Chyba při ukládání: " + e.getMessage());
        }
    }

    private void handleKeyPressed(KeyEvent e) {
        inputHandler.handleKeyPressed(e);
        if (inputHandler.isPressed(javafx.scene.input.KeyCode.SPACE)) {
            game.playerJump();
        }
        if (inputHandler.isPressed(javafx.scene.input.KeyCode.ESCAPE)) {
            if (!game.isPaused()) showPauseMenu();
            else hidePauseMenu();
        }
    }

    private void handleKeyReleased(KeyEvent e) {
        inputHandler.handleKeyReleased(e);
    }

    private void handleGameOver() {
        timer.stop();
        int finalScore = game.getScore();
        if (scoreManager.updateHighScore(lastDifficulty, finalScore)) {
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Nové nejvyšší skóre!");
                alert.setHeaderText(null);
                alert.setContentText("Gratulujeme! Vaše nové nejvyšší skóre je " + finalScore);
                alert.showAndWait();
            });
        }

        game.render(canvas.getGraphicsContext2D());
        PauseTransition pause = new PauseTransition(Duration.seconds(2));
        pause.setOnFinished(event -> {
            try {
                Platform.runLater(() -> {
                    try {
                        showStartScreen();
                    } catch (Exception e) { e.printStackTrace(); }
                });
            } catch (Exception ex) { ex.printStackTrace(); }
        });
        pause.play();
    }

    private void showPauseMenu() {
        game.pause();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/pause_menu.fxml"));
            Parent pauseMenu = loader.load();
            PauseMenuController controller = loader.getController();
            controller.setOnResume(this::hidePauseMenu);
            controller.setOnRestart(() -> {
                hidePauseMenu();
                startGame(lastDifficulty);
            });
            controller.setOnExit(() -> {
                timer.stop();
                try { showStartScreen(); } catch (Exception ex) { ex.printStackTrace(); }
            });
            pauseMenu.setMouseTransparent(false);
            gameRoot.getChildren().add(pauseMenu);
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    private void hidePauseMenu() {
        game.resume();
        if (gameRoot.getChildren().size() > 1)
            gameRoot.getChildren().remove(gameRoot.getChildren().size() - 1);
    }

    public static void main(String[] args) {
        launch();
    }
}