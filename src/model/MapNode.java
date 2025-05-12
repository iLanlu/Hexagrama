package model;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

/**
 *
 * @author DELL
 */
public class MapNode implements Serializable {

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
