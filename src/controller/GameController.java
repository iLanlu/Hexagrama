package controller;

import java.util.ArrayList;
import java.util.Scanner;
import model.*;

/**
 *
 * @author DELL
 */
public class GameController {
    private Player player;
    private Enemy enemy;
    private MapGraph map;
    private Inventory inventory;
    private ActionHistory history;
    private BattleQueue battleQueue;
    private SkillTree skillTree;
    private Scanner scanner;

    public GameController() {
        scanner = new Scanner(System.in);
        history = new ActionHistory();
        map = new MapGraph();
        inventory = new Inventory();
        battleQueue = new BattleQueue();
        skillTree = new SkillTree("Habilidad raíz");
    }

    public void startGame() {
        System.out.println("Bienvenido a Hexagrama");
        crearJugador();
        configurarMapa();
        poblarInventario();
        poblarHabilidades();
        crearEnemigo();

        loopPrincipal();

        System.out.println("Gracias por jugar.");
    }

    private void crearJugador() {
        System.out.print("Ingresa el nombre de tu personaje: ");
        String nombre = scanner.nextLine();
        player = new Player(nombre);
        player.addAttack(new Attack("Espada de Luz", 25, "Ataque brillante", 1));
        player.addAttack(new Attack("Flecha Sombra", 15, "Ataque a distancia", 2));
        System.out.println("Jugador " + nombre + " creado con éxito.");
        battleQueue.addCharacter(player.getName());
    }

    private void crearEnemigo() {
        enemy = new Enemy("Goblin", 50, 10);
        battleQueue.addCharacter(enemy.getName());
    }

    private void configurarMapa() {
        map.addZone("Pueblo");
        map.addZone("Bosque");
        map.addZone("Castillo");
        map.connectZones("Pueblo", "Bosque");
        map.connectZones("Bosque", "Castillo");
    }

    private void poblarInventario() {
        inventory.addItem("Poción", "Recupera 20 HP");
        inventory.addItem("Mapa", "Muestra el mundo");
    }

    private void poblarHabilidades() {
        SkillNode ataqueRapido = new SkillNode("Ataque Rápido");
        SkillNode ataqueFuerte = new SkillNode("Ataque Fuerte");
        skillTree.getRoot().addChild(ataqueRapido);
        skillTree.getRoot().addChild(ataqueFuerte);
    }

    private void loopPrincipal() {
        boolean jugando = true;
        while (jugando && player.getHealth() > 0 && enemy.getHealth() > 0) {
            String actual = battleQueue.nextTurn();
            System.out.println("\nTurno de: " + actual);
            if (actual.equals(player.getName())) {
                turnoJugador();
            } else {
                turnoEnemigo();
            }
        }

        if (player.getHealth() <= 0) {
            System.out.println("¡Has sido derrotado!");
        } else if (enemy.getHealth() <= 0) {
            System.out.println("¡Has vencido al enemigo!");
        }
    }

    private void turnoJugador() {
        System.out.println("Elige un ataque:");
        ArrayList<Attack> ataques = player.getAttacks();
        for (int i = 0; i < ataques.size(); i++) {
            System.out.println((i + 1) + ". " + ataques.get(i));
        }

        int eleccion = scanner.nextInt();
        scanner.nextLine(); // limpiar buffer
        if (eleccion >= 1 && eleccion <= ataques.size()) {
            Attack ataque = ataques.get(eleccion - 1);
            ataque.use(enemy);
            history.record("Jugador usó " + ataque.getName());
        } else {
            System.out.println("Opción no válida.");
        }
    }

    private void turnoEnemigo() {
        System.out.println(enemy.getName() + " ataca a " + player.getName());
        player.takeDamage(enemy.getDamage());
        history.record("Enemigo atacó con " + enemy.getDamage() + " de daño");
    }
}
