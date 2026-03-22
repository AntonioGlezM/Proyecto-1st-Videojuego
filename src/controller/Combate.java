package controller;

import java.util.Random;
import java.util.Scanner;

import model.Acciones.Defensa;
import model.habilidades.Habilidad;
import model.personajes.Personaje;
import model.capacidades.Defensor;

/**
 * Controlador del combate PvP por turnos.
 *
 * Funcionalidades:
 * - Sistema de iniciativa
 * - Turnos alternos
 * - Menú dinámico de habilidades según el personaje
 * - Sistema de defensa con cooldown
 * - Uso de consumibles
 *
 * @author Antonio Gonzalez Martel
 */
public class Combate {

    private Personaje jugador1;
    private Personaje jugador2;

    private final Random random = new Random();
    private final Scanner scanner = new Scanner(System.in);

    // Contadores de turnos
    private int turnosJugador1 = 0;
    private int turnosJugador2 = 0;

    // Último turno en que se usó defensa (cooldown de 3 turnos)
    private int ultimoTurnoDefensaJ1 = -3;
    private int ultimoTurnoDefensaJ2 = -3;

    public Combate() {
        this.jugador1 = seleccionarPersonaje("Jugador 1");
        this.jugador2 = seleccionarPersonaje("Jugador 2");
    }

    /**
     * Permite al usuario seleccionar un personaje.
     */
    private Personaje seleccionarPersonaje(String jugador) {

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

        int opcion = scanner.nextInt();

        System.out.print("Nombre del personaje: ");
        String nombre = scanner.next();

        switch (opcion) {

            case 1:

                return new model.personajes.Guerrero(
                        nombre, 800, 15, "Humano", 20, 10);

            case 2:
                return new model.personajes.Asesino(
                        nombre, 500, 20, "Humano", 25);

            case 3:

                return new model.personajes.Barbaro(
                        nombre, 600, 18, "Orco", 30);

            case 4:
                /*
                 * return new com.personajesvideojuegos.modelo.Personajes.Clerigo(
                 * nombre, 100, 12, "Humano", 25, 100, 10);
                 */

            case 5:
                /*
                 * return new com.personajesvideojuegos.modelo.Personajes.Mago(
                 * nombre, 80, 10, "Humano", 30, 120);
                 */

            case 6:
                /*
                 * return new com.personajesvideojuegos.modelo.Personajes.Paladin(
                 * nombre, 130, 14, "Humano", 18, 80);
                 */

            case 7:

                return new com.personajesvideojuegos.modelo.Personajes.Picaro(
                        nombre, 500, 16, "Humano", 22);

            default:
                System.out.println("Opción no válida. Se asigna Asesino por defecto.");
                return new com.personajesvideojuegos.modelo.Personajes.Asesino(
                        nombre, 900, 15, "Humano", 20);
        }
    }

    /**
     * Inicia el combate hasta que uno de los jugadores sea derrotado.
     */
    public void iniciarCombate() {

        System.out.println("\n========== COMBATE ==========");

        // Cada jugador recibe sus consumibles iniciales
        jugador1.cargarConsumiblesIniciales();
        jugador2.cargarConsumiblesIniciales();

        // Tirada de iniciativa para decidir quién comienza
        Personaje atacante = tirarIniciativa();
        Personaje defensor = (atacante == jugador1) ? jugador2 : jugador1;

        System.out.println("\nEmpieza atacando: " + atacante.getNombre());

        // Bucle principal del combate
        while (jugador1.estaVivo() && jugador2.estaVivo()) {

            System.out.println("\n=================================");
            System.out.println("Es el turno de " + atacante.getNombre());
            System.out.println("Salud actual: " + atacante.getSalud());
            System.out.println("=================================");

            // Incrementar el contador de turnos del atacante
            if (atacante == jugador1) {
                turnosJugador1++;
            } else {
                turnosJugador2++;
            }

            // Ejecutar la acción elegida por el atacante
            ejecutarTurno(atacante, defensor);

            // Comprobar si el defensor ha muerto
            if (!defensor.estaVivo()) {
                System.out.println("\n" + defensor.getNombre() + " ha sido derrotado.");
                break;
            }

            // Intercambiar roles: el defensor pasa a ser atacante
            Personaje temp = atacante;
            atacante = defensor;
            defensor = temp;
        }

        System.out.println("\n========= FIN DEL COMBATE =========");
    }

