package core;

import entities.Player;
import entities.Obstacle;
import entities.Platform;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

public class Game implements Serializable {
    private Player player;
    private List<Obstacle> obstacles = new ArrayList<>();
    private List<Platform> platforms = new ArrayList<>();
    private boolean gameOver = false;
    private boolean paused = false;
    private Level level;

    private transient Runnable onGameOver;

    private double cameraX = 0;

    private transient List<GameListener> listeners = new ArrayList<>();

    public void addListener(GameListener l) {
        if (listeners == null) listeners = new ArrayList<>();
        listeners.add(l);
    }

    public void setOnGameOver(Runnable r) { onGameOver = r; }

    public void initAfterLoad() {
        listeners = new ArrayList<>();
    }

    public void reset(Level level) {
        this.level = level;
        this.gameOver = false;
        this.paused = false;
        this.cameraX = 0;

        player = new Player(level.getPlayerStartX(), level.getPlayerStartY(), 40, 40);

        obstacles.clear();
        obstacles.addAll(level.generateObstacles());

        platforms.clear();
        platforms.addAll(level.generatePlatforms());
    }

    public void update() {
        if (gameOver || paused) return;

        player.update();

        boolean onAnyPlatform = false;

        for (Platform platform : platforms) {
            if (player.intersects(platform)) {

                double playerBottom = player.getY() + player.getHeight();
                double platformTop = platform.getY();
                double penetrationDepth = playerBottom - platformTop;

                boolean isFalling = player.getVelocityY() >= 0;
                boolean isTopCollision = penetrationDepth <= 30;

                if (isFalling && isTopCollision) {
                    player.landOn(platform.getTop());
                    onAnyPlatform = true;
                } else {
                    triggerGameOver();
                    return;
                }
            }
        }

        for (Obstacle obstacle : obstacles) {
            if (obstacle.intersects(player)) {
                triggerGameOver();
                return;
            }
        }

        if (player.getY() > 800) {
            triggerGameOver();
        }
    }

    public void render(GraphicsContext gc) {
        gc.save();
        gc.setTransform(1, 0, 0, 1, 0, 0);
        gc.clearRect(0, 0, 1200, 800);

        gc.setFill(Color.rgb(40, 40, 40));
        gc.fillRect(0, 0, 1200, 800);
        gc.restore();

        cameraX = player.getX() - 200;

        gc.save();
        gc.translate(-cameraX, 0);

        platforms.forEach(platform -> platform.render(gc));
        obstacles.forEach(obstacle -> obstacle.render(gc));
        player.render(gc);

        gc.restore();

        if (gameOver) {
            gc.setFill(Color.WHITE);
            gc.fillText("GAME OVER", 500 - 40, 300);
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
        if (listeners != null) listeners.forEach(GameListener::onGameOver);
        if (onGameOver != null) onGameOver.run();
    }

    public Player getPlayer() { return player; }
    public int getScore() { return player != null ? player.getX() / 50 : 0; }
}