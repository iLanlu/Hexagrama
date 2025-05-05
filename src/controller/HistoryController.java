package controller;

import java.util.Stack;

/**
 *
 * @author DELL
 */
public class HistoryController {

    private Stack<String> history;

    public HistoryController() {
        history = new Stack<>();
        history.push("Juego iniciado.");
    }

    public void addAction(String action) {
        history.push(action);
    }

    public void showHistory() {
        System.out.println("--- Historial de acciones ---");
        for (String h : history) {
            System.out.println("> " + h);
        }
    }
}
