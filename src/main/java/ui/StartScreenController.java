package ui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class StartScreenController {
    public enum Difficulty { EASY, MEDIUM, HARD, ENDLESS }

    private Difficulty selectedDifficulty;
    private Stage primaryStage;
    private OnStartGameListener startGameListener;

    @FXML private Button easyButton;
    @FXML private Button mediumButton;
    @FXML private Button hardButton;
    @FXML private Button endlessButton;
    @FXML private Button exitButton;

    public interface OnStartGameListener {
        void onStartGame(Difficulty difficulty);
    }

    public void setOnStartGameListener(OnStartGameListener listener) {
        this.startGameListener = listener;
    }

    public void setPrimaryStage(Stage stage) {
        this.primaryStage = stage;
    }

    @FXML
    private void handleEasy() {
        selectedDifficulty = Difficulty.EASY;
        if (startGameListener != null) startGameListener.onStartGame(selectedDifficulty);
    }

    @FXML
    private void handleMedium() {
        selectedDifficulty = Difficulty.MEDIUM;
        if (startGameListener != null) startGameListener.onStartGame(selectedDifficulty);
    }

    @FXML
    private void handleHard() {
        selectedDifficulty = Difficulty.HARD;
        if (startGameListener != null) startGameListener.onStartGame(selectedDifficulty);
    }

    @FXML
    private void handleEndless() {
        selectedDifficulty = Difficulty.ENDLESS;
        if (startGameListener != null) startGameListener.onStartGame(selectedDifficulty);
    }

    @FXML
    private void handleExit() {
        if (primaryStage != null) primaryStage.close();
    }
}