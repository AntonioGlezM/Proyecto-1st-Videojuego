package main;

import controller.Combate;
import view.View;

public class Main {
    public static void main(String[] args) {
        View view = new View();
        Combate combate = new Combate(view);
        combate.iniciarCombate();
    }
}
