package entities;

import core.GameObject;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import java.io.Serializable;

public class Obstacle extends GameObject implements Serializable {

    private boolean inverted;
    private int spikeCount;
    private boolean isSquare;

    public Obstacle(int x, int y, int width, int height, boolean inverted, int spikeCount, boolean isSquare) {
        super(x, y, width, height);
        this.inverted = inverted;
        this.spikeCount = spikeCount;
        this.isSquare = isSquare;
    }

    public Obstacle(int x, int y, int width, int height, boolean inverted, int spikeCount) {
        this(x, y, width, height, inverted, spikeCount, false);
    }

    @Override
    public void render(GraphicsContext gc) {
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1);

        double subWidth = width / (double) spikeCount;
        double subHeight = isSquare ? subWidth : ((spikeCount > 1) ? height / 2.0 : height);

        for (int i = 0; i < spikeCount; i++) {
            double offsetX = x + (i * subWidth);
            double offsetY;

            if (inverted) {
                offsetY = y;
            } else {
                offsetY = y + height - subHeight;
            }

            if (isSquare) {
                gc.setFill(Color.rgb(50, 50, 50));
                gc.fillRect(offsetX, offsetY, subWidth, subHeight);
                gc.strokeRect(offsetX, offsetY, subWidth, subHeight);

                gc.setFill(Color.rgb(80, 80, 80));
                gc.fillRect(offsetX + 5, offsetY + 5, subWidth - 10, subHeight - 10);
            }
            else {
                double[] xPoints;
                double[] yPoints;

                if (inverted) {
                    xPoints = new double[]{ offsetX, offsetX + subWidth / 2.0, offsetX + subWidth };
                    yPoints = new double[]{ offsetY, offsetY + subHeight, offsetY };
                } else {
                    double baseLine = y + height;
                    xPoints = new double[]{ offsetX, offsetX + subWidth / 2.0, offsetX + subWidth };
                    yPoints = new double[]{ baseLine, baseLine - subHeight, baseLine };
                }

                gc.setFill(Color.rgb(50, 50, 50));
                gc.fillPolygon(xPoints, yPoints, 3);
                gc.strokePolygon(xPoints, yPoints, 3);

                if (spikeCount == 1) {
                    double[] hX = { offsetX + subWidth/2.0, offsetX + subWidth/2.0 + 5, offsetX + subWidth/2.0 - 5 };
                    double[] hY = inverted
                            ? new double[]{ y + subHeight - 5, y + 5, y + 5 }
                            : new double[]{ y + height - 5, y + height - subHeight + 5, y + height - subHeight + 5 };

                    gc.setFill(Color.rgb(100, 100, 100));
                    gc.fillPolygon(hX, hY, 3);
                }
            }
        }
    }

    @Override
    public boolean intersects(GameObject other) {
        if (super.intersects(other)) {
            if (!(other instanceof Player)) return true;
            Player p = (Player) other;

            double subWidth = width / (double) spikeCount;
            double subHeight = isSquare ? subWidth : ((spikeCount > 1) ? height / 2.0 : height);

            for (int i = 0; i < spikeCount; i++) {
                double offsetX = x + (i * subWidth);
                double offsetY = inverted ? y : (y + height - subHeight);

                if (isSquare) {
                    if (p.getRight() > offsetX + 2 &&
                            p.getLeft() < offsetX + subWidth - 2 &&
                            p.getBottom() > offsetY + 2 &&
                            p.getTop() < offsetY + subHeight - 2) {
                        return true;
                    }
                }
                else {
                    double ax, ay, bx, by, cx, cy;
                    if (inverted) {
                        ax = offsetX;              ay = offsetY;
                        bx = offsetX + subWidth/2; by = offsetY + subHeight;
                        cx = offsetX + subWidth;   cy = offsetY;
                    } else {
                        double baseLine = y + height;
                        ax = offsetX;              ay = baseLine;
                        bx = offsetX + subWidth/2; by = baseLine - subHeight;
                        cx = offsetX + subWidth;   cy = baseLine;
                    }

                    double pLeft = p.getX() + 5;
                    double pRight = p.getX() + p.getWidth() - 5;
                    double pTop = p.getY() + 5;
                    double pBottom = p.getY() + p.getHeight() - 5;

                    if (isPointInTriangle(pLeft, pBottom, ax, ay, bx, by, cx, cy)) return true;
                    if (isPointInTriangle(pRight, pBottom, ax, ay, bx, by, cx, cy)) return true;
                    if (isPointInTriangle(pLeft, pTop, ax, ay, bx, by, cx, cy)) return true;
                    if (isPointInTriangle(pRight, pTop, ax, ay, bx, by, cx, cy)) return true;
                    if (isPointInTriangle(pLeft + (pRight-pLeft)/2, pBottom, ax, ay, bx, by, cx, cy)) return true;
                    if (isPointInTriangle(pLeft + (pRight-pLeft)/2, pTop, ax, ay, bx, by, cx, cy)) return true;
                }
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