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

    @FXML private Button easyButton;
    @FXML private Button mediumButton;
    @FXML private Button hardButton;
    @FXML private Button exitButton;
    @FXML private Label easyScoreLabel;
    @FXML private Label mediumScoreLabel;
    @FXML private Label hardScoreLabel;


    public interface OnStartGameListener {
        void onStartGame(Difficulty difficulty);
    }

    public void setOnStartGameListener(OnStartGameListener listener) {
        this.startGameListener = listener;
    }

    public void setPrimaryStage(Stage stage) {
        this.primaryStage = stage;
    }

    public void setScoreManager(ScoreManager scoreManager) {
        this.scoreManager = scoreManager;
    }

    public void displayHighScores() {
        if (scoreManager != null) {
            easyScoreLabel.setText("Nejvyšší skóre: " + scoreManager.getHighScore(Difficulty.EASY));
            mediumScoreLabel.setText("Nejvyšší skóre: " + scoreManager.getHighScore(Difficulty.MEDIUM));
            hardScoreLabel.setText("Nejvyšší skóre: " + scoreManager.getHighScore(Difficulty.HARD));
        }
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
    private void handleExit() {
        if (primaryStage != null) primaryStage.close();
    }
}