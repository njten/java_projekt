package core;

import entities.FinishLine;
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
    private FinishLine finishLine;

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
                    String token = tokens[col].trim();
                    if (token.isEmpty()) continue;

                    int val = Integer.parseInt(token);
                    int x = col * TILE_SIZE;
                    int y = row * TILE_SIZE;

                    if (val == 2) {
                        // Čtverec
                        cachedPlatforms.add(new Platform(x, y, TILE_SIZE, TILE_SIZE));
                    }
                    else if (val == 1) {
                        // Trojúhelník
                        cachedObstacles.add(new Obstacle(x, y, TILE_SIZE, TILE_SIZE, false, 1));
                    }
                    else if (val == 3) {
                        // Dva malé čtverce nahoře
                        int smallSize = TILE_SIZE / 2;
                        cachedPlatforms.add(new Platform(x, y, smallSize, smallSize));
                        cachedPlatforms.add(new Platform(x + smallSize, y, smallSize, smallSize));
                    }
                    else if (val == 4) {
                        // Tři malé ostny dole
                        cachedObstacles.add(new Obstacle(x, y, TILE_SIZE, TILE_SIZE, false, 3));
                    }
                    else if (val == 5) {
                        // Obrácený trojúhelník
                        cachedObstacles.add(new Obstacle(x, y, TILE_SIZE, TILE_SIZE, true, 1));
                    }
                    // 0 - nic
                }
                row++;
            }

            this.width = maxCols * TILE_SIZE;
            this.height = row * TILE_SIZE;
            this.finishLine = new FinishLine(this.width, 800);

            int floorY = 9 * TILE_SIZE;
            cachedPlatforms.add(new Platform(-500, floorY, this.width + 2000, 200));

        } catch (Exception e) {
            System.err.println("Chyba při načítání levelu: " + levelFile);
            e.printStackTrace();
        }
    }

    public List<Obstacle> generateObstacles() { return cachedObstacles; }
    public List<Platform> generatePlatforms() { return cachedPlatforms; }
    public FinishLine getFinishLine() { return finishLine; }
    public int getPlayerStartX() { return playerStartX; }
    public int getPlayerStartY() { return playerStartY; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
}