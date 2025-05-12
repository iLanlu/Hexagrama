package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 *
 * @author DELL
 */
public class Player implements Serializable {

    private String name;
    private int health, maxHealth, skillPoints;
    private ArrayList<Attack> attacks;
    private Set<String> unlockedSkills;
    private Random random = new Random();
    private boolean concienciaExpandidaActiva = false; // Para el efecto pasivo por turno

    private int luzCortantePlusCooldown = 0;
    private int sombrasDefensaPlusCooldown = 0;
    private boolean sombrasDefensaPlusActivo = false;
    private int selloSolarCooldown = 0;
    private int llamaNegraCooldown = 0;
    private int juicioFinalCooldown = 0;
    private int devoradorAlmasCooldown = 0;

    public Player(String name) {
        this.name = name;
        this.health = 1000;
        this.maxHealth = 1000;
        this.attacks = new ArrayList<>();
        this.unlockedSkills = new HashSet<>();
        this.skillPoints = 0;
        // Habilidades iniciales
        this.attacks.add(new Attack("Espada de Luz", 25, "Ataque brillante", 1));
        this.attacks.add(new Attack("Flecha Sombra", 15, "Ataque a distancia", 2));
        this.unlockedSkills.add("Espada de Luz"); // Consideramos las habilidades iniciales como desbloqueadas para usarlas
        this.unlockedSkills.add("Flecha Sombra");
    }

    public void addAttack(Attack attack) {
        attacks.add(attack);
    }

    public ArrayList<Attack> getAttacks() {
        return attacks;
    }

    public void gainSkillPoints(int amount) {
        this.skillPoints += amount;
    }

    public boolean unlockSkill(SkillNode skill) {
        if (unlockedSkills.contains(skill.getName())) {
            return false;
        }
        if (skill.getCost() <= skillPoints && (skill.getParent() == null || unlockedSkills.contains(skill.getParent().getName()))) {
            unlockedSkills.add(skill.getName());
            skillPoints -= skill.getCost();
            System.out.println("Habilidad desbloqueada: " + skill.getName());
            // Si la habilidad desbloqueada es un ataque, lo añadimos a la lista de ataques
            if (skill.getAttack() != null) {
                addAttack(skill.getAttack());
            }
            return true;
        }
        return false;
    }

    public boolean hasSkill(String skillName) {
        return unlockedSkills.contains(skillName);
    }

    public int getSkillPoints() {
        return skillPoints;
    }

    public int getHealth() {
        return health;
    }

    public void takeDamage(int amount) {
        if (unlockedSkills.contains("Conciencia Expandida") && random.nextDouble() < 0.5) {
            System.out.println(name + " esquiva el ataque gracias a Conciencia Expandida.");
            return; // esquiva
        }
        int damageTaken = amount;
        if (sombrasDefensaPlusActivo) {
            int reflectedDamage = amount / 2;
            System.out.println(name + " refleja " + reflectedDamage + " de daño a los enemigos.");
            // Aquí necesitarías una forma de dañar a todos los enemigos
            // Esto se manejará mejor en el GameController durante el turno del jugador.
            damageTaken = 0; // El jugador no recibe daño
        }
        health -= damageTaken;
        if (health < 0) {
            health = 0;
        }
    }

    public void heal(int amount) {
        this.health += amount;
        if (this.health > this.maxHealth) {
            this.health = this.maxHealth;
        }
    }

    public void resetHealth() {
        this.health = this.maxHealth;
    }

    public String getName() {
        return name;
    }

    public Set<String> unlockedSkills() {
        return unlockedSkills;
    }

    public void resetCooldowns() {
        luzCortantePlusCooldown = Math.max(0, luzCortantePlusCooldown - 1);
        sombrasDefensaPlusCooldown = Math.max(0, sombrasDefensaPlusCooldown - 1);
        selloSolarCooldown = Math.max(0, selloSolarCooldown - 1);
        llamaNegraCooldown = Math.max(0, llamaNegraCooldown - 1);
        juicioFinalCooldown = Math.max(0, juicioFinalCooldown - 1);
        devoradorAlmasCooldown = Math.max(0, devoradorAlmasCooldown - 1);
        if (sombrasDefensaPlusCooldown == 0) {
            sombrasDefensaPlusActivo = false;
        }
        // Efecto pasivo de Conciencia Expandida (chance de crítico en cada turno)
        concienciaExpandidaActiva = unlockedSkills.contains("Conciencia Expandida") && random.nextDouble() < 0.5;
    }

