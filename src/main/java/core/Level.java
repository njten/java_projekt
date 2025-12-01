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
                        // 2 = Čtverec
                        cachedPlatforms.add(new Platform(x, y, TILE_SIZE, TILE_SIZE));
                    }
                    else if (val == 1) {
                        // 1 = Trojúhelník
                        cachedObstacles.add(new Obstacle(x, y, TILE_SIZE, TILE_SIZE, false, 1, false));
                    }
                    else if (val == 3) {
                        // 3 = Dva malé čtverce nahoře
                        int smallSize = TILE_SIZE / 2;
                        // Levý čtverec
                        cachedPlatforms.add(new Platform(x, y, smallSize, smallSize));
                        // Pravý čtverec
                        cachedPlatforms.add(new Platform(x + smallSize, y, smallSize, smallSize));
                    }
                    else if (val == 4) {
                        // 4 = Tři malé ostny
                        cachedObstacles.add(new Obstacle(x, y, TILE_SIZE, TILE_SIZE, false, 3, false));
                    }
                    else if (val == 5) {
                        // 5 = Obrácený trojúhelník
                        cachedObstacles.add(new Obstacle(x, y, TILE_SIZE, TILE_SIZE, true, 1, false));
                    }
                    // 0 a cokoliv jiného je vzduch
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