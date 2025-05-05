package model;

import java.util.Stack;

/**
 *
 * @author DELL
 */
public class ActionHistory {

    private Stack<String> actions;

    public ActionHistory() {
        actions = new Stack<>();
    }

    public void record(String action) {
        actions.push(action);
    }

    public String undo() {
        return actions.isEmpty() ? null : actions.pop();
    }

    public Stack<String> getAllActions() {
        return actions;
    }
}
