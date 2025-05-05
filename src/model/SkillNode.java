package model;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author DELL
 */
public class SkillNode {

    private String name;
    private List<SkillNode> children;

    public SkillNode(String name) {
        this.name = name;
        this.children = new ArrayList<>();
    }

    public void addChild(SkillNode child) {
        children.add(child);
    }

    public String getName() {
        return name;
    }

    public List<SkillNode> getChildren() {
        return children;
    }
}