    /**
     * Ejecuta la acción del jugador en su turno.
     */
    private void ejecutarTurno(Personaje atacante, Personaje defensor) {

        System.out.println("1 - Atacar");
        System.out.println("2 - Defender");
        System.out.println("3 - Usar consumible");
        System.out.println("4 - Pasar turno");

        System.out.print("Elige opción: ");
        int opcion = scanner.nextInt();

        switch (opcion) {

            case 1:
                // Menú dinámico de habilidades
                menuHabilidades(atacante, defensor);
                break;

            case 2:
                // Usar defensa si el personaje tiene la capacidad
                usarDefensa(atacante);
                break;

            case 3:
                // Usar un consumible del inventario
                usarConsumible(atacante);
                break;

            case 4:
                System.out.println("Turno pasado.");
                break;

            default:
                System.out.println("Opción inválida.");
        }
    }

    /**
     * Menú dinámico de habilidades del personaje.
     * Se obtiene la lista de habilidades del atacante y se muestra.
     */
    private void menuHabilidades(Personaje atacante, Personaje defensor) {

        var habilidades = atacante.getHabilidades();

        if (habilidades.isEmpty()) {
            System.out.println("Este personaje no tiene habilidades.");
            return;
        }

        System.out.println("\n--- HABILIDADES ---");

        for (int i = 0; i < habilidades.size(); i++) {
            System.out.println((i + 1) + " - " + habilidades.get(i).getNombre()
                    + " (Daño: " + habilidades.get(i).getDaño()
                    + " | Coste: " + habilidades.get(i).getCoste() + ")");
        }

        System.out.println("0 - Volver");
        System.out.print("Elige habilidad: ");
        int opcion = scanner.nextInt();

        if (opcion == 0) {
            return;
        }

        if (opcion < 1 || opcion > habilidades.size()) {
            System.out.println("Habilidad inválida.");
            return;
        }

        var habilidadSeleccionada = habilidades.get(opcion - 1);

        ejecutarHabilidad(atacante, defensor, habilidadSeleccionada);
    }

