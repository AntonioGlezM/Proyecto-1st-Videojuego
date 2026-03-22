package controller;

import java.util.Random;

import model.personajes.*;
import com.personajesvideojuegos.modelo.Acciones.Defensa;
import model.habilidades.Habilidad;
import com.personajesvideojuegos.modelo.capacidades.Defensor;
import view.View;

/**
 * Controlador del combate PvP por turnos.
 *
 * Responsabilidades:
 * - Lógica del combate (iniciativa, turnos, cooldowns).
 * - Orquestar las acciones: ataque, defensa, consumibles.
 * - Delegar toda la presentación en la Vista.
 *
 * NO imprime nada por pantalla directamente.
 * NO recoge input del usuario directamente.
 *
 * @author Antonio Gonzalez Martel
 */
public class Combate {

    private Personaje jugador1;
    private Personaje jugador2;

    private final Random random = new Random();
    private final View view;

    // Contadores de turnos
    private int turnosJugador1 = 0;
    private int turnosJugador2 = 0;

    // Último turno en que se usó defensa (cooldown de 3 turnos)
    private int ultimoTurnoDefensaJ1 = -3;
    private int ultimoTurnoDefensaJ2 = -3;

    public Combate(View view) {
        this.view = view;
        this.jugador1 = seleccionarPersonaje("Jugador 1");
        this.jugador2 = seleccionarPersonaje("Jugador 2");
    }

