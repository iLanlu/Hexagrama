package controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
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
    private SkillTreeManager skillTree;
    private Scanner scanner;

    private String zonaActual;
    private Set<String> zonasDesbloqueadas;
    private String ultimaZonaDesbloqueada;

    public GameController() {
        scanner = new Scanner(System.in);
        history = new ActionHistory();
        map = new MapGraph();
        inventory = new Inventory();
        battleQueue = new BattleQueue();
        skillTree = new SkillTreeManager();
        zonasDesbloqueadas = new HashSet<>();
        wave = new WaveManager();
        this.ultimaZonaDesbloqueada = "Sombra del inicio"; // El inicio
        this.zonasDesbloqueadas.add(ultimaZonaDesbloqueada);
    }

    public void run() {
        int opcion = 0;
        while (opcion != 4) {
            System.out.println("==== MENÚ PRINCIPAL ====");
            System.out.println("1. Nuevo Juego");
            System.out.println("2. Créditos");
            System.out.println("3. Salir");
            System.out.print("Selecciona una opción: ");
            try {
                opcion = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Opción inválida.");
                continue;
            }

            switch (opcion) {
                case 1:
                    resetJuego();
                    startGame();
                    break;
                case 2:
                    mostrarCreditos();
                    break;
                case 3:
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

        zonaActual = "Sombra del inicio";
        zonasDesbloqueadas.add(zonaActual);

        wave = new WaveManager();
        loopPrincipal(); // Primera batalla

        // Al terminar la primera batalla, se desbloquea la siguiente zona
        zonasDesbloqueadas.add("Torres del juicio");
        mostrarMapa();
    }

    private void resetJuego() {
        history = new ActionHistory();
        map = new MapGraph();
        inventory = new Inventory();
        battleQueue = new BattleQueue();
        skillTree = new SkillTreeManager();
        zonasDesbloqueadas = new HashSet<>();
        player = null;
        wave = new WaveManager();
        zonaActual = null;
    }

    private void mostrarMapa() {
        while (true) {
            System.out.println("\n=== Mapa de Hexagrama ===");
            System.out.println("Estás en: " + zonaActual);
            MapNode actual = map.getZone(zonaActual);

            Map<Integer, String> opciones = new HashMap<>();
            int i = 1;
            System.out.println("Zonas disponibles para viajar:");
            for (MapNode vecino : actual.getNeighbors()) {
                if (zonasDesbloqueadas.contains(vecino.getName())) {
                    System.out.println(i + ". " + vecino.getName());
                    opciones.put(i, vecino.getName());
                    i++;
                }
            }
            System.out.println(i + ". Ver inventario");
            System.out.println((i + 1) + ". Gestionar habilidades");
            System.out.println((i + 2) + ". Volver al menú principal");

            System.out.print("Selecciona una opción: ");
            try {
                int eleccion = Integer.parseInt(scanner.nextLine());

                if (eleccion == i) {
                    System.out.println("\nInventario:");
                    inventory.printInventory();
                } else if (eleccion == i + 1) {
                    gestionarHabilidades();
                } else if (eleccion == i + 2) {
                    break; // Volver al menú principal (post-batalla)
                } else if (opciones.containsKey(eleccion)) {
                    String destino = opciones.get(eleccion);
                    if (zonaActual.equals("Corona del hexagrama")) {
                        // Modo libre: permitir viajar directamente
                        System.out.println("Viajando desde " + zonaActual + " a " + destino + "...");
                        zonaActual = destino;
                        loopPrincipal(); // Iniciar batalla en el nuevo nodo
                    } else if (actual.getNeighbors().stream().anyMatch(n -> n.getName().equals(destino))) {
                        // Modo secuencial: solo permitir viajar a vecinos directos
                        System.out.println("Viajando desde " + zonaActual + " a " + destino + "...");
                        zonaActual = destino;
                        loopPrincipal(); // Iniciar batalla en el nuevo nodo
                    } else {
                        System.out.println("No puedes viajar directamente a esa zona. Debes seguir la secuencia.");
                    }
                } else {
                    System.out.println("Opción inválida.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Opción inválida.");
            }
        }
    }

    private void gestionarHabilidades() {
        while (true) {
            System.out.println("\n=== Árbol de Habilidades ===");
            System.out.println("Puntos de habilidad disponibles: " + player.getSkillPoints());
            mostrarArbolHabilidades(skillTree.getRoot(), 0, player.unlockedSkills());
            System.out.println("\nOpciones:");
            System.out.println("1. Desbloquear habilidad");
            System.out.println("2. Volver al mapa");
            System.out.print("Selecciona una opción: ");
            try {
                int opcion = Integer.parseInt(scanner.nextLine());

                switch (opcion) {
                    case 1:
                        System.out.print("Ingresa el nombre de la habilidad que deseas desbloquear: ");
                        String nombreHabilidad = scanner.nextLine();
                        SkillNode nodoHabilidad = skillTree.findNodeByName(nombreHabilidad);
                        if (nodoHabilidad != null) {
                            if (player.unlockSkill(nodoHabilidad)) {
                                System.out.println("Habilidad " + nombreHabilidad + " desbloqueada con éxito.");
                            } else {
                                System.out.println("No puedes desbloquear " + nombreHabilidad + ". Revisa los puntos de habilidad o si la habilidad padre está desbloqueada.");
                            }
                        } else {
                            System.out.println("Habilidad no encontrada.");
                        }
                        break;
                    case 2:
                        return;
                    default:
                        System.out.println("Opción inválida.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Opción inválida.");
            }
        }
    }

    private void mostrarArbolHabilidades(SkillNode nodo, int nivel, Set<String> desbloqueadas) {
        for (int i = 0; i < nivel; i++) {
            System.out.print("  ");
        }
        String estado = desbloqueadas.contains(nodo.getName()) ? "[DESBLOQUEADA]" : "[" + nodo.getCost() + " SP]";
        System.out.println("- " + nodo.getName() + " " + estado);
        for (SkillNode hijo : nodo.getChildren()) {
            mostrarArbolHabilidades(hijo, nivel + 1, desbloqueadas);
        }
    }

    private void loopPrincipal() {
        boolean jugando = true;
        wave.generateWave(); // Genera la nueva oleada
        Enemy[][] enemies = wave.getEnemyGrid();

        // Añadir al jugador y enemigos vivos a la cola
        battleQueue.getQueue().clear(); // Asegurarse de que la cola esté vacía al inicio de cada batalla
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
                    turnoEnemigo(enemigo);
                }

                // Verificamos si termina la ronda de enemigos
                if (battleQueue.peekNext().equals(player.getName())) {
                    System.out.println("Vida restante del jugador: " + player.getHealth());
                    history.record("Vida del jugador después de ataques enemigos: " + player.getHealth());
                }
            }

            wave.printGridStatus();
        }

        if (player.getHealth() <= 0) {
            System.out.println("¡Has sido derrotado en " + zonaActual + "!");
        } else {
            System.out.println("¡Has vencido a todos los enemigos en " + zonaActual + "!");
            // Otorgar puntos de habilidad después de ganar
            player.gainSkillPoints(5);
            System.out.println("¡Has ganado 5 puntos de habilidad por derrotar a los enemigos en " + zonaActual + "!");
            mostrarOpcionesPostBatalla();
            // Desbloquear la siguiente zona después de la batalla, si no es la Corona
            if (!zonaActual.equals("Corona del hexagrama")) {
                desbloquearSiguienteZona();
            }
        }

        // Restablecer la vida del jugador al máximo después de cada batalla
        player.resetHealth();
    }

    private void mostrarOpcionesPostBatalla() {
        boolean salir = false;
        while (!salir) {
            System.out.println("\n=== DespuÃ©s de la Batalla ===");
            System.out.println("1. Ver inventario");
            System.out.println("2. Gestionar habilidades (" + player.getSkillPoints() + " puntos disponibles)");
            System.out.println("3. Interactuar con el mapa"); // Cambiado "Continuar" a "Interactuar con el mapa"
            System.out.print("Elige una opciÃ³n: ");
            try {
                int opcion = Integer.parseInt(scanner.nextLine());
                switch (opcion) {
                    case 1:
                        inventory.printInventory();
                        break;
                    case 2:
                        gestionarHabilidades();
                        break;
                    case 3:
                        mostrarMapa(); // Ir a la interacción del mapa
                        salir = true; // Considera si quieres salir aquí o después de interactuar con el mapa
                        break;
                    default:
                        System.out.println("OpciÃ³n invÃ¡lida.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Entrada invÃ¡lida.");
            }
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
        player = new Player(nombre); // El Player se crea con sus ataques iniciales
        System.out.println("Jugador " + nombre + " creado con éxito.");
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
        map.connectZones("El vacio del alma", "Sombra del inicio"); // Corrección de conexión
        map.connectZones("Torres del juicio", "Corona del hexagrama");
        map.connectZones("Santuario del caos", "Corona del hexagrama");
        map.connectZones("Espejo Astral", "Corona del hexagrama");
        map.connectZones("Sombra del inicio", "Corona del hexagrama"); // Conexión directa inicial para permitir el desbloqueo final
    }

    private void desbloquearSiguienteZona() {
        switch (ultimaZonaDesbloqueada) {
            case "Sombra del inicio":
                desbloquearZona("Torres del juicio");
                break;
            case "Torres del juicio":
                desbloquearZona("Santuario del caos");
                break;
            case "Santuario del caos":
                desbloquearZona("Espejo Astral");
                break;
            case "Espejo Astral":
                desbloquearZona("El vacio del alma");
                break;
            case "El vacio del alma":
                desbloquearZona("Corona del hexagrama");
                break;
            case "Corona del hexagrama":
                // Todas las zonas principales desbloqueadas, se habilitará el mapa libre
                break;
        }
    }

    private void desbloquearZona(String nombreZona) {
        if (!zonasDesbloqueadas.contains(nombreZona)) {
            zonasDesbloqueadas.add(nombreZona);
            ultimaZonaDesbloqueada = nombreZona;
            System.out.println("¡Zona desbloqueada: " + nombreZona + "!");
        }
    }

    private void poblarInventario() {
        inventory.addItem("Poción", "Recupera 20 HP");
        inventory.addItem("Mapa", "Muestra el mundo");
    }

    private void turnoJugador(Enemy[][] enemies) {
        System.out.println("\n=== Tu Turno ===");
        System.out.println("Elige una acciÃ³n:");
        System.out.println("1. Atacar");
        System.out.println("2. Usar pociÃ³n");
        if (player.hasSkill("Luz Cortante +")) {
            System.out.println("3. Usar Luz Cortante + (" + (player.getLuzCortantePlusCooldown() > 0 ? "CD: " + player.getLuzCortantePlusCooldown() : "Listo") + ")");
        }
        if (player.hasSkill("Sombras de Defensa +")) {
            System.out.println("4. Usar Sombras de Defensa + (" + (player.getSombrasDefensaPlusCooldown() > 0 ? "CD: " + player.getSombrasDefensaPlusCooldown() : (player.isSombrasDefensaPlusActivo() ? "Activo" : "Listo")) + ")");
        }
        if (player.hasSkill("Sello Solar")) {
            System.out.println("5. Usar Sello Solar (" + (player.getSelloSolarCooldown() > 0 ? "CD: " + player.getSelloSolarCooldown() : "Listo") + ")");
        }
        if (player.hasSkill("Llama Negra")) {
            System.out.println("6. Usar Llama Negra (" + (player.getLlamaNegraCooldown() > 0 ? "CD: " + player.getLlamaNegraCooldown() : "Listo") + ")");
        }
        if (player.hasSkill("Juicio Final")) {
            System.out.println("7. Usar Juicio Final (" + (player.getJuicioFinalCooldown() > 0 ? "CD: " + player.getJuicioFinalCooldown() : "Listo") + ")");
        }
        if (player.hasSkill("Devorador de Almas")) {
            System.out.println("8. Usar Devorador de Almas (" + (player.getDevoradorAlmasCooldown() > 0 ? "CD: " + player.getDevoradorAlmasCooldown() : "Listo") + ")");
        }

        try {
            int accion = Integer.parseInt(scanner.nextLine());

            switch (accion) {
                case 1:
                    System.out.println("Elige la posiciÃ³n del enemigo que quieres atacar (fila y columna):");
                    wave.printGridStatus();
                    System.out.print("Fila (0, 1 o 2): ");
                    int filaAt = Integer.parseInt(scanner.nextLine());
                    System.out.print("Columna (0, 1 o 2): ");
                    int columnaAt = Integer.parseInt(scanner.nextLine());

                    if (filaAt >= 0 && filaAt < 3 && columnaAt >= 0 && columnaAt < 3) {
                        Enemy objetivo = enemies[filaAt][columnaAt];

                        if (objetivo != null && objetivo.getHealth() > 0) {
                            System.out.println("Elige un ataque:");
                            ArrayList<Attack> ataques = player.getAttacks();
                            for (int i = 0; i < ataques.size(); i++) {
                                System.out.println((i + 1) + ". " + ataques.get(i));
                            }
                            try {
                                int eleccionAtaque = Integer.parseInt(scanner.nextLine());
                                if (eleccionAtaque >= 1 && eleccionAtaque <= ataques.size()) {
                                    Attack ataque = ataques.get(eleccionAtaque - 1);
                                    int vidaAntes = objetivo.getHealth();
                                    int totalDamage = ataque.use(objetivo, player);

                                    history.record("Jugador usÃ³ " + ataque.getName() + " contra " + objetivo.getName() + " e hizo " + totalDamage + " de daÃ±o.");
                                    System.out.println("DaÃ±o total causado: " + totalDamage);

                                    if (objetivo.getHealth() <= 0 && vidaAntes > 0) {
                                        System.out.println("Â¡Has derrotado a " + objetivo.getName() + "!");
                                        inventory.addItem("PociÃ³n", "Recupera 20 HP");
                                        System.out.println("Has recibido una pociÃ³n como recompensa.");
                                    }
                                } else {
                                    System.out.println("OpciÃ³n de ataque no vÃ¡lida.");
                                }
                            } catch (NumberFormatException e) {
                                System.out.println("Entrada invÃ¡lida para la elecciÃ³n de ataque.");
                            }
                        } else {
                            System.out.println("No hay enemigo vivo en esa posiciÃ³n.");
                        }
                    } else {
                        System.out.println("PosiciÃ³n invÃ¡lida.");
                    }
                    break;
                case 2:
                    if (inventory.hasItem("PociÃ³n")) {
                        player.heal(20);
                        inventory.useItem("PociÃ³n"); // Usar el mÃ©todo correcto para remover
                        System.out.println(player.getName() + " usa una pociÃ³n y recupera 20 HP.");
                    } else {
                        System.out.println("No tienes pociones en tu inventario.");
                    }
                    break;
                case 3:
                    if (player.hasSkill("Luz Cortante +")) {
                        player.usarLuzCortantePlus(enemies);
                    } else {
                        System.out.println("OpciÃ³n invÃ¡lida.");
                    }
                    break;
                case 4:
                    if (player.hasSkill("Sombras de Defensa +")) {
                        player.usarSombrasDefensaPlus();
                        // La aplicaciÃ³n del reflejo de daÃ±o se harÃ¡ en turnoEnemigos
                    } else {
                        System.out.println("OpciÃ³n invÃ¡lida.");
                    }
                    break;
                case 5:
                    if (player.hasSkill("Sello Solar")) {
                        usarSelloSolar(enemies);
                    } else {
                        System.out.println("OpciÃ³n invÃ¡lida.");
                    }
                    break;
                case 6:
                    if (player.hasSkill("Llama Negra")) {
                        usarLlamaNegra(enemies);
                    } else {
                        System.out.println("OpciÃ³n invÃ¡lida.");
                    }
                    break;
                case 7:
                    if (player.hasSkill("Juicio Final")) {
                        usarJuicioFinal(enemies);
                    } else {
                        System.out.println("OpciÃ³n invÃ¡lida.");
                    }
                    break;
                case 8:
                    if (player.hasSkill("Devorador de Almas")) {
                        usarDevoradorDeAlmas(enemies);
                    } else {
                        System.out.println("OpciÃ³n invÃ¡lida.");
                    }
                    break;
                default:
                    System.out.println("AcciÃ³n invÃ¡lida.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Entrada invÃ¡lida.");
        } finally {
            player.resetCooldowns(); // Asegurarse de que los cooldowns se reduzcan al final del turno
        }
    }

    private void turnoEnemigo(Enemy enemy) {
        int damage = enemy.getDamage();
        System.out.println(enemy.getName() + " ataca a " + player.getName() + " causando " + damage + " de daño.");
        player.takeDamage(damage);
        history.record(enemy.getName() + " atacó a " + player.getName() + " e hizo " + damage + " de daño.");
        if (player.isSombrasDefensaPlusActivo()) {
            System.out.println(player.getName() + " refleja " + damage / 2 + " de daño a " + enemy.getName() + ".");
            enemy.takeDamage(damage / 2);
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

    private void usarSelloSolar(Enemy[][] enemies) {
        if (player.hasSkill("Sello Solar") && player.getSelloSolarCooldown() == 0) {
            System.out.println("Elige la esquina superior izquierda del Ã¡rea 2x2 para Sello Solar:");
            wave.printGridStatus();
            System.out.print("Fila (0 o 1): ");
            try {
                int filaInicio = Integer.parseInt(scanner.nextLine());
                System.out.print("Columna (0 o 1): ");
                int columnaInicio = Integer.parseInt(scanner.nextLine());

                if (filaInicio >= 0 && filaInicio <= 1 && columnaInicio >= 0 && columnaInicio <= 1) {
                    Attack selloSolarAttack = player.getAttacks().stream().filter(a -> a.getName().equals("Sello Solar")).findFirst().orElse(null);
                    if (selloSolarAttack != null) {
                        int enemigosGolpeados = 0;
                        for (int i = filaInicio; i < filaInicio + 2; i++) {
                            for (int j = columnaInicio; j < columnaInicio + 2; j++) {
                                if (i >= 0 && i < 3 && j >= 0 && j < 3 && enemies[i][j] != null && enemies[i][j].getHealth() > 0) {
                                    int damage = selloSolarAttack.use(enemies[i][j], player);
                                    System.out.println("Sello Solar golpea a " + enemies[i][j].getName() + " con " + damage + " de daÃ±o.");
                                    enemigosGolpeados++;
                                }
                            }
                        }
                        if (enemigosGolpeados > 0) {
                            player.setSelloSolarCooldown(4);
                        }
                    }
                } else {
                    System.out.println("Coordenadas invÃ¡lidas.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Entrada invÃ¡lida.");
            }
        } else if (!player.hasSkill("Sello Solar")) {
            System.out.println("No has desbloqueado Sello Solar.");
        } else {
            System.out.println("Sello Solar estÃ¡ en cooldown (" + player.getSelloSolarCooldown() + " turnos restantes).");
        }
    }

    private void usarLlamaNegra(Enemy[][] enemies) {
        if (player.hasSkill("Llama Negra") && player.getLlamaNegraCooldown() == 0) {
            System.out.println("Elige la fila para la Llama Negra (0, 1 o 2):");
            wave.printGridStatus();
            try {
                int fila = Integer.parseInt(scanner.nextLine());
                if (fila >= 0 && fila < 3) {
                    Attack llamaNegraAttack = player.getAttacks().stream().filter(a -> a.getName().equals("Llama Negra")).findFirst().orElse(null);
                    if (llamaNegraAttack != null) {
                        int enemigosGolpeados = 0;
                        for (int j = 0; j < 3; j++) {
                            if (enemies[fila][j] != null && enemies[fila][j].getHealth() > 0) {
                                int damage = llamaNegraAttack.use(enemies[fila][j], player);
                                System.out.println("Llama Negra golpea a " + enemies[fila][j].getName() + " con " + damage + " de daÃ±o.");
                                enemigosGolpeados++;
                            }
                        }
                        if (enemigosGolpeados > 0) {
                            player.setLlamaNegraCooldown(4);
                        }
                    }
                } else {
                    System.out.println("Fila invÃ¡lida.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Entrada invÃ¡lida.");
            }
        } else if (!player.hasSkill("Llama Negra")) {
            System.out.println("No has desbloqueado Llama Negra.");
        } else {
            System.out.println("Llama Negra estÃ¡ en cooldown (" + player.getLlamaNegraCooldown() + " turnos restantes).");
        }
    }

    private void usarJuicioFinal(Enemy[][] enemies) {
        if (player.hasSkill("Juicio Final") && player.getJuicioFinalCooldown() == 0) {
            System.out.println("Â¡El Juicio Final golpea a toda la oleada!");
            Attack juicioFinalAttack = player.getAttacks().stream().filter(a -> a.getName().equals("Juicio Final")).findFirst().orElse(null);
            if (juicioFinalAttack != null) {
                for (int i = 0; i < 3; i++) {
                    for (int j = 0; j < 3; j++) {
                        if (enemies[i][j] != null && enemies[i][j].getHealth() > 0) {
                            int damage = juicioFinalAttack.use(enemies[i][j], player);
                            System.out.println("Juicio Final golpea a " + enemies[i][j].getName() + " con " + damage + " de daÃ±o.");
                        }
                    }
                }
            }
            player.setJuicioFinalCooldown(5);
        } else if (!player.hasSkill("Juicio Final")) {
            System.out.println("No has desbloqueado Juicio Final.");
        } else {
            System.out.println("Juicio Final estÃ¡ en cooldown (" + player.getJuicioFinalCooldown() + " turnos restantes).");
        }
    }

    private void usarDevoradorDeAlmas(Enemy[][] enemies) {
        if (player.hasSkill("Devorador de Almas") && player.getDevoradorAlmasCooldown() == 0) {
            List<Enemy> enemigosVivos = new ArrayList<>();
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (enemies[i][j] != null && enemies[i][j].getHealth() > 0) {
                        enemigosVivos.add(enemies[i][j]);
                    }
                }
            }

            if (!enemigosVivos.isEmpty()) {
                Random random = new Random();
                Enemy objetivo = enemigosVivos.get(random.nextInt(enemigosVivos.size()));
                int vidaObjetivo = objetivo.getHealth();
                System.out.println(player.getName() + " usa Devorador de Almas contra " + objetivo.getName() + ".");
                objetivo.takeDamage(vidaObjetivo); // Mata al enemigo
                player.heal(vidaObjetivo);
                System.out.println("Â¡" + objetivo.getName() + " ha sido devorado! " + player.getName() + " recupera " + vidaObjetivo + " de vida.");
                player.setDevoradorAlmasCooldown(6);
            } else {
                System.out.println("No hay enemigos vivos para usar Devorador de Almas.");
            }
        } else if (!player.hasSkill("Devorador de Almas")) {
            System.out.println("No has desbloqueado Devorador de Almas.");
        } else {
            System.out.println("Devorador de Almas estÃ¡ en cooldown (" + player.getDevoradorAlmasCooldown() + " turnos restantes).");
        }
    }

    // ======= BFS PARA MOSTRAR RUTAS =======
    public void mostrarRuta(String inicio, String destino) {
        Queue<List<String>> cola = new LinkedList<>();
        Set<String> visitados = new HashSet<>();

        cola.add(List.of(inicio));
        while (!cola.isEmpty()) {
            List<String> ruta = cola.poll();
            String actual = ruta.get(ruta.size() - 1);
            if (actual.equals(destino)) {
                System.out.println("Ruta encontrada: " + String.join(" -> ", ruta));
                return;
            }
            if (!visitados.contains(actual)) {
                visitados.add(actual);
                for (MapNode vecino : map.getZone(actual).getNeighbors()) {
                    if (zonasDesbloqueadas.contains(vecino.getName())) {
                        List<String> nuevaRuta = new ArrayList<>(ruta);
                        nuevaRuta.add(vecino.getName());
                        cola.add(nuevaRuta);
                    }
                }
            }
        }
        System.out.println("No hay ruta entre " + inicio + " y " + destino);
    }
}