    /**
     * Ejecuta la habilidad seleccionada mostrando el daño real
     * y la salud restante del defensor.
     */
    private void ejecutarHabilidad(Personaje atacante,
            Personaje defensor,
            Habilidad habilidad) {

        // Comprobamos si el personaje que ataca es de tipo PersonajeMagico
        // Esto es necesario porque solo los mágicos tienen sistema de mana.
        boolean esMagico = atacante instanceof com.personajesvideojuegos.modelo.Personajes.PersonajeMagico;

        // ==============================
        // 1️⃣ SI ES MÁGICO → CONSUMIR MANA
        // ==============================
        if (esMagico) {

            // Convertimos el objeto atacante a PersonajeMagico
            // (Casting) para poder usar sus métodos específicos como usarMana().
            var magico = (com.personajesvideojuegos.modelo.Personajes.PersonajeMagico) atacante;

            // Intentamos gastar el mana necesario según el coste de la habilidad.
            // El método usarMana() devuelve:
            // - true → si hay suficiente mana (y lo resta)
            // - false → si no hay suficiente mana
            if (!magico.usarMana(habilidad.getCoste())) {

                // Si no hay mana suficiente, se informa al jugador
                System.out.println("No tienes suficiente mana.");

                // Se termina el método aquí.
                // La habilidad NO se ejecuta.
                return;
            }
        }

        // Aplica el daño

        System.out.println("Usando habilidad: " + habilidad.getNombre());

        int dado = random.nextInt(6); // 0-5

        if (dado == 0 || dado == 1) {

            System.out.println("El ataque ha FALLADO.");
            return;
        }

        // ==============================
        // 3️⃣ CALCULAR DAÑO BASE
        // ==============================

        int dañoTotal = habilidad.getDaño();

        // Si el personaje es físico añadimos su daño físico
        if (atacante instanceof com.personajesvideojuegos.modelo.Personajes.PersonajeFisico) {

            var fisico = (com.personajesvideojuegos.modelo.Personajes.PersonajeFisico) atacante;
            dañoTotal += fisico.calcularDanioFisico();
        }

        // CRÍTICO

        if (dado == 5) {

            System.out.println("¡GOLPE CRÍTICO!");

            dañoTotal = (int) (dañoTotal * 1.5); // 50% más daño
        }

        // Guardamos la salud del defensor antes de recibir daño
        // Esto nos permite calcular el daño real aplicado.
        int saludAntes = defensor.getSalud();

        // Aplicamos el daño de la habilidad al defensor
        defensor.recibirDanio(dañoTotal);

        // Calculamos el daño realmente recibido
        // (puede variar si la armadura reduce parte del daño)
        int dañoReal = saludAntes - defensor.getSalud();

        // Mostramos el daño final causado
        System.out.println("Daño causado: " + dañoReal);

        // Mostramos la salud restante del defensor
        System.out.println("Salud restante de " + defensor.getNombre() + ": "
                + defensor.getSalud());

        // ==============================
        // 4️⃣ SI ES MÁGICO → MOSTRAR MANA
        // ==============================
        if (esMagico) {

            // Volvemos a convertir el atacante a PersonajeMagico
            var magico = (com.personajesvideojuegos.modelo.Personajes.PersonajeMagico) atacante;

            // Mostramos el mana restante después de haber gastado el coste
            System.out.println("Mana restante: " + magico.getMana());
        }
    }

    /**
     * Permite usar defensa si el personaje implementa Defensor.
     * Incluye cooldown de 3 turnos.
     */
    private void usarDefensa(Personaje atacante) {

        int turnoActual;
        int ultimoTurno;

        if (atacante == jugador1) {
            turnoActual = turnosJugador1;
            ultimoTurno = ultimoTurnoDefensaJ1;
        } else {
            turnoActual = turnosJugador2;
            ultimoTurno = ultimoTurnoDefensaJ2;
        }

        if (turnoActual - ultimoTurno < 3) {
            System.out.println("Defensa en cooldown.");
            return;
        }

        if (atacante instanceof Defensor) {

            Defensa defensa = ((Defensor) atacante).defender();
            defensa.realizarAccion(atacante);

            if (atacante == jugador1) {
                ultimoTurnoDefensaJ1 = turnosJugador1;
            } else {
                ultimoTurnoDefensaJ2 = turnosJugador2;
            }

            System.out.println(atacante.getNombre() + " aumenta su defensa.");

        } else {
            System.out.println("Este personaje no puede defender.");
        }
    }

    /**
     * Uso de consumibles.
     */
    private void usarConsumible(Personaje personaje) {

        if (personaje.getInventario().isEmpty()) {
            System.out.println("No tienes consumibles.");
            return;
        }

        for (int i = 0; i < personaje.getInventario().size(); i++) {
            System.out.println((i + 1) + " - "
                    + personaje.getInventario().get(i).getNombre());
        }

        System.out.println("0 - Volver");

        System.out.print("Selecciona consumible: ");
        int opcion = scanner.nextInt();

        if (opcion == 0) {
            return;
        }

        // Ajustar índice
        int index = opcion - 1;

        if (index < 0 || index >= personaje.getInventario().size()) {
            System.out.println("Opción inválida.");
            return;
        }

        personaje.usarConsumible(index, personaje);
    }

    /**
     * Tirada de iniciativa entre los dos jugadores.
     * Retorna el jugador que comienza atacando.
     */
    private Personaje tirarIniciativa() {

        int dado1;
        int dado2;

        do {
            dado1 = random.nextInt(20) + 1;
            dado2 = random.nextInt(20) + 1;
        } while (dado1 == dado2);

        return (dado1 > dado2) ? jugador1 : jugador2;
    }
}