    public boolean isConcienciaExpandidaActiva() {
        return concienciaExpandidaActiva;
    }

    public int getLuzCortantePlusCooldown() {
        return luzCortantePlusCooldown;
    }

    public void setLuzCortantePlusCooldown(int luzCortantePlusCooldown) {
        this.luzCortantePlusCooldown = luzCortantePlusCooldown;
    }

    public void usarLuzCortantePlus(Enemy[][] enemies) {
        if (unlockedSkills.contains("Luz Cortante +") && luzCortantePlusCooldown == 0) {
            heal(30);
            System.out.println(name + " usa Luz Cortante +, se cura 30 HP.");
            List<Enemy> enemigosVivos = new ArrayList<>();
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (enemies[i][j] != null && enemies[i][j].getHealth() > 0) {
                        enemigosVivos.add(enemies[i][j]);
                    }
                }
            }
            if (!enemigosVivos.isEmpty()) {
                Enemy objetivo = enemigosVivos.get(random.nextInt(enemigosVivos.size()));
                objetivo.takeDamage(15);
                System.out.println("Luz Cortante + daña a " + objetivo.getName() + " con 15 de daño.");
            } else {
                System.out.println("No hay enemigos vivos para dañar con Luz Cortante +.");
            }
            luzCortantePlusCooldown = 2;
        } else if (!unlockedSkills.contains("Luz Cortante +")) {
            System.out.println("No has desbloqueado Luz Cortante +.");
        } else {
            System.out.println("Luz Cortante + está en cooldown (" + luzCortantePlusCooldown + " turnos restantes).");
        }
    }

    public int getSombrasDefensaPlusCooldown() {
        return sombrasDefensaPlusCooldown;
    }

    public void setSombrasDefensaPlusCooldown(int sombrasDefensaPlusCooldown) {
        this.sombrasDefensaPlusCooldown = sombrasDefensaPlusCooldown;
    }

    public boolean isSombrasDefensaPlusActivo() {
        return sombrasDefensaPlusActivo;
    }

    public void setSombrasDefensaPlusActivo(boolean sombrasDefensaPlusActivo) {
        this.sombrasDefensaPlusActivo = sombrasDefensaPlusActivo;
    }

    public void usarSombrasDefensaPlus() {
        if (unlockedSkills.contains("Sombras de Defensa +") && sombrasDefensaPlusCooldown == 0) {
            sombrasDefensaPlusActivo = true;
            sombrasDefensaPlusCooldown = 3;
            System.out.println(name + " activa Sombras de Defensa +, reflejará el 50% del daño este turno.");
        } else if (!unlockedSkills.contains("Sombras de Defensa +")) {
            System.out.println("No has desbloqueado Sombras de Defensa +.");
        } else {
            System.out.println("Sombras de Defensa + está en cooldown (" + sombrasDefensaPlusCooldown + " turnos restantes).");
        }
    }

    public void usarSelloSolar(Enemy[][] enemies) {
        if (unlockedSkills.contains("Sello Solar") && selloSolarCooldown == 0) {
            System.out.println(name + " usa Sello Solar, atacando un área 2x2.");
            int dañoBase = 25 + (unlockedSkills.contains("Dominio de Luz") ? 12 : 0); // Daño similar a Espada de Luz buffeada
            int enemigosAtacados = 0;
            for (int i = 0; i < 2; i++) {
                for (int j = 0; j < 2; j++) {
                    // Seleccionar un área 2x2 aleatoria dentro de la cuadrícula
                    int startRow = random.nextInt(2);
                    int startCol = random.nextInt(2);
                    int row = startRow + i;
                    int col = startCol + j;
                    if (enemies[row][col] != null && enemies[row][col].getHealth() > 0) {
                        int damageDealt = new Attack("Sello Solar", dañoBase, "Ataque de área", 0).use(enemies[row][col], this);
                        System.out.println("Sello Solar golpea a " + enemies[row][col].getName() + " con " + damageDealt + " de daño.");
                        enemigosAtacados++;
                    }
                }
            }
            if (enemigosAtacados > 0) {
                selloSolarCooldown = 4;
            } else {
                System.out.println("No había enemigos en el área objetivo de Sello Solar.");
            }
        } else if (!unlockedSkills.contains("Sello Solar")) {
            System.out.println("No has desbloqueado Sello Solar.");
        } else {
            System.out.println("Sello Solar está en cooldown (" + selloSolarCooldown + " turnos restantes).");
        }
    }

    public void usarLlamaNegra(Enemy[][] enemies) {
        if (unlockedSkills.contains("Llama Negra") && llamaNegraCooldown == 0) {
            System.out.println(name + " usa Llama Negra, atacando en línea recta.");
            int dañoBase = 15 + (unlockedSkills.contains("Dominio de Sombra") ? 18 : 0); // Daño similar a Flecha Sombra buffeada
            int startRow = random.nextInt(3); // Ataca una fila aleatoria
            for (int col = 0; col < 3; col++) {
                if (enemies[startRow][col] != null && enemies[startRow][col].getHealth() > 0) {
                    int damageDealt = new Attack("Llama Negra", dañoBase, "Ataque en línea", 0).use(enemies[startRow][col], this);
                    System.out.println("Llama Negra golpea a " + enemies[startRow][col].getName() + " con " + damageDealt + " de daño.");
                }
            }
            llamaNegraCooldown = 4;
        } else if (!unlockedSkills.contains("Llama Negra")) {
            System.out.println("No has desbloqueado Llama Negra.");
        } else {
            System.out.println("Llama Negra está en cooldown (" + llamaNegraCooldown + " turnos restantes).");
        }
    }

    public void usarJuicioFinal(Enemy[][] enemies) {
        if (unlockedSkills.contains("Juicio Final") && juicioFinalCooldown == 0) {
            System.out.println(name + " usa Juicio Final, ¡arrasando toda la oleada!");
            int dañoBase = 35 + (unlockedSkills.contains("Sello Solar") ? 15 : 0); // Daño base similar a Sello Solar
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (enemies[i][j] != null && enemies[i][j].getHealth() > 0) {
                        int damageDealt = new Attack("Juicio Final", dañoBase, "Ataque de área total", 0).use(enemies[i][j], this);
                        System.out.println("Juicio Final golpea a " + enemies[i][j].getName() + " con " + damageDealt + " de daño.");
                    }
                }
            }
            juicioFinalCooldown = 5;
        } else if (!unlockedSkills.contains("Juicio Final")) {
            System.out.println("No has desbloqueado Juicio Final.");
        } else {
            System.out.println("Juicio Final está en cooldown (" + juicioFinalCooldown + " turnos restantes).");
        }
    }

    public void usarDevoradorAlmas(Enemy[][] enemies) {
        if (unlockedSkills.contains("Devorador de Almas") && devoradorAlmasCooldown == 0) {
            List<Enemy> enemigosVivos = new ArrayList<>();
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (enemies[i][j] != null && enemies[i][j].getHealth() > 0) {
                        enemigosVivos.add(enemies[i][j]);
                    }
                }
            }
            if (!enemigosVivos.isEmpty()) {
                Enemy objetivo = enemigosVivos.get(random.nextInt(enemigosVivos.size()));
                int vidaObjetivo = objetivo.getHealth();
                System.out.println(name + " usa Devorador de Almas contra " + objetivo.getName() + ".");
                objetivo.takeDamage(vidaObjetivo); // Mata al enemigo
                heal(vidaObjetivo);
                System.out.println(objetivo.getName() + " ha sido devorado. " + name + " gana " + vidaObjetivo + " de vida.");
                devoradorAlmasCooldown = 6;
            } else {
                System.out.println("No hay enemigos vivos para usar Devorador de Almas.");
            }
        } else if (!unlockedSkills.contains("Devorador de Almas")) {
            System.out.println("No has desbloqueado Devorador de Almas.");
        } else {
            System.out.println("Devorador de Almas está en cooldown (" + devoradorAlmasCooldown + " turnos restantes).");
        }
    }

    public int getSelloSolarCooldown() {
        return selloSolarCooldown;
    }

    public void setSelloSolarCooldown(int selloSolarCooldown) {
        this.selloSolarCooldown = selloSolarCooldown;
    }

    public int getLlamaNegraCooldown() {
        return llamaNegraCooldown;
    }

    public void setLlamaNegraCooldown(int llamaNegraCooldown) {
        this.llamaNegraCooldown = llamaNegraCooldown;
    }

    public int getJuicioFinalCooldown() {
        return juicioFinalCooldown;
    }

    public void setJuicioFinalCooldown(int juicioFinalCooldown) {
        this.juicioFinalCooldown = juicioFinalCooldown;
    }

    public int getDevoradorAlmasCooldown() {
        return devoradorAlmasCooldown;
    }

    public void setDevoradorAlmasCooldown(int devoradorAlmasCooldown) {
        this.devoradorAlmasCooldown = devoradorAlmasCooldown;
    }
}
