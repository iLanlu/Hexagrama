package model;

import java.util.Random;

/**
 *
 * @author DELL
 */
public class WaveManager {

    private Enemy[][] enemyGrid; 
    private Random random;

    public WaveManager() {
        enemyGrid = new Enemy[3][3];
        random = new Random();
    }

    public void generateWave() {
        int enemyCount = 1 + random.nextInt(9); 
        int filled = 0;

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                if (filled < enemyCount) {
                    String name = "Enemigo " + (filled + 1);
                    int health = 50 + random.nextInt(51);
                    int damage = 5 + random.nextInt(11);
                    enemyGrid[row][col] = new Enemy(name, health, damage);
                    filled++;
                } else {
                    enemyGrid[row][col] = null; 
                }
            }
        }
    }

    public Enemy[][] getEnemyGrid() {
        return enemyGrid;
    }

    public boolean isWaveDefeated() {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                Enemy enemy = enemyGrid[row][col];
                if (enemy != null && enemy.getHealth() > 0) {
                    return false;
                }
            }
        }
        return true;
    }

    public void printGridStatus() {
        System.out.println("Estado de la oleada:");
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                Enemy enemy = enemyGrid[row][col];
                if (enemy != null) {
                    System.out.print("[" + enemy.getName() + " HP:" + enemy.getHealth() + "]\t");
                } else {
                    System.out.print("[VACÍO]\t");
                }
            }
            System.out.println();
        }
    }
}
