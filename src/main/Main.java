package main;

import controller.Combate;
import view.View;

/**
 * Punto de entrada de la aplicación.
 *
 * Gestiona el bucle principal del juego:
 * - Muestra el menú principal.
 * - Inicia combates nuevos mientras el jugador quiera seguir jugando.
 * - Cierra la aplicación cuando el jugador decide salir.
 *
 * Es el único lugar donde se instancian View y Combate.
 * No contiene ninguna lógica de juego.
 *
 * @author Antonio González Martel
 */
public class Main {

    public static void main(String[] args) {

        View view = new View();

        // ==============================
        // BUCLE PRINCIPAL DEL JUEGO
        // Muestra el menú principal y gestiona la opción elegida.
        // Se repite hasta que el jugador elige salir.
        // ==============================
        boolean seguirJugando = true;

        while (seguirJugando) {

            view.mostrarMenuPrincipal();
            int opcion = view.leerOpcion();

            switch (opcion) {

                case 1:
                    // Creamos un nuevo Combate cada vez que se inicia una partida.
                    // Esto garantiza que todos los flags y contadores empiezan desde cero.
                    Combate combate = new Combate(view);
                    combate.iniciarCombate();

                    // ==============================
                    // PREGUNTAR SI JUGAR DE NUEVO
                    // Al terminar el combate se ofrece volver al menú principal
                    // o salir directamente sin pasar por él.
                    // ==============================
                    view.mostrarMenuJugarDeNuevo();
                    int respuesta = view.leerOpcion();

                    if (respuesta == 2) {
                        // El jugador no quiere seguir: salimos del bucle
                        seguirJugando = false;
                        view.mostrarSalida();
                    }
                    // Si elige 1 volvemos al menú principal automáticamente

                    break;

                case 2:
                    // El jugador elige salir desde el menú principal
                    seguirJugando = false;
                    view.mostrarSalida();
                    break;

                default:
                    view.mostrarOpcionInvalida();
            }
        }
    }
}