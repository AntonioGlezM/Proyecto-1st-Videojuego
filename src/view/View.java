package view;

import java.util.List;
import java.util.Scanner;

import model.habilidades.Habilidad;
import model.personajes.Personaje;

/**
 * Clase Vista del combate RPG.
 *
 * Responsabilidades:
 * - Mostrar toda la información por pantalla (mensajes, menús, resultados).
 * - Recoger la entrada del usuario (opciones, nombres).
 *
 * NO contiene lógica del juego.
 * NO toma decisiones. Solo muestra y pregunta.
 *
 * @author Antonio Gonzalez Martel
 */
public class View {

    private final Scanner scanner = new Scanner(System.in);

    // ============================
    // MENÚ PRINCIPAL
    // ============================

    public void mostrarMenuPrincipal() {
        System.out.println("\n=================================");
        System.out.println("      JUEGO DE COMBATE RPG");
        System.out.println("=================================");
        System.out.println("1 - Iniciar combate");
        System.out.println("2 - Salir");
        System.out.print("Elige opción: ");
    }

    public void mostrarSalida() {
        System.out.println("Saliendo del juego...");
    }

    public void mostrarOpcionInvalida() {
        System.out.println("Opción inválida.");
    }

    public int leerOpcion() {
        return scanner.nextInt();
    }

    // ============================
    // SELECCIÓN DE PERSONAJE
    // ============================

    public void mostrarMenuSeleccionPersonaje(String jugador) {
        System.out.println("=================================");
        System.out.println(jugador + " - Selecciona personaje");
        System.out.println("1 - Guerrero");
        System.out.println("2 - Asesino");
        System.out.println("3 - Barbaro");
        System.out.println("4 - Clerigo");
        System.out.println("5 - Mago");
        System.out.println("6 - Paladin");
        System.out.println("7 - Picaro");
        System.out.print("Opción: ");
    }

    public int leerSeleccionPersonaje() {
        return scanner.nextInt();
    }

    public void pedirNombrePersonaje() {
        System.out.print("Nombre del personaje: ");
    }

    public String leerNombrePersonaje() {
        return scanner.next();
    }

    public void mostrarPersonajeInvalido() {
        System.out.println("Opción no válida. Se asigna Asesino por defecto.");
    }

    // ============================
    // INICIO DE COMBATE
    // ============================

    public void mostrarInicioCombate() {
        System.out.println("\n========== COMBATE ==========");
    }

    public void mostrarIniciativa(String nombreGanador) {
        System.out.println("\nEmpieza atacando: " + nombreGanador);
    }

    // ============================
    // TURNO
    // ============================

    public void mostrarCabeceraTurno(Personaje atacante) {
        System.out.println("\n=================================");
        System.out.println("Es el turno de " + atacante.getNombre());
        System.out.println("Salud actual: " + atacante.getSalud());
        System.out.println("=================================");
    }

    public void mostrarMenuAcciones() {
        System.out.println("1 - Atacar");
        System.out.println("2 - Defender");
        System.out.println("3 - Usar consumible");
        System.out.println("4 - Pasar turno");
        System.out.print("Elige opción: ");
    }

    public void mostrarTurnoPasado() {
        System.out.println("Turno pasado.");
    }

    // ============================
    // HABILIDADES
    // ============================

    public void mostrarMenuHabilidades(List<Habilidad> habilidades) {
        System.out.println("\n--- HABILIDADES ---");
        for (int i = 0; i < habilidades.size(); i++) {
            System.out.println((i + 1) + " - " + habilidades.get(i).getNombre()
                    + " (Daño: " + habilidades.get(i).getDaño()
                    + " | Coste: " + habilidades.get(i).getCoste() + ")");
        }
        System.out.println("0 - Volver");
        System.out.print("Elige habilidad: ");
    }

    public void mostrarSinHabilidades() {
        System.out.println("Este personaje no tiene habilidades.");
    }

    public void mostrarHabilidadInvalida() {
        System.out.println("Habilidad inválida.");
    }

    // ============================
    // RESULTADO DEL ATAQUE
    // ============================

    public void mostrarAtaqueFallado() {
        System.out.println("El ataque ha FALLADO.");
    }

    public void mostrarGolpeCritico() {
        System.out.println("¡GOLPE CRÍTICO!");
    }

    public void mostrarUsoHabilidad(String nombreHabilidad) {
        System.out.println("Usando habilidad: " + nombreHabilidad);
    }

    public void mostrarResultadoAtaque(int dañoReal, String nombreDefensor, int saludRestante) {
        System.out.println("Daño causado: " + dañoReal);
        System.out.println("Salud restante de " + nombreDefensor + ": " + saludRestante);
    }

    public void mostrarManaInsuficiente() {
        System.out.println("No tienes suficiente mana.");
    }

    public void mostrarManaRestante(int mana) {
        System.out.println("Mana restante: " + mana);
    }

    // ============================
    // DEFENSA
    // ============================

    public void mostrarDefensaEnCooldown() {
        System.out.println("Defensa en cooldown.");
    }

    public void mostrarDefensaAplicada(String nombrePersonaje) {
        System.out.println(nombrePersonaje + " aumenta su defensa.");
    }

    public void mostrarSinCapacidadDefensa() {
        System.out.println("Este personaje no puede defender.");
    }

    // ============================
    // CONSUMIBLES
    // ============================

    public void mostrarInventarioVacio() {
        System.out.println("No tienes consumibles.");
    }

    public void mostrarMenuConsumibles(List<? extends model.consumibles.Consumibles> inventario) {
        for (int i = 0; i < inventario.size(); i++) {
            System.out.println((i + 1) + " - " + inventario.get(i).getNombre());
        }
        System.out.println("0 - Volver");
        System.out.print("Selecciona consumible: ");
    }

    // ============================
    // FIN DE COMBATE
    // ============================

    public void mostrarDerrota(String nombreDerrotado) {
        System.out.println("\n" + nombreDerrotado + " ha sido derrotado.");
    }

    public void mostrarFinCombate() {
        System.out.println("\n========= FIN DEL COMBATE =========");
    }
}