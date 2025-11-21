package core;

import ui.StartScreenController.Difficulty;
import java.io.*;
import java.util.EnumMap;
import java.util.Map;
import java.util.Properties;

public class ScoreManager {
    private static final String SCORE_FILE = "scores.properties";
    private final EnumMap<Difficulty, Integer> highScores;

    public ScoreManager() {
        highScores = new EnumMap<>(Difficulty.class);
        loadHighScores();
    }

    private void loadHighScores() {
        Properties props = new Properties();
        File scoreFile = new File(SCORE_FILE);
        if (!scoreFile.exists()) {
            initializeEmptyScores();
            return;
        }

        try (InputStream input = new FileInputStream(scoreFile)) {
            props.load(input);
            for (Difficulty d : Difficulty.values()) {
                if (d != Difficulty.ENDLESS) {
                    String score = props.getProperty(d.name());
                    highScores.put(d, score != null ? Integer.parseInt(score) : 0);
                }
            }
        } catch (IOException e) {
            System.err.println("Chyba při načítání skóre: " + e.getMessage());
            initializeEmptyScores();
        }
    }

    private void initializeEmptyScores() {
        for (Difficulty d : Difficulty.values()) {
            if (d != Difficulty.ENDLESS) {
                highScores.put(d, 0);
            }
        }
    }

    public void saveHighScores() {
        Properties props = new Properties();
        for (Map.Entry<Difficulty, Integer> entry : highScores.entrySet()) {
            props.setProperty(entry.getKey().name(), entry.getValue().toString());
        }

        try (OutputStream output = new FileOutputStream(SCORE_FILE)) {
            props.store(output, "High Scores");
        } catch (IOException e) {
            System.err.println("Chyba při ukládání skóre: " + e.getMessage());
        }
    }

    public int getHighScore(Difficulty difficulty) {
        return highScores.getOrDefault(difficulty, 0);
    }

    public boolean updateHighScore(Difficulty difficulty, int newScore) {
        if (newScore > getHighScore(difficulty)) {
            highScores.put(difficulty, newScore);
            saveHighScores();
            return true;
        }
        return false;
    }
}