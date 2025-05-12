package model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author DELL
 */
public class MapGraph implements Serializable {

    private Map<String, MapNode> nodes;

    public MapGraph() {
        nodes = new HashMap<>();
    }

    public void addZone(String name) {
        nodes.put(name, new MapNode(name));
    }

    public void connectZones(String from, String to) {
        MapNode a = nodes.get(from);
        MapNode b = nodes.get(to);
        if (a != null && b != null) {
            a.connect(b);
        }
    }

    public MapNode getZone(String name) {
        return nodes.get(name);
    }

    public Map<String, MapNode> getAllZones() {
        return nodes;
    }

    public Set<String> getAllZoneNames() {
        return nodes.keySet();
    }
}
