package entities;

import core.GameObject;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Obstacle extends GameObject {
    public Obstacle(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    @Override
    public void render(GraphicsContext gc) {
        gc.setFill(Color.DARKORANGE);
        double[] xPoints = {x, x + width / 2.0, x + width};
        double[] yPoints = {y + height, y, y + height};
        gc.fillPolygon(xPoints, yPoints, 3);

        gc.setStroke(Color.BLACK);
        gc.strokePolygon(xPoints, yPoints, 3);
    }

    public boolean intersects(Player p) {
        return !(getRight() < p.getLeft() ||
                getLeft() > p.getRight() ||
                getBottom() < p.getTop() ||
                getTop() > p.getBottom());
    }
}