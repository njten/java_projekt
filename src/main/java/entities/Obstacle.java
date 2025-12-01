package entities;

import core.GameObject;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import java.io.Serializable;

public class Obstacle extends GameObject implements Serializable {

    private boolean inverted;
    private int spikeCount;

    public Obstacle(int x, int y, int width, int height, boolean inverted, int spikeCount) {
        super(x, y, width, height);
        this.inverted = inverted;
        this.spikeCount = spikeCount;
    }

    @Override
    public void render(GraphicsContext gc) {
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1);

        double subWidth = width / (double) spikeCount;
        double spikeHeight = (spikeCount > 1) ? height / 2.0 : height;

        for (int i = 0; i < spikeCount; i++) {
            double offsetX = x + (i * subWidth);
            double[] xPoints;
            double[] yPoints;

            if (inverted) {
                xPoints = new double[]{ offsetX, offsetX + subWidth / 2.0, offsetX + subWidth };
                yPoints = new double[]{ y, y + spikeHeight, y };
            } else {
                double baseLine = y + height;
                xPoints = new double[]{ offsetX, offsetX + subWidth / 2.0, offsetX + subWidth };
                yPoints = new double[]{ baseLine, baseLine - spikeHeight, baseLine };
            }

            gc.setFill(Color.rgb(50, 50, 50));
            gc.fillPolygon(xPoints, yPoints, 3);
            gc.strokePolygon(xPoints, yPoints, 3);
        }
    }

    @Override
    public boolean intersects(GameObject other) {
        if (super.intersects(other)) {
            if (!(other instanceof Player)) return true;
            Player p = (Player) other;

            double pLeft = p.getX() + 5;
            double pRight = p.getX() + p.getWidth() - 5;
            double pTop = p.getY() + 5;
            double pBottom = p.getY() + p.getHeight() - 5;

            double subWidth = width / (double) spikeCount;
            double spikeHeight = (spikeCount > 1) ? height / 2.0 : height;

            for (int i = 0; i < spikeCount; i++) {
                double offsetX = x + (i * subWidth);
                double ax, ay, bx, by, cx, cy;

                if (inverted) {
                    ax = offsetX;              ay = y;
                    bx = offsetX + subWidth/2; by = y + spikeHeight;
                    cx = offsetX + subWidth;   cy = y;
                } else {
                    double baseLine = y + height;
                    ax = offsetX;              ay = baseLine;
                    bx = offsetX + subWidth/2; by = baseLine - spikeHeight;
                    cx = offsetX + subWidth;   cy = baseLine;
                }

                if (isPointInTriangle(pLeft, pBottom, ax, ay, bx, by, cx, cy)) return true;
                if (isPointInTriangle(pRight, pBottom, ax, ay, bx, by, cx, cy)) return true;
                if (isPointInTriangle(pLeft, pTop, ax, ay, bx, by, cx, cy)) return true;
                if (isPointInTriangle(pRight, pTop, ax, ay, bx, by, cx, cy)) return true;
                if (isPointInTriangle(pLeft + (pRight-pLeft)/2, pBottom, ax, ay, bx, by, cx, cy)) return true;
                if (isPointInTriangle(pLeft + (pRight-pLeft)/2, pTop, ax, ay, bx, by, cx, cy)) return true;
            }
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