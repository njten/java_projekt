package core;

import javafx.scene.canvas.GraphicsContext;
import java.io.Serializable;

public abstract class GameObject implements Serializable {
    protected int x, y, width, height;

    public GameObject(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public abstract void render(GraphicsContext gc);

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getLeft() {
        return x;
    }

    public int getRight() {
        return x + width;
    }

    public int getTop() {
        return y;
    }

    public int getBottom() {
        return y + height;
    }

    public boolean intersects(GameObject other) {
        return !(getRight() < other.getLeft() ||
                getLeft() > other.getRight() ||
                getBottom() < other.getTop() ||
                getTop() > other.getBottom());
    }
}