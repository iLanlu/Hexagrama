package controller;

import java.util.Scanner;
import model.Enemy;

/**
 *
 * @author DELL
 */
public class CombatController {

    public void startCombat(EnemyController enemyController) {
        Scanner scanner = new Scanner(System.in);
        enemyController.listEnemies();

        System.out.print("¿A cuál enemigo quieres atacar? ");
        String name = scanner.nextLine();

        Enemy enemy = null;
        for (Enemy e : enemyController.getEnemies()) {
            if (e.getName().equalsIgnoreCase(name)) {
                enemy = e;
                break;
            }
        }

        if (enemy != null) {
            enemy.takeDamage(20);
            System.out.println("Atacaste a " + enemy.getName() + ". HP restante: " + enemy.getHealth());
        } else {
            System.out.println("Ese enemigo no existe.");
        }
    }
}
