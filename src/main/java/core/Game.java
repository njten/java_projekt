package core;

import entities.Checkpoint;
import entities.FinishLine;
import entities.Player;
import entities.Obstacle;
import entities.Platform;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

public class Game implements Serializable {
    private Player player;
    private List<Obstacle> obstacles = new ArrayList<>();
    private List<Platform> platforms = new ArrayList<>();
    private List<Checkpoint> checkpoints = new ArrayList<>();
    private Checkpoint lastCheckpoint = null;

    private FinishLine finishLine;
    private boolean gameWon = false;

    private boolean gameOver = false;
    private boolean paused = false;
    private Level level;

    private double speedMultiplier = 1.0;
    private int slowMotionFrames = 0;

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
        this.gameWon = false;
        this.paused = false;
        this.cameraX = 0;

        this.speedMultiplier = 1.0;
        this.slowMotionFrames = 0;

        this.checkpoints.clear();
        this.lastCheckpoint = null;

        this.finishLine = level.getFinishLine();

        player = new Player(level.getPlayerStartX(), level.getPlayerStartY(), 30, 30);

        obstacles.clear();
        obstacles.addAll(level.generateObstacles());

        platforms.clear();
        platforms.addAll(level.generatePlatforms());
    }

    public void placeCheckpoint() {
        if (!gameOver && !paused && !gameWon) {
            Checkpoint cp = new Checkpoint(player.getX(), player.getY());
            checkpoints.add(cp);
            lastCheckpoint = cp;
        }
    }

    private void handleDeath() {
        if (lastCheckpoint != null) {
            player.respawnAt(lastCheckpoint.getX(), lastCheckpoint.getY());
            cameraX = player.getX() - 200;

            activateSlowMotion();
        } else {
            triggerGameOver();
        }
    }

    private void activateSlowMotion() {
        speedMultiplier = 0.7;
        slowMotionFrames = 60;
    }

    private void handleWin() {
        if (!gameWon) {
            gameWon = true;
            paused = true;
            if (listeners != null) listeners.forEach(GameListener::onLevelComplete);
        }
    }

    public void update() {
        if (gameOver || paused || gameWon) return;

        if (slowMotionFrames > 0) {
            slowMotionFrames--;
            if (slowMotionFrames <= 0) {
                speedMultiplier = 1.0;
            }
        }

        player.update(speedMultiplier);

        if (player.intersects(finishLine)) {
            handleWin();
            return;
        }

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
                    handleDeath();
                    return;
                }
            }
        }

        for (Obstacle obstacle : obstacles) {
            if (obstacle.intersects(player)) {
                handleDeath();
                return;
            }
        }

        if (player.getY() > 800) {
            handleDeath();
        }
    }

    public void render(GraphicsContext gc) {
        gc.save();
        gc.setTransform(1, 0, 0, 1, 0, 0);
        gc.clearRect(0, 0, 1200, 800);

        gc.setFill(Color.DARKCYAN);
        gc.fillRect(0, 0, 1200, 800);
        gc.restore();

        cameraX = player.getX() - 200;

        gc.save();
        gc.translate(-cameraX, 0);

        platforms.forEach(platform -> platform.render(gc));
        obstacles.forEach(obstacle -> obstacle.render(gc));
        checkpoints.forEach(cp -> cp.render(gc));

        if (finishLine != null) finishLine.render(gc);

        player.render(gc);

        gc.restore();

        if (speedMultiplier < 1.0) {
            gc.setFill(Color.CYAN);
            gc.setFont(Font.font("Arial", FontWeight.BOLD, 15));
            gc.fillText("SLOW MOTION", 20, 30);
        }

        if (gameOver) {
            gc.setFill(Color.WHITE);
            gc.setFont(Font.font("Arial", FontWeight.BOLD, 50));
            gc.fillText("GAME OVER", 350, 300);
        }

        if (gameWon) {
            gc.setFill(Color.LIME);
            gc.setStroke(Color.BLACK);
            gc.setLineWidth(2);
            gc.setFont(Font.font("Arial", FontWeight.BOLD, 60));

            String text = "LEVEL COMPLETE!";
            gc.fillText(text, 250, 300);
            gc.strokeText(text, 250, 300);

            gc.setFont(Font.font("Arial", 30));
            gc.setFill(Color.WHITE);
            gc.fillText("Press ESC to exit", 380, 360);
        }
    }

    public void playerJump() {
        if (!gameOver && !paused && !gameWon) player.jump();
    }

    public void pause() { if (!gameWon) paused = true; }
    public void resume() { if (!gameWon) paused = false; }
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