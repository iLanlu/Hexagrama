package controller;

/**
 *
 * @author DELL
 */
public class MapController {

    private int currentZone = 0;

    public void loadMap() {
        System.out.println("Mapa cargado con éxito.");
    }

    public void showCurrentZone() {
        System.out.println("Estás en la zona #" + currentZone);
    }

    public void moveToNextZone() {
        currentZone++;
        System.out.println("Te has movido a la zona #" + currentZone);
    }

    public int getCurrentZone() {
        return currentZone;
    }
}
