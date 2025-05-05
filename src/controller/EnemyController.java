package controller;

import java.util.ArrayList;
import model.Enemy;

/**
 *
 * @author DELL
 */
public class EnemyController {

    private ArrayList<Enemy> enemies;

    public EnemyController() {
        enemies = new ArrayList<>();
        enemies.add(new Enemy("Slime", 30, 5));
        enemies.add(new Enemy("Goblin", 50, 10));
    }

    public ArrayList<Enemy> getEnemies() {
        return enemies;
    }

    public void listEnemies() {
        System.out.println("--- Enemigos disponibles ---");
        for (Enemy e : enemies) {
            System.out.println("- " + e.getName() + " (HP: " + e.getHealth() + ")");
        }
    }
}
