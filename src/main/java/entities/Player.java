package entities;

import core.GameObject;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Player extends GameObject {
    private int velocityY;
    private int velocityX = 3; // automatický pohyb doprava
    private final int gravity = 1;

    public Player(int x, int y, int width, int height) {
        super(x, y, width, height);
        this.velocityY = 0;
    }

    public void setSpeed(int speed) {
        this.velocityX = speed;
    }

    public int getX() {
        return x;
    }

    public void jump() {
        if (velocityY == 0) {
            velocityY = -15;
        }
    }

    public void update() {
        velocityY += gravity;
        y += velocityY;
        x += velocityX;

        if (y > 250) {
            y = 250;
            velocityY = 0;
        }
    }

    public void updateHorizontal() {
        x += velocityX;
    }

    public void landOn(int platformTop) {
        y = platformTop - height;
        velocityY = 0;
    }

    @Override
    public void render(GraphicsContext gc) {
        gc.setFill(Color.RED);
        gc.fillRect(x, y, width, height);
        gc.setStroke(Color.BLACK);
        gc.strokeRect(x, y, width, height);
    }

    public boolean intersects(Obstacle o) {
        return !(getRight() < o.getLeft() ||
                getLeft() > o.getRight() ||
                getBottom() < o.getTop() ||
                getTop() > o.getBottom());
    }
}