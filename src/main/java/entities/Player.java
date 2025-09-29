package entities;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Player {
    private int x, y;
    private int width, height;
    private int velocityY;
    private final int gravity = 1;

    public Player(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.velocityY = 0;
    }

    public void jump() {
        velocityY = -15;
    }

    public void update() {
        velocityY += gravity;
        y += velocityY;

        if (y > 250) {
            y = 250;
            velocityY = 0;
        }
    }

    public void render(GraphicsContext gc) {
        gc.setFill(Color.RED);
        gc.fillRect(x, y, width, height);
    }
}