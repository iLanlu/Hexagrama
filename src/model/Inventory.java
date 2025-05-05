package model;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author DELL
 */
public class Inventory {

    private Map<String, String> items;

    public Inventory() {
        items = new HashMap<>();
    }

    public void addItem(String name, String description) {
        items.put(name, description);
    }

    public String getItem(String name) {
        return items.get(name);
    }

    public Map<String, String> getAllItems() {
        return items;
    }
}
