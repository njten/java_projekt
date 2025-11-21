package core;

import entities.Player;
import entities.Obstacle;
import entities.Platform;
import javafx.scene.canvas.GraphicsContext;
import ui.StartScreenController;
import java.util.List;
import java.util.ArrayList;

public class Game {
    private Player player;
    private List<Obstacle> obstacles = new ArrayList<>();
    private List<Platform> platforms = new ArrayList<>();
    private boolean gameOver = false;
    private boolean paused = false;
    private Level level;
    private StartScreenController.Difficulty difficulty;
    private Runnable onGameOver;

    private final List<GameListener> listeners = new ArrayList<>();
    public void addListener(GameListener l) { listeners.add(l); }

    public void setOnGameOver(Runnable r) { onGameOver = r; }

    public void reset(Level level) {
        this.level = level;
        this.difficulty = null;
        this.gameOver = false;
        this.paused = false;

        player = new Player(level.getPlayerStartX(), level.getPlayerStartY(), 30, 30);

        obstacles.clear();
        obstacles.addAll(level.generateObstacles());

        platforms.clear();
        platforms.addAll(level.generatePlatforms());
    }

    public void update() {
        if (gameOver || paused) return;

        boolean onAnyPlatform = false;
        for (Platform platform : platforms) {
            if (platform.isPlayerOnTop(player)) {
                player.landOn(platform.getTop());
                onAnyPlatform = true;
                break;
            }
        }
        if (!onAnyPlatform) {
            player.update();
        } else {
            player.updateHorizontal();
        }

        for (Obstacle obstacle : obstacles) {
            if (player.intersects(obstacle)) {
                triggerGameOver();
                break;
            }
        }
    }

    public void render(GraphicsContext gc) {
        gc.clearRect(0, 0, level.getWidth(), level.getHeight());

        platforms.forEach(platform -> platform.render(gc));
        player.render(gc);
        obstacles.forEach(obstacle -> obstacle.render(gc));

        if (gameOver) {
            gc.setFill(javafx.scene.paint.Color.BLACK);
            gc.fillText("GAME OVER", level.getWidth() / 2.0 - 40, level.getHeight() / 2.0);
        }
    }

    public void playerJump() {
        if (!gameOver && !paused) player.jump();
    }

    public void pause() { paused = true; }
    public void resume() { paused = false; }

    public boolean isPaused() { return paused; }
    public boolean isGameOver() { return gameOver; }

    private void triggerGameOver() {
        gameOver = true;
        listeners.forEach(GameListener::onGameOver);
        if (onGameOver != null) onGameOver.run();
    }

    public Player getPlayer() { return player; }
    public int getScore() { return player != null ? player.getX() : 0; }
    public List<Obstacle> getObstacles() { return obstacles; }
    public List<Platform> getPlatforms() { return platforms; }
    public Level getLevel() { return level; }
}