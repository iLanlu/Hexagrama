package model;

import java.util.ArrayList;

/**
 *
 * @author DELL
 */
public class Player {

    private String name;
    private int health;
    private ArrayList<Attack> attacks;

    public Player(String name) {
        this.name = name;
        this.health = 100;
        this.attacks = new ArrayList<>();
    }

    public void addAttack(Attack attack) {
        attacks.add(attack);
    }

    public ArrayList<Attack> getAttacks() {
        return attacks;
    }

    public int getHealth() {
        return health;
    }

    public void takeDamage(int amount) {
        health -= amount;
        if (health < 0) health = 0;
    }

    public String getName() {
        return name;
    }
}
