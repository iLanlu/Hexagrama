package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author DELL
 */
public class SkillNode implements Serializable {

    private String name;
    private int cost;
    private List<SkillNode> children = new ArrayList<>();
    private SkillNode parent;
    private Attack attack; // Referencia al ataque que desbloquea esta habilidad (si es un ataque)

    public SkillNode(String name, int cost, Attack attack) {
        this.name = name;
        this.cost = cost;
        this.attack = attack;
    }

    public void addChild(SkillNode child) {
        children.add(child);
        child.setParent(this);
    }

    public void setParent(SkillNode parent) {
        this.parent = parent;
    }

    public SkillNode getParent() {
        return parent;
    }

    public String getName() {
        return name;
    }

    public int getCost() {
        return cost;
    }

    public List<SkillNode> getChildren() {
        return children;
    }

    public Attack getAttack() {
        return attack;
    }
}
