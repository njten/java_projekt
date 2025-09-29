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


public class Main extends Application {

    private Player player;
    private Obstacle obstacle;

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
        obstacle = new Obstacle(700, 270, 50, 50, 5);

        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.SPACE) {
                player.jump();
            }
        });

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                update();
                render(gc);
            }
        };
        timer.start();
    }

    private void update() {
        player.update();
        obstacle.update();
    }

    private void render(GraphicsContext gc) {
        gc.clearRect(0, 0, 800, 400);

        player.render(gc);
        obstacle.render(gc);
    }

    public static void main(String[] args) {
        launch();
    }
}