    /**
     * Permite al usuario seleccionar un personaje.
     */
    private Personaje seleccionarPersonaje(String jugador) {

        view.mostrarMenuSeleccionPersonaje(jugador);
        int opcion = view.leerSeleccionPersonaje();

        view.pedirNombrePersonaje();
        String nombre = view.leerNombrePersonaje();

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
                return new model.personajes.Picaro(
                        nombre, 500, 16, "Humano", 22);

            default:
                view.mostrarPersonajeInvalido();
                return new model.personajes.Asesino(
                        nombre, 900, 15, "Humano", 20);
        }
    }

    /**
     * Inicia el combate hasta que uno de los jugadores sea derrotado.
     */
    public void iniciarCombate() {

        view.mostrarInicioCombate();

        jugador1.cargarConsumiblesIniciales();
        jugador2.cargarConsumiblesIniciales();

        Personaje atacante = tirarIniciativa();
        Personaje defensor = (atacante == jugador1) ? jugador2 : jugador1;

        view.mostrarIniciativa(atacante.getNombre());

        while (jugador1.estaVivo() && jugador2.estaVivo()) {

            view.mostrarCabeceraTurno(atacante);

            if (atacante == jugador1) {
                turnosJugador1++;
            } else {
                turnosJugador2++;
            }

            ejecutarTurno(atacante, defensor);

            if (!defensor.estaVivo()) {
                view.mostrarDerrota(defensor.getNombre());
                break;
            }

            Personaje temp = atacante;
            atacante = defensor;
            defensor = temp;
        }

        view.mostrarFinCombate();
    }

    /**
     * Ejecuta la acción del jugador en su turno.
     */
    private void ejecutarTurno(Personaje atacante, Personaje defensor) {

        view.mostrarMenuAcciones();
        int opcion = view.leerOpcion();

        switch (opcion) {

            case 1:
                menuHabilidades(atacante, defensor);
                break;

            case 2:
                usarDefensa(atacante);
                break;

            case 3:
                usarConsumible(atacante);
                break;

            case 4:
                view.mostrarTurnoPasado();
                break;

            default:
                view.mostrarOpcionInvalida();
        }
    }

    /**
     * Menú dinámico de habilidades del personaje.
     */
    private void menuHabilidades(Personaje atacante, Personaje defensor) {

        var habilidades = atacante.getHabilidades();

        if (habilidades.isEmpty()) {
            view.mostrarSinHabilidades();
            return;
        }

        view.mostrarMenuHabilidades(habilidades);
        int opcion = view.leerOpcion();

        if (opcion == 0) return;

        if (opcion < 1 || opcion > habilidades.size()) {
            view.mostrarHabilidadInvalida();
            return;
        }

        ejecutarHabilidad(atacante, defensor, habilidades.get(opcion - 1));
    }

    /**
     * Ejecuta la habilidad seleccionada aplicando toda la lógica de combate.
     */
    private void ejecutarHabilidad(Personaje atacante, Personaje defensor, Habilidad habilidad) {

        // Comprobamos si el personaje que ataca es de tipo PersonajeMagico
        // Esto es necesario porque solo los mágicos tienen sistema de mana.
        boolean esMagico = atacante instanceof model.personajes.PersonajeMagico;

        // ==============================
        // 1️⃣ SI ES MÁGICO → CONSUMIR MANA
        // ==============================
        if (esMagico) {

            // Convertimos el objeto atacante a PersonajeMagico
            // (Casting) para poder usar sus métodos específicos como usarMana().
            var magico = (model.personajes.PersonajeMagico) atacante;

            // Intentamos gastar el mana necesario según el coste de la habilidad.
            // El método usarMana() devuelve:
            // - true → si hay suficiente mana (y lo resta)
            // - false → si no hay suficiente mana
            if (!magico.usarMana(habilidad.getCoste())) {

                // Si no hay mana suficiente, se informa al jugador
                // La habilidad NO se ejecuta.
                view.mostrarManaInsuficiente();
                return;
            }
        }

        view.mostrarUsoHabilidad(habilidad.getNombre());

        int dado = random.nextInt(6); // 0-5

        if (dado == 0 || dado == 1) {
            view.mostrarAtaqueFallado();
            return;
        }

        // ==============================
        // 3️⃣ CALCULAR DAÑO BASE
        // ==============================

        int dañoTotal = habilidad.getDaño();

        // Si el personaje es físico añadimos su daño físico
        if (atacante instanceof model.personajes.PersonajeFisico) {
            var fisico = (model.personajes.PersonajeFisico) atacante;
            dañoTotal += fisico.calcularDanioFisico();
        }

        // CRÍTICO
        if (dado == 5) {
            view.mostrarGolpeCritico();
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

        view.mostrarResultadoAtaque(dañoReal, defensor.getNombre(), defensor.getSalud());

        // ==============================
        // 4️⃣ SI ES MÁGICO → MOSTRAR MANA
        // ==============================
        if (esMagico) {

            // Volvemos a convertir el atacante a PersonajeMagico
            var magico = (model.personajes.PersonajeMagico) atacante;

            // Mostramos el mana restante después de haber gastado el coste
            view.mostrarManaRestante(magico.getMana());
        }
    }

    /**
     * Usa la defensa del personaje si implementa Defensor y no está en cooldown.
     */
    private void usarDefensa(Personaje atacante) {

        int turnoActual = (atacante == jugador1) ? turnosJugador1 : turnosJugador2;
        int ultimoTurno = (atacante == jugador1) ? ultimoTurnoDefensaJ1 : ultimoTurnoDefensaJ2;

        if (turnoActual - ultimoTurno < 3) {
            view.mostrarDefensaEnCooldown();
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

            view.mostrarDefensaAplicada(atacante.getNombre());

        } else {
            view.mostrarSinCapacidadDefensa();
        }
    }

    /**
     * Permite usar un consumible del inventario del personaje.
     */
    private void usarConsumible(Personaje personaje) {

        if (personaje.getInventario().isEmpty()) {
            view.mostrarInventarioVacio();
            return;
        }

        view.mostrarMenuConsumibles(personaje.getInventario());
        int opcion = view.leerOpcion();

        if (opcion == 0) return;

        int index = opcion - 1;

        if (index < 0 || index >= personaje.getInventario().size()) {
            view.mostrarOpcionInvalida();
            return;
        }

        personaje.usarConsumible(index, personaje);
    }

    /**
     * Tirada de iniciativa entre los dos jugadores.
     */
    private Personaje tirarIniciativa() {

        int dado1, dado2;

        do {
            dado1 = random.nextInt(20) + 1;
            dado2 = random.nextInt(20) + 1;
        } while (dado1 == dado2);

        return (dado1 > dado2) ? jugador1 : jugador2;
    }
}