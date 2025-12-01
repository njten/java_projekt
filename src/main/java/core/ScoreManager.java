package core;

import ui.StartScreenController.Difficulty;
import java.io.*;
import java.util.Properties;

public class ScoreManager {
    private static final String SCORE_FILE = "user_progress.properties";
    private Properties properties;
    private String currentUser;

    public ScoreManager() {
        properties = new Properties();
        loadScores();
    }

    public void setCurrentUser(String username) {
        this.currentUser = username;
    }

    private void loadScores() {
        File scoreFile = new File(SCORE_FILE);
        if (scoreFile.exists()) {
            try (InputStream input = new FileInputStream(scoreFile)) {
                properties.load(input);
            } catch (IOException e) {
                System.err.println("Chyba při načítání postupu: " + e.getMessage());
            }
        }
    }

    private void saveScores() {
        try (OutputStream output = new FileOutputStream(SCORE_FILE)) {
            properties.store(output, "User Progress (Percentage)");
        } catch (IOException e) {
            System.err.println("Chyba při ukládání postupu: " + e.getMessage());
        }
    }

    private String getKey(Difficulty difficulty) {
        if (currentUser == null || currentUser.isEmpty()) return "Guest_" + difficulty.name();
        return currentUser + "_" + difficulty.name();
    }

    public int getUserProgress(Difficulty difficulty) {
        String key = getKey(difficulty);
        String val = properties.getProperty(key);
        return val != null ? Integer.parseInt(val) : 0;
    }

    public boolean saveProgress(Difficulty difficulty, int percent) {
        int currentBest = getUserProgress(difficulty);

        if (percent > currentBest) {
            properties.setProperty(getKey(difficulty), String.valueOf(percent));
            saveScores();
            return true;
        }
        return false;
    }
}