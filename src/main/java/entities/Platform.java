package entities;

import core.GameObject;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Platform extends GameObject {
    private static final long serialVersionUID = 1L;

    public Platform(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    @Override
    public void render(GraphicsContext gc) {
        gc.setFill(Color.CYAN);
        gc.fillRect(x, y, width, height);

        gc.setStroke(Color.DARKCYAN);
        gc.strokeRect(x, y, width, height);

        gc.setStroke(Color.WHITE);
        gc.setLineWidth(1);
        gc.strokeRect(x + 2, y + 2, width - 4, height - 4);
    }

    public boolean isPlayerOnTop(Player p) {
        return p.getBottom() >= getTop() - 5 &&
                p.getBottom() <= getTop() + 10 &&
                p.getRight() > getLeft() &&
                p.getLeft() < getRight();
    }
}