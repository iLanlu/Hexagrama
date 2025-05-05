package controller;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author DELL
 */
public class InventoryController {
    private Map<String, String> inventory;

    public InventoryController() {
        inventory = new HashMap<>();
        inventory.put("Poción de vida", "Restaura 50 HP");
        inventory.put("Pergamino de fuego", "Lanza un ataque de fuego");
    }

    public void showInventory() {
        System.out.println("--- Inventario ---");
        for (String key : inventory.keySet()) {
            System.out.println(key + ": " + inventory.get(key));
        }
    }
}
