package core;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.animation.AnimationTimer;
import entities.Player;
import entities.Obstacle;
import entities.Platform;
import java.util.Random;

public class Main extends Application {

    private Player player;
    private Obstacle[] obstacles;
    private Platform[] platforms;
    private final int OBSTACLE_COUNT = 5;
    private final int PLATFORM_COUNT = 4;
    private boolean gameOver = false;
    private AnimationTimer timer;

    @Override
    public void start(Stage stage) {
        Pane root = new Pane();
        Canvas canvas = new Canvas(800, 400);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        root.getChildren().add(canvas);

        Scene scene = new Scene(root);
        stage.setTitle("Projekt - Geometry Dash");
        stage.setScene(scene);
        stage.show();

        player = new Player(100, 250, 30, 30);

        obstacles = new Obstacle[OBSTACLE_COUNT];
        Random rand = new Random();
        for (int i = 0; i < OBSTACLE_COUNT; i++) {
            int x = 300 + rand.nextInt(450); // více vpravo
            int y = 270 + rand.nextInt(80);
            int width = 40;
            int height = 40;
            obstacles[i] = new Obstacle(x, y, width, height);
        }

        platforms = new Platform[PLATFORM_COUNT];
        for (int i = 0; i < PLATFORM_COUNT; i++) {
            int x = 100 + rand.nextInt(600);
            int y = 150 + rand.nextInt(200);
            int width = 80 + rand.nextInt(80);
            int height = 15;
            platforms[i] = new Platform(x, y, width, height);
        }

        scene.setOnKeyPressed(e -> {
            if (!gameOver && e.getCode() == KeyCode.SPACE) {
                player.jump();
            }
        });

        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                update();
                render(gc);
            }
        };
        timer.start();
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
            player.updateHorizontal(); // pouze vodorovný pohyb
        }

        for (Obstacle obstacle : obstacles) {
            if (player.intersects(obstacle)) {
                gameOver = true;
                timer.stop();
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
            gc.setFill(javafx.scene.paint.Color.BLACK);
            gc.fillText("GAME OVER", 350, 200);
        }
    }

    public static void main(String[] args) {
        launch();
    }
}