package ui;

import core.ScoreManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class StartScreenController {
    public enum Difficulty { EASY, MEDIUM, HARD, ENDLESS }

    private Stage primaryStage;
    private OnStartGameListener startGameListener;
    private ScoreManager scoreManager;
    private Runnable onLoadGameListener;

    @FXML private Button easyButton;
    @FXML private Button mediumButton;
    @FXML private Button hardButton;
    @FXML private Button exitButton;
    @FXML private Button loadButton;

    @FXML private Label easyScoreLabel;
    @FXML private Label mediumScoreLabel;
    @FXML private Label hardScoreLabel;


    public interface OnStartGameListener {
        void onStartGame(Difficulty difficulty);
    }

    public void setOnStartGameListener(OnStartGameListener listener) {
        this.startGameListener = listener;
    }

    public void setOnLoadGameListener(Runnable listener) {
        this.onLoadGameListener = listener;
    }

    public void setPrimaryStage(Stage stage) {
        this.primaryStage = stage;
    }

    public void setScoreManager(ScoreManager scoreManager) {
        this.scoreManager = scoreManager;
        updateProgressLabels();
    }

    public void updateProgressLabels() {
        if (scoreManager != null) {
            easyScoreLabel.setText("Progress: " + scoreManager.getUserProgress(Difficulty.EASY) + "%");
            mediumScoreLabel.setText("Progress: " + scoreManager.getUserProgress(Difficulty.MEDIUM) + "%");
            hardScoreLabel.setText("Progress: " + scoreManager.getUserProgress(Difficulty.HARD) + "%");
        }
    }

    public void displayHighScores() {
        updateProgressLabels();
    }

    @FXML
    private void handleEasy() {
        if (startGameListener != null) startGameListener.onStartGame(Difficulty.EASY);
    }

    @FXML
    private void handleMedium() {
        if (startGameListener != null) startGameListener.onStartGame(Difficulty.MEDIUM);
    }

    @FXML
    private void handleHard() {
        if (startGameListener != null) startGameListener.onStartGame(Difficulty.HARD);
    }

    @FXML
    private void handleLoadGame() {
        if (onLoadGameListener != null) onLoadGameListener.run();
    }

    @FXML
    private void handleExit() {
        if (primaryStage != null) primaryStage.close();
    }
}