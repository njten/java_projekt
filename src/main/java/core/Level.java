package core;

import entities.Obstacle;
import entities.Platform;
import java.util.Random;
import java.util.List;
import java.util.ArrayList;

public class Level {
    private int obstacleCount;
    private int platformCount;
    private int playerStartX;
    private int playerStartY;
    private int width, height;

    public Level(int obstacleCount, int platformCount, int playerStartX, int playerStartY, int width, int height) {
        this.obstacleCount = obstacleCount;
        this.platformCount = platformCount;
        this.playerStartX = playerStartX;
        this.playerStartY = playerStartY;
        this.width = width;
        this.height = height;
    }

    public int getObstacleCount() { return obstacleCount; }
    public int getPlatformCount() { return platformCount; }
    public int getPlayerStartX() { return playerStartX; }
    public int getPlayerStartY() { return playerStartY; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }

    public List<Obstacle> generateObstacles() {
        List<Obstacle> obstacles = new ArrayList<>();
        Random rand = new Random();
        for (int i = 0; i < obstacleCount; i++) {
            int x = 300 + rand.nextInt(Math.max(1, width - 350));
            int y = 270 + rand.nextInt(Math.max(1, height - 310));
            int obstacleWidth = 40;
            int obstacleHeight = 40;
            obstacles.add(new Obstacle(x, y, obstacleWidth, obstacleHeight));
        }
        return obstacles;
    }

    public List<Platform> generatePlatforms() {
        List<Platform> platforms = new ArrayList<>();
        Random rand = new Random();
        for (int i = 0; i < platformCount; i++) {
            int x = 100 + rand.nextInt(Math.max(1, width - 180));
            int y = 150 + rand.nextInt(Math.max(1, height - 165));
            int platformWidth = 80 + rand.nextInt(80);
            int platformHeight = 15;
            platforms.add(new Platform(x, y, platformWidth, platformHeight));
        }
        return platforms;
    }
}