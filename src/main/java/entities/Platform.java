package entities;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Platform {
    private int x, y;
    private int width, height;

    public Platform(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void render(GraphicsContext gc) {
        gc.setFill(Color.LIGHTGREEN);
        gc.fillRect(x, y, width, height);
        gc.setStroke(Color.DARKGREEN);
        gc.strokeRect(x, y, width, height);
    }

    public int getLeft()   { return x; }
    public int getRight()  { return x + width; }
    public int getTop()    { return y; }
    public int getBottom() { return y + height; }

    public boolean isPlayerOnTop(Player p) {
        return p.getBottom() >= getTop() - 5 &&
                p.getBottom() <= getTop() + 10 &&
                p.getRight() > getLeft() &&
                p.getLeft() < getRight();
    }
}