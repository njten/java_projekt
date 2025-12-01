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
    private static final String SAVE_FILE = "savegame.bin";

    private Stage mainStage;
    private StackPane gameRoot;
    private Canvas canvas;
    private AnimationTimer timer;
    private Game game;
    private InputHandler inputHandler = new InputHandler();
    private StartScreenController.Difficulty lastDifficulty;
    private ScoreManager scoreManager;

    private FeiEmployeeNerf feiEmployeeService = new FeiEmployeeNerf();
    private boolean isFeiEmployee = false;

    private StartScreenController startScreenController;

    @Override
    public void start(Stage stage) throws Exception {
        this.mainStage = stage;
        this.scoreManager = new ScoreManager();

        feiEmployeeService.loadEmployeeNames();
        showStartScreen();
        promptForUsername();
    }

    private void promptForUsername() {
        TextInputDialog dialog = new TextInputDialog("");
        dialog.initStyle(StageStyle.UTILITY);
        dialog.setTitle("Přihlášení");
        dialog.setHeaderText("Zadejte své jméno pro uložení a načtení postupu.");
        dialog.setContentText("Celé jméno:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent() && !result.get().trim().isEmpty()) {
            String username = result.get().trim();

            scoreManager.setCurrentUser(username);
            if (startScreenController != null) {
                startScreenController.updateProgressLabels();
            }

            if (feiEmployeeService.isFeiEmployee(username)) {
                isFeiEmployee = true;
                System.out.println("Uživatel '" + username + "' je z FEI.");

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Vítejte");
                alert.setHeaderText(null);
                alert.setContentText("Jako člen katedry informatiky nedostanete Slow-Motion při respawnu!");
                alert.show();
            }
        }
    }

    private void showStartScreen() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/start_screen.fxml"));
        Parent root = loader.load();

        this.startScreenController = loader.getController();
        startScreenController.setPrimaryStage(mainStage);
        startScreenController.setScoreManager(scoreManager);

        startScreenController.updateProgressLabels();

        startScreenController.setOnStartGameListener(difficulty -> {
            try {
                startGame(difficulty);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        startScreenController.setOnLoadGameListener(this::loadGame);

        Scene scene = new Scene(root, WIDTH, HEIGHT);
        mainStage.setTitle("Projekt - Geometry Dash");
        mainStage.setScene(scene);
        mainStage.show();
    }

    private void startGame(StartScreenController.Difficulty difficulty) {
        lastDifficulty = difficulty;
        game = new Game();

        game.setFeiNerfActive(isFeiEmployee);

        game.addListener(new GameListener() {
            @Override
            public void onGameOver() { handleGameOver(); }
            @Override
            public void onLevelComplete() { handleLevelComplete(); }
        });

        String mapFile = "/levels/lvl01.csv";
        if (difficulty == StartScreenController.Difficulty.MEDIUM) mapFile = "/levels/lvl02.csv";
        if (difficulty == StartScreenController.Difficulty.HARD) mapFile = "/levels/lvl03.csv";

        Level level = new Level(mapFile, 100, 300);
        game.reset(level);
        game.getPlayer().setSpeed(DEFAULT_PLAYER_SPEED);

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
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void loadGame() {
        File file = new File(SAVE_FILE);
        if (!file.exists()) return;
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(SAVE_FILE))) {
            this.game = (Game) in.readObject();
            this.game.initAfterLoad();
            this.game.addListener(new GameListener() {
                @Override
                public void onGameOver() { handleGameOver(); }
                @Override
                public void onLevelComplete() { handleLevelComplete(); }
            });
            initGameScene();
            showPauseMenu();
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void handleKeyPressed(KeyEvent e) {
        inputHandler.handleKeyPressed(e);
        if (inputHandler.isPressed(javafx.scene.input.KeyCode.SPACE)) game.playerJump();
        if (inputHandler.isPressed(javafx.scene.input.KeyCode.S)) game.placeCheckpoint();
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
        int percentage = game.getScore();

        if (scoreManager.saveProgress(lastDifficulty, percentage)) {
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Nový rekord!");
                alert.setHeaderText(null);
                alert.setContentText("Dosáhl jsi " + percentage + "%!");
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
                        if (startScreenController != null) startScreenController.updateProgressLabels();
                    } catch (Exception e) { e.printStackTrace(); }
                });
            } catch (Exception ex) { ex.printStackTrace(); }
        });
        pause.play();
    }

    private void handleLevelComplete() {
        timer.stop();
        scoreManager.saveProgress(lastDifficulty, 100);
        game.render(canvas.getGraphicsContext2D());
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Vítězství!");
            alert.setHeaderText(null);
            alert.setContentText("Gratulujeme! Level dokončen (100%).");
            alert.showAndWait();
            try {
                showStartScreen();
                if (startScreenController != null) startScreenController.updateProgressLabels();
            } catch (Exception e) { e.printStackTrace(); }
        });
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
            controller.setOnSave(this::saveGame);
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