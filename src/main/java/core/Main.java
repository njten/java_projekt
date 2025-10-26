package core;

import javafx.animation.AnimationTimer;
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import ui.PauseMenuController;
import ui.StartScreenController;

public class Main extends Application {
    private static final int WIDTH = 1200;
    private static final int HEIGHT = 800;

    private Stage mainStage;
    private StackPane gameRoot;
    private Canvas canvas;
    private AnimationTimer timer;
    private Game game;
    private InputHandler inputHandler = new InputHandler();
    private StartScreenController.Difficulty lastDifficulty;

    @Override
    public void start(Stage stage) throws Exception {
        this.mainStage = stage;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/start_screen.fxml"));
        Parent root = loader.load();

        StartScreenController controller = loader.getController();
        controller.setPrimaryStage(stage);
        controller.setOnStartGameListener(this::startGame);

        // sjednocená velikost okna
        Scene scene = new Scene(root, WIDTH, HEIGHT);
        stage.setTitle("Projekt - Geometry Dash");
        stage.setScene(scene);
        stage.show();
    }

    private void startGame(StartScreenController.Difficulty difficulty) {
        lastDifficulty = difficulty;
        game = new Game();
        game.setOnGameOver(this::handleGameOver);

        // Zvol Level podle obtížnosti
        Level level;
        switch (difficulty) {
            case EASY:
                level = new Level(4, 5, 100, 250, WIDTH, HEIGHT);
                break;
            case MEDIUM:
                level = new Level(6, 4, 100, 250, WIDTH, HEIGHT);
                break;
            case HARD:
                level = new Level(8, 3, 100, 250, WIDTH, HEIGHT);
                break;
            default:
                level = new Level(4, 5, 100, 250, WIDTH, HEIGHT); // fallback
        }
        game.reset(level);

        gameRoot = new StackPane();
        Pane root = new Pane();
        canvas = new Canvas(WIDTH, HEIGHT);
        root.getChildren().add(canvas);
        gameRoot.getChildren().add(root);

        GraphicsContext gc = canvas.getGraphicsContext2D();

        // sjednocená velikost okna i pro hru
        Scene gameScene = new Scene(gameRoot, WIDTH, HEIGHT);
        mainStage.setScene(gameScene);

        gameScene.setOnKeyPressed(this::handleKeyPressed);
        gameScene.setOnKeyReleased(this::handleKeyReleased);

        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (!game.isPaused()) {
                    game.update();
                    game.render(gc);
                }
            }
        };
        timer.start();
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
        // Zobrazí GAME OVER a po 5s návrat do menu
        game.render(canvas.getGraphicsContext2D());
        PauseTransition pause = new PauseTransition(Duration.seconds(5));
        pause.setOnFinished(event -> {
            try {
                start(mainStage);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
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
                try {
                    start(mainStage);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
            pauseMenu.setMouseTransparent(false);
            gameRoot.getChildren().add(pauseMenu);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
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