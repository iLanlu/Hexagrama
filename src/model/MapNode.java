package model;

import java.util.List;
import java.util.ArrayList;

/**
 *
 * @author DELL
 */
public class MapNode {

    private String name;
    private List<MapNode> neighbors;

    public MapNode(String name) {
        this.name = name;
        this.neighbors = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public List<MapNode> getNeighbors() {
        return neighbors;
    }

    public void connect(MapNode other) {
        neighbors.add(other);
    }
}
