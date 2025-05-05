package model;

/**
 *
 * @author DELL
 */
public class SkillTree {

    private SkillNode root;

    public SkillTree(String rootName) {
        this.root = new SkillNode(rootName);
    }

    public SkillNode getRoot() {
        return root;
    }
}
