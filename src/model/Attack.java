package model;

import java.io.Serializable;

/**
 *
 * @author DELL
 */
public class Attack implements Serializable {

    private String name;
    private int damage;
    private String description;
    private int chainLevel; // cuántas veces puede encadenarse

    public Attack(String name, int damage, String description, int chainLevel) {
        this.name = name;
        this.damage = damage;
        this.description = description;
        this.chainLevel = chainLevel;
    }

    public int use(Enemy target, Player attacker) {
        int totalDamage = damage;
        boolean isCrit = false;

        // Lógica de Conciencia Expandida (Crítico)
        if (attacker != null && attacker.hasSkill("Conciencia Expandida") && Math.random() < 0.5) {
            totalDamage *= 2; // Daño crítico
            isCrit = true;
            System.out.println("¡Ataque crítico!");
        }

        System.out.println("Lanzando " + name + " a " + target.getName() + " (Daño base: " + damage + ")");
        int finalDamage = totalDamage;

        // Buff de Dominio de Luz (Curación)
        if (attacker != null && attacker.hasSkill("Dominio de Luz") && name.equals("Espada de Luz")) {
            int healAmount = finalDamage / 2;
            attacker.heal(healAmount);
            System.out.println(attacker.getName() + " se cura " + healAmount + " HP.");
        }

        // Buff de Dominio de Sombra (Duplicar/Triplicar daño)
        if (attacker != null && attacker.hasSkill("Dominio de Sombra") && name.equals("Flecha Sombra")) {
            double rand = Math.random();
            if (rand < 0.1) {
                finalDamage *= 3;
                System.out.println("¡Flecha Sombra triplica el daño!");
            } else if (rand < 0.5) {
                finalDamage *= 2;
                System.out.println("¡Flecha Sombra duplica el daño!");
            }
        }

        target.takeDamage(finalDamage);
        if (isCrit) {
            System.out.println("Daño total infligido (crítico): " + finalDamage);
        } else {
            System.out.println("Daño total infligido: " + finalDamage);
        }

        if (chainLevel > 0) {
            Attack chained = new Attack(name + " (eco)", damage / 2, "Eco del ataque", chainLevel - 1);
            totalDamage += chained.use(target, attacker); // Importante pasar el attacker también
        }

        return finalDamage;
    }

    public String getName() {
        return name;
    }

    public int getDamage() {
        return damage;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return name + " (Daño: " + damage + ") - " + description;
    }
}
