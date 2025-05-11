package model;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author DELL
 */
public class Inventory {

    private HashMap<String, Integer> items;

    public Inventory() {
        items = new HashMap<>();
    }

    public void addItem(String itemName, String description) {
        items.put(itemName, items.getOrDefault(itemName, 0) + 1);
    }

    public boolean useItem(String itemName) {
        if (items.containsKey(itemName) && items.get(itemName) > 0) {
            items.put(itemName, items.get(itemName) - 1);
            if (items.get(itemName) == 0) {
                items.remove(itemName);
            }
            return true;
        }
        return false;
    }

    public int getItemCount(String itemName) {
        return items.getOrDefault(itemName, 0);
    }

    public boolean hasItem(String itemName) {
        return items.getOrDefault(itemName, 0) > 0;
    }

    public void printInventory() {
        if (items.isEmpty()) {
            System.out.println("Inventario vacío.");
        } else {
            System.out.println("Inventario:");
            for (String item : items.keySet()) {
                System.out.println(item + ": " + items.get(item));
            }
        }
    }
}
