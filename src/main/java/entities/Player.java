package entities;

import core.GameObject;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Player extends GameObject {
    private double velocityY;
    private int velocityX = 6;
    private final double gravity = 1.0;
    private final double jumpStrength = -16.0;

    private boolean isJumping = true;

    // Kvuli slow motion effectu
    private double realX;
    private double realY;

    public Player(int x, int y, int width, int height) {
        super(x, y, width, height);
        this.velocityY = 0;
        this.realX = x;
        this.realY = y;
    }

    public void respawnAt(int newX, int newY) {
        this.x = newX;
        this.y = newY;
        this.realX = newX;
        this.realY = newY;
        this.velocityY = 0;
        this.isJumping = true;
    }

    public void setSpeed(int speed) {
        this.velocityX = speed;
    }

    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }
    public double getVelocityY() {
        return velocityY;
    }

    public void jump() {
        if (!isJumping) {
            velocityY = jumpStrength;
            isJumping = true;
        }
    }

    public void update(double multiplier) {
        velocityY += gravity * multiplier;
        realY += velocityY * multiplier;
        realX += velocityX * multiplier;

        y = (int) realY;
        x = (int) realX;

        if (velocityY > 20) {
            velocityY = 20;
        }
    }

    public void landOn(int platformTop) {
        y = platformTop - height;
        realY = y;
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
        gc.fillRect(x + width - 10, y + 5, 6, 6);
    }
}