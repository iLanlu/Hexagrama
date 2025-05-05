package model;

import java.util.LinkedList;
import java.util.Queue;

/**
 *
 * @author DELL
 */
public class BattleQueue {

    private Queue<String> turnOrder;

    public BattleQueue() {
        turnOrder = new LinkedList<>();
    }

    public void addCharacter(String name) {
        turnOrder.add(name);
    }

    public String nextTurn() {
        String name = turnOrder.poll();
        turnOrder.add(name);
        return name;
    }

    public Queue<String> getQueue() {
        return turnOrder;
    }
}
