package entities;

import core.GameObject;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import java.io.Serializable;

public class FinishLine extends GameObject implements Serializable {

    public FinishLine(int x, int height) {
        super(x, 0, 20, height);
    }

    @Override
    public void render(GraphicsContext gc) {
        gc.setFill(Color.CYAN);
        gc.fillRect(x, y, width, height);

        gc.setStroke(Color.WHITE);
        gc.setLineWidth(2);
        gc.strokeRect(x, y, width, height);

        gc.setStroke(Color.BLUE);
        for (int i = 0; i < height; i += 40) {
            gc.strokeLine(x, i, x + width, i + 20);
        }
    }
}