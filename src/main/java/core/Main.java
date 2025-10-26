package core;

import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.animation.AnimationTimer;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import entities.Player;
import entities.Obstacle;
import entities.Platform;
import javafx.util.Duration;
import ui.StartScreenController;
import ui.PauseMenuController;

import java.util.Random;

public class Main extends Application {

    private Player player;
    private Obstacle[] obstacles;
    private Platform[] platforms;
    private boolean gameOver = false;
    private boolean paused = false;
    private AnimationTimer timer;
    private Stage mainStage;
    private StackPane gameRoot;
    private Canvas canvas;
    private StartScreenController.Difficulty lastDifficulty;

    @Override
    public void start(Stage stage) throws Exception {
        mainStage = stage;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/start_screen.fxml"));
        Parent root = loader.load();

        StartScreenController controller = loader.getController();
        controller.setPrimaryStage(stage);
        controller.setOnStartGameListener(this::startGame);

        Scene scene = new Scene(root);
        stage.setTitle("Projekt - Geometry Dash");
        stage.setScene(scene);
        stage.show();
    }

    private void startGame(StartScreenController.Difficulty difficulty) {
        lastDifficulty = difficulty;
        gameRoot = new StackPane();
        Pane root = new Pane();
        canvas = new Canvas(800, 400);
        root.getChildren().add(canvas);
        gameRoot.getChildren().add(root);

        GraphicsContext gc = canvas.getGraphicsContext2D();

        Scene gameScene = new Scene(gameRoot);
        mainStage.setScene(gameScene);
        mainStage.setTitle("Projekt - Geometry Dash");

        int playerSpeed = 3;
        int obstacleCount = 5;
        int platformCount = 4;

        switch (difficulty) {
            case EASY:
                playerSpeed = 3; obstacleCount = 4; platformCount = 5; break;
            case MEDIUM:
                playerSpeed = 4; obstacleCount = 6; platformCount = 4; break;
            case HARD:
                playerSpeed = 5; obstacleCount = 8; platformCount = 3; break;
            case ENDLESS:
                playerSpeed = 4; obstacleCount = 5; platformCount = 4; break;
        }

        player = new Player(100, 250, 30, 30);
        player.setSpeed(playerSpeed);

        obstacles = new Obstacle[obstacleCount];
        Random rand = new Random();
        for (int i = 0; i < obstacleCount; i++) {
            int x = 300 + rand.nextInt(450);
            int y = 270 + rand.nextInt(80);
            int width = 40;
            int height = 40;
            obstacles[i] = new Obstacle(x, y, width, height);
        }

        platforms = new Platform[platformCount];
        for (int i = 0; i < platformCount; i++) {
            int x = 100 + rand.nextInt(600);
            int y = 150 + rand.nextInt(200);
            int width = 80 + rand.nextInt(80);
            int height = 15;
            platforms[i] = new Platform(x, y, width, height);
        }

        gameOver = false;
        paused = false;

        gameScene.setOnKeyPressed(e -> {
            if (!gameOver && !paused && e.getCode().toString().equals("SPACE")) {
                player.jump();
            }
            if (!gameOver && e.getCode().toString().equals("ESCAPE")) {
                if (!paused) showPauseMenu();
                else hidePauseMenu();
            }
        });

        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (!paused) {
                    update();
                    render(gc);
                    if (difficulty == StartScreenController.Difficulty.ENDLESS && !gameOver) {
                        if (player.getX() > 700) {
                            addEndlessObstacle();
                            addEndlessPlatform();
                        }
                    }
                }
            }
        };
        timer.start();
    }

    private void showPauseMenu() {
        paused = true;
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
        paused = false;
        if (gameRoot.getChildren().size() > 1)
            gameRoot.getChildren().remove(gameRoot.getChildren().size() - 1);
    }

    private void addEndlessObstacle() {
        Obstacle[] newObstacles = new Obstacle[obstacles.length + 1];
        System.arraycopy(obstacles, 0, newObstacles, 0, obstacles.length);
        Random rand = new Random();
        int x = 800 + rand.nextInt(200);
        int y = 270 + rand.nextInt(80);
        int width = 40;
        int height = 40;
        newObstacles[newObstacles.length - 1] = new Obstacle(x, y, width, height);
        obstacles = newObstacles;
    }

    private void addEndlessPlatform() {
        Platform[] newPlatforms = new Platform[platforms.length + 1];
        System.arraycopy(platforms, 0, newPlatforms, 0, platforms.length);
        Random rand = new Random();
        int x = 800 + rand.nextInt(200);
        int y = 150 + rand.nextInt(200);
        int width = 80 + rand.nextInt(80);
        int height = 15;
        newPlatforms[newPlatforms.length - 1] = new Platform(x, y, width, height);
        platforms = newPlatforms;
    }

    private void update() {
        if (gameOver) return;

        boolean onAnyPlatform = false;
        for (Platform platform : platforms) {
            if (platform.isPlayerOnTop(player)) {
                player.landOn(platform.getTop());
                onAnyPlatform = true;
                break;
            }
        }
        if (!onAnyPlatform) {
            player.update();
        } else {
            player.updateHorizontal();
        }

        for (Obstacle obstacle : obstacles) {
            if (player.intersects(obstacle)) {
                gameOver = true;
                timer.stop();
                render(canvas.getGraphicsContext2D());

                PauseTransition pause = new PauseTransition(Duration.seconds(5));
                pause.setOnFinished(event -> {
                    try {
                        start(mainStage);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                });
                pause.play();
                break;
            }
        }
    }

    private void render(GraphicsContext gc) {
        gc.clearRect(0, 0, 800, 400);

        for (Platform platform : platforms) {
            platform.render(gc);
        }
        player.render(gc);
        for (Obstacle obstacle : obstacles) {
            obstacle.render(gc);
        }

        if (gameOver) {
            gc.setFill(Color.BLACK);
            gc.fillText("GAME OVER", 350, 200);
        }
    }

    public static void main(String[] args) {
        launch();
    }
}