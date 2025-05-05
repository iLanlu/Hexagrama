package model;

/**
 *
 * @author DELL
 */
public class Attack {
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

    public void use(Player target) {
        System.out.println("Lanzando " + name + " a " + target.getName());
        target.takeDamage(damage);

        if (chainLevel > 0) {
            Attack chained = new Attack(name + " (eco)", damage / 2, "Eco del ataque", chainLevel - 1);
            chained.use(target); // recursividad aquí
        }
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
