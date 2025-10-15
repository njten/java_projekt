package entities;

import core.GameObject;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Platform extends GameObject {
    public Platform(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    @Override
    public void render(GraphicsContext gc) {
        gc.setFill(Color.LIGHTGREEN);
        gc.fillRect(x, y, width, height);
        gc.setStroke(Color.DARKGREEN);
        gc.strokeRect(x, y, width, height);
    }

    public boolean isPlayerOnTop(Player p) {
        return p.getBottom() >= getTop() - 5 &&
                p.getBottom() <= getTop() + 10 &&
                p.getRight() > getLeft() &&
                p.getLeft() < getRight();
    }
}