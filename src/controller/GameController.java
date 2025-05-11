package controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import model.*;

/**
 *
 * @author DELL
 */
public class GameController {

    private Player player;
    private WaveManager wave;
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

    public void run() {
        int opcion = 0;
        while (opcion != 4) {
            System.out.println("==== MENÚ PRINCIPAL ====");
            System.out.println("1. Nuevo Juego");
            System.out.println("2. Cargar Partida");
            System.out.println("3. Créditos");
            System.out.println("4. Salir");
            System.out.print("Selecciona una opción: ");
            opcion = Integer.parseInt(scanner.nextLine());

            switch (opcion) {
                case 1:
                    resetJuego();
                    startGame();
                    break;
                case 2:
                    cargarPartida();
                    break;
                case 3:
                    mostrarCreditos();
                    break;
                case 4:
                    System.out.println("Saliendo del juego...");
                    break;
                default:
                    System.out.println("Opción inválida.");
            }
        }
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

    private void resetJuego() {
        history = new ActionHistory();
        map = new MapGraph();
        inventory = new Inventory();
        battleQueue = new BattleQueue();
        skillTree = new SkillTree("Habilidad raíz");
        player = null;
        wave = null;
    }

    private void cargarPartida() {
        configurarMapa();
        poblarInventario();
        poblarHabilidades();
        crearJugador();

        String zonaActual = "Sombra del inicio";
        Set<String> zonasVisitadas = new HashSet<>();
        zonasVisitadas.add(zonaActual);

        while (true) {
            System.out.println("\nEstás en: " + zonaActual);
            MapNode actual = map.getZone(zonaActual);

            System.out.println("Zonas disponibles:");
            int i = 1;
            Map<Integer, String> opciones = new HashMap<>();
            for (MapNode vecina : actual.getNeighbors()) {
                if (zonasVisitadas.contains(vecina.getName())) {
                    opciones.put(i, vecina.getName());
                    System.out.println(i + ". " + vecina.getName());
                    i++;
                }
            }
            System.out.println(i + ". Ver inventario");
            System.out.println((i + 1) + ". Volver al menú principal");

            int eleccion = scanner.nextInt();
            scanner.nextLine();

            if (eleccion == i) {
                System.out.println("");;
            } else if (eleccion == i + 1) {
                break;
            } else if (opciones.containsKey(eleccion)) {
                zonaActual = opciones.get(eleccion);
            } else {
                System.out.println("Opción inválida.");
            }
        }
    }

    private void loopPrincipal() {
        boolean jugando = true;
        wave.generateWave(); // Genera la nueva oleada
        Enemy[][] enemies = wave.getEnemyGrid();

        // Añadir al jugador y enemigos vivos a la cola
        battleQueue.addCharacter(player.getName());
        for (int fila = 0; fila < 3; fila++) {
            for (int col = 0; col < 3; col++) {
                Enemy e = enemies[fila][col];
                if (e != null && e.getHealth() > 0) {
                    battleQueue.addCharacter(e.getName());
                }
            }
        }

        while (jugando && player.getHealth() > 0 && !wave.isWaveDefeated()) {
            String actual = battleQueue.nextTurn();
            System.out.println("\nTurno de: " + actual);

            if (actual.equals(player.getName())) {
                turnoJugador(enemies);
                System.out.println("Vida de " + player.getName() + ": " + player.getHealth());
            } else {
                Enemy enemigo = buscarEnemigoPorNombre(actual, enemies);
                if (enemigo != null && enemigo.getHealth() > 0) {
                    turnoEnemigoIndividual(enemigo);
                }

                // Verificamos si termina la ronda de enemigos (es decir, que sigue el jugador en la cola)
                if (battleQueue.peekNext().equals(player.getName())) {
                    System.out.println("Vida restante del jugador: " + player.getHealth());
                    history.record("Vida del jugador después de ataques enemigos: " + player.getHealth());
                }
            }

            wave.printGridStatus();
        }

        if (player.getHealth() <= 0) {
            System.out.println("¡Has sido derrotado!");
        } else {
            System.out.println("¡Has vencido a todos los enemigos!");
        }
    }

    private void mostrarCreditos() {
        System.out.println("\n=== Créditos ===");
        System.out.println("Juego desarrollado por Lanlux");
        System.out.println("Gracias por jugar Hexagrama!\n");
    }

    private void crearJugador() {
        System.out.print("Ingresa el nombre de tu personaje: ");
        String nombre = scanner.nextLine();
        player = new Player(nombre);
        player.addAttack(new Attack("Espada de Luz", 25, "Ataque brillante", 1));
        player.addAttack(new Attack("Flecha Sombra", 15, "Ataque a distancia", 2));
        System.out.println("Jugador " + nombre + " creado con éxito.");
    }

    private void crearEnemigo() {
        wave = new WaveManager();
    }

    private void configurarMapa() {
        map.addZone("Sombra del inicio");
        map.addZone("Torres del juicio");
        map.addZone("Santuario del caos");
        map.addZone("Espejo Astral");
        map.addZone("El vacio del alma");
        map.addZone("Corona del hexagrama");
        map.connectZones("Sombra del inicio", "Torres del juicio");
        map.connectZones("Torres del juicio", "Santuario del caos");
        map.connectZones("Santuario del caos", "Espejo Astral");
        map.connectZones("Espejo Astral", "El vacio del alma");
        map.connectZones("El vacio del alma", "Corona del hexagrama");
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

    private void turnoJugador(Enemy[][] enemies) {
        System.out.println("Elige una acción:");
        System.out.println("1. Atacar");
        System.out.println("2. Usar poción");

        int accion = Integer.parseInt(scanner.nextLine());

        if (accion == 2) {
            if (inventory.hasItem("Poción")) {
                inventory.useItem("Poción");
                player.heal(20);
                int restantes = inventory.getItemCount("Poción");
                System.out.println("Usaste una poción. Vida actual: " + player.getHealth());
                System.out.println("Pociones restantes: " + restantes);
                history.record("Jugador usó una poción. Vida actual: " + player.getHealth() + ". Pociones restantes: " + restantes);
            } else {
                System.out.println("No tienes pociones.");
            }
        }

        System.out.println("Elige la posición del enemigo que quieres atacar (fila y columna):");
        wave.printGridStatus();

        System.out.print("Fila (0, 1 o 2): ");
        int fila = Integer.parseInt(scanner.nextLine());

        System.out.print("Columna (0, 1 o 2): ");
        int columna = Integer.parseInt(scanner.nextLine());

        if (fila >= 0 && fila < 3 && columna >= 0 && columna < 3) {
            Enemy objetivo = enemies[fila][columna];

            if (objetivo != null && objetivo.getHealth() > 0) {
                System.out.println("Elige un ataque:");
                ArrayList<Attack> ataques = player.getAttacks();
                for (int i = 0; i < ataques.size(); i++) {
                    System.out.println((i + 1) + ". " + ataques.get(i));
                }

                int eleccion = Integer.parseInt(scanner.nextLine());

                if (eleccion >= 1 && eleccion <= ataques.size()) {
                    Attack ataque = ataques.get(eleccion - 1);
                    int vidaAntes = objetivo.getHealth();
                    int totalDamage = ataque.use(objetivo);

                    history.record("Jugador usó " + ataque.getName() + " contra " + objetivo.getName() + " e hizo " + totalDamage + " de daño.");
                    System.out.println("Daño total causado: " + totalDamage);

                    // Si el enemigo murió, damos una poción
                    if (objetivo.getHealth() <= 0 && vidaAntes > 0) {
                        System.out.println("¡Has derrotado a " + objetivo.getName() + "!");
                        inventory.addItem("Poción", "Recupera 20 HP");
                        System.out.println("Has recibido una poción como recompensa.");
                    }

                } else {
                    System.out.println("Opción de ataque no válida.");
                }
            } else {
                System.out.println("No hay enemigo vivo en esa posición.");
            }
        } else {
            System.out.println("Posición inválida.");
        }
    }

    private Enemy buscarEnemigoPorNombre(String name, Enemy[][] enemies) {
        for (int fila = 0; fila < enemies.length; fila++) {
            for (int col = 0; col < enemies[fila].length; col++) {
                Enemy e = enemies[fila][col];
                if (e != null && e.getName().equals(name)) {
                    return e;
                }
            }
        }
        return null;
    }

    private void turnoEnemigoIndividual(Enemy enemy) {
        int damage = enemy.getDamage();
        System.out.println(enemy.getName() + " ataca a " + player.getName() + " causando " + damage + " de daño.");
        player.takeDamage(damage);
        history.record(enemy.getName() + " atacó a " + player.getName() + " e hizo " + damage + " de daño.");
    }

}
