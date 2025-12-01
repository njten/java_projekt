package entities;

import core.GameObject;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import java.io.Serializable;

public class Checkpoint extends GameObject implements Serializable {

    public Checkpoint(int x, int y) {
        super(x, y, 30, 40);
    }

    @Override
    public void render(GraphicsContext gc) {
        gc.setFill(Color.LIMEGREEN);

        double[] xPoints = { x + width/2.0, x + width, x + width/2.0, x };
        double[] yPoints = { y, y + height/2.0, y + height, y + height/2.0 };

        gc.fillPolygon(xPoints, yPoints, 4);

        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);
        gc.strokePolygon(xPoints, yPoints, 4);
    }
}