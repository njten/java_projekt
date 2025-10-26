package core;

import entities.Player;
import entities.Obstacle;
import entities.Platform;
import javafx.scene.canvas.GraphicsContext;
import ui.StartScreenController;
import java.util.List;

public class Game {
    private Player player;
    private Obstacle[] obstacles;
    private Platform[] platforms;
    private boolean gameOver = false;
    private boolean paused = false;
    private Level level;
    private StartScreenController.Difficulty difficulty;
    private Runnable onGameOver;

    public void setOnGameOver(Runnable r) { onGameOver = r; }

    public void reset(Level level) {
        this.level = level;
        this.difficulty = null;
        this.gameOver = false;
        this.paused = false;

        player = new Player(level.getPlayerStartX(), level.getPlayerStartY(), 30, 30);

        List<Obstacle> obstaclesList = level.generateObstacles();
        obstacles = obstaclesList.toArray(new Obstacle[0]);

        List<Platform> platformsList = level.generatePlatforms();
        platforms = platformsList.toArray(new Platform[0]);
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

        for (Platform platform : platforms) {
            platform.render(gc);
        }
        player.render(gc);
        for (Obstacle obstacle : obstacles) {
            obstacle.render(gc);
        }
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
        if (onGameOver != null) onGameOver.run();
    }

    public Player getPlayer() { return player; }
    public Obstacle[] getObstacles() { return obstacles; }
    public Platform[] getPlatforms() { return platforms; }
    public Level getLevel() { return level; }
}