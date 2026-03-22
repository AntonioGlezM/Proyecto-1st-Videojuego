package view;

import java.util.Scanner;

import controller.Combate;



/**
 * Clase Vista.
 *
 * Se encarga de:
 * - Mostrar el menú principal
 * - Iniciar el combate
 *
 * NO contiene lógica del juego.
 * Solo interactúa con el usuario.
 */
public class View {

    private Scanner scanner;

    public View() {
        scanner = new Scanner(System.in);
    }

    public void mostrarMenu() {

        int opcion;

        do {
            System.out.println("\n=================================");
            System.out.println("      JUEGO DE COMBATE RPG");
            System.out.println("=================================");
            System.out.println("1 - Iniciar combate");
            System.out.println("2 - Salir");
            System.out.print("Elige opción: ");

            opcion = scanner.nextInt();

            switch (opcion) {

                case 1:
                    iniciarCombate();
                    break;

                case 2:
                    System.out.println("Saliendo del juego...");
                    break;

                default:
                    System.out.println("Opción inválida.");
            }

        } while (opcion != 2);
    }

    private void iniciarCombate() {

        Combate combate = new Combate();
        combate.iniciarCombate();
    }
}