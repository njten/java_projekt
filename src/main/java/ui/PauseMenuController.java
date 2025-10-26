package ui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class PauseMenuController {
    private Runnable onResume;
    private Runnable onRestart;
    private Runnable onExit;

    @FXML private Button resumeButton;
    @FXML private Button restartButton;
    @FXML private Button exitButton;

    public void setOnResume(Runnable r) { onResume = r; }
    public void setOnRestart(Runnable r) { onRestart = r; }
    public void setOnExit(Runnable r) { onExit = r; }

    @FXML private void handleResume() { if (onResume != null) onResume.run(); }
    @FXML private void handleRestart() { if (onRestart != null) onRestart.run(); }
    @FXML private void handleExit() { if (onExit != null) onExit.run(); }
}