package entities;

import core.GameObject;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import java.io.Serializable;

public class Obstacle extends GameObject implements Serializable {

    public Obstacle(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    @Override
    public void render(GraphicsContext gc) {
        double[] xPoints = { x, x + width / 2.0, x + width };
        double[] yPoints = { y + height, y, y + height };

        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);
        gc.strokePolygon(xPoints, yPoints, 3);

        gc.setFill(Color.rgb(50, 50, 50));
        gc.fillPolygon(xPoints, yPoints, 3);

        double[] highlightX = { x + width/2.0, x + width/2.0 + 5, x + width/2.0 - 5 };
        double[] highlightY = { y + 5, y + height - 5, y + height - 5 };
        gc.setFill(Color.rgb(100, 100, 100));
        gc.fillPolygon(highlightX, highlightY, 3);
    }

    @Override
    public boolean intersects(GameObject other) {
        if (super.intersects(other)) {
            if (!(other instanceof Player)) {
                return true;
            }

            Player p = (Player) other;

            double ax = x;
            double ay = y + height;
            double bx = x + width / 2.0;
            double by = y;
            double cx = x + width;
            double cy = y + height;

            double pLeft = p.getX() + 5;
            double pRight = p.getX() + p.getWidth() - 5;
            double pTop = p.getY() + 5;
            double pBottom = p.getY() + p.getHeight() - 5;

            if (isPointInTriangle(pLeft, pBottom, ax, ay, bx, by, cx, cy)) return true;
            if (isPointInTriangle(pRight, pBottom, ax, ay, bx, by, cx, cy)) return true;
            if (isPointInTriangle(pLeft, pTop, ax, ay, bx, by, cx, cy)) return true;
            if (isPointInTriangle(pRight, pTop, ax, ay, bx, by, cx, cy)) return true;
            if (isPointInTriangle(pLeft + (pRight-pLeft)/2, pBottom, ax, ay, bx, by, cx, cy)) return true;

            return false;
        }
        return false;
    }

    private boolean isPointInTriangle(double px, double py, double ax, double ay, double bx, double by, double cx, double cy) {
        double areaOrig = Math.abs((bx - ax) * (cy - ay) - (cx - ax) * (by - ay));
        double area1 = Math.abs((ax - px) * (by - py) - (bx - px) * (ay - py));
        double area2 = Math.abs((bx - px) * (cy - py) - (cx - px) * (by - py));
        double area3 = Math.abs((cx - px) * (ay - py) - (ax - px) * (cy - py));
        return Math.abs(area1 + area2 + area3 - areaOrig) < 1.0;
    }
}