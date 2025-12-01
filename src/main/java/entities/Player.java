package entities;

import core.GameObject;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Player extends GameObject {
    private int velocityY;
    private int velocityX = 6;
    private final int gravity = 1;
    private final int jumpStrength = -16;
    private boolean isJumping = true;

    public Player(int x, int y, int width, int height) {
        super(x, y, width, height);
        this.velocityY = 0;
    }

    public void setSpeed(int speed) {
        this.velocityX = speed;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getVelocityY() { return velocityY; }

    public void jump() {
        if (!isJumping) {
            velocityY = jumpStrength;
            isJumping = true;
        }
    }

    public void update() {
        velocityY += gravity;
        y += velocityY;
        x += velocityX;

        if (velocityY > 20) velocityY = 20;
    }

    public void updateHorizontal() {
        x += velocityX;
    }

    public void landOn(int platformTop) {
        y = platformTop - height;
        velocityY = 0;
        isJumping = false;
    }

    @Override
    public void render(GraphicsContext gc) {
        gc.setFill(Color.YELLOW);
        gc.fillRect(x, y, width, height);

        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);
        gc.strokeRect(x, y, width, height);

        gc.setFill(Color.BLACK);
        gc.fillRect(x + width - 15, y + 10, 8, 8);
    }

    public boolean intersects(GameObject o) {
        return !(getRight() < o.getLeft() ||
                getLeft() > o.getRight() ||
                getBottom() < o.getTop() ||
                getTop() > o.getBottom());
    }
}