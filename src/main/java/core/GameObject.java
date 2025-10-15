package core;

import javafx.scene.canvas.GraphicsContext;

public abstract class GameObject implements Renderable {
    protected int x, y, width, height;

    public GameObject(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public int getLeft()   { return x; }
    public int getRight()  { return x + width; }
    public int getTop()    { return y; }
    public int getBottom() { return y + height; }
}