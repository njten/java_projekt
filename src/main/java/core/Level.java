package core;

import entities.Obstacle;
import entities.Platform;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Level implements Serializable {
    private String levelFile;
    private int width, height;
    private int playerStartX;
    private int playerStartY;

    private static final int TILE_SIZE = 50;

    private List<Obstacle> cachedObstacles;
    private List<Platform> cachedPlatforms;

    public Level(String levelFile, int playerStartX, int playerStartY) {
        this.levelFile = levelFile;
        this.playerStartX = playerStartX;
        this.playerStartY = playerStartY;
        loadLevel();
    }

    private void loadLevel() {
        cachedObstacles = new ArrayList<>();
        cachedPlatforms = new ArrayList<>();

        try (InputStream is = getClass().getResourceAsStream(levelFile);
             BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {

            String line;
            int row = 0;
            int maxCols = 0;

            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split(",");
                maxCols = Math.max(maxCols, tokens.length);

                for (int col = 0; col < tokens.length; col++) {
                    int val = Integer.parseInt(tokens[col].trim());

                    int x = col * TILE_SIZE;
                    int y = row * TILE_SIZE;

                    if (val == 0) continue;

                    if (val == 2 || val == 3 || val == 4) {
                        cachedPlatforms.add(new Platform(x, y, TILE_SIZE, TILE_SIZE));
                    }
                    else {
                        cachedObstacles.add(new Obstacle(x, y, TILE_SIZE, TILE_SIZE));
                    }
                }
                row++;
            }

            this.width = maxCols * TILE_SIZE;
            this.height = row * TILE_SIZE;

            int floorY = 9 * TILE_SIZE;
            cachedPlatforms.add(new Platform(-500, floorY, this.width + 2000, 200));

        } catch (Exception e) {
            System.err.println("Chyba při načítání levelu: " + levelFile);
            e.printStackTrace();
        }
    }

    public List<Obstacle> generateObstacles() { return cachedObstacles; }
    public List<Platform> generatePlatforms() { return cachedPlatforms; }
    public int getPlayerStartX() { return playerStartX; }
    public int getPlayerStartY() { return playerStartY; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
}