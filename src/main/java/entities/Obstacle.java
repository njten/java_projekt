package entities;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Obstacle {
    private int x, y;
    private int width, height;

    public Obstacle(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    // obstacle zůstávají na místě, není potřeba update

    public void render(GraphicsContext gc) {
        gc.setFill(Color.DARKORANGE);
        double[] xPoints = {x, x + width / 2.0, x + width};
        double[] yPoints = {y + height, y, y + height};
        gc.fillPolygon(xPoints, yPoints, 3);

        gc.setStroke(Color.BLACK);
        gc.strokePolygon(xPoints, yPoints, 3);
    }

    public int getLeft()   { return x; }
    public int getRight()  { return x + width; }
    public int getTop()    { return y; }
    public int getBottom() { return y + height; }

    public boolean intersects(Player p) {
        return !(getRight() < p.getLeft() ||
                getLeft() > p.getRight() ||
                getBottom() < p.getTop() ||
                getTop() > p.getBottom());
    }
}