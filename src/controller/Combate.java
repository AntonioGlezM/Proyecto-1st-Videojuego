package controller;

import java.util.Random;

import model.personajes.*;
import model.acciones.Defensa;
import model.habilidades.Habilidad;
import model.interfaces.Defensor;
import model.interfaces.HabilidadEspecial;
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

    // Último turno en que se usó la habilidad especial (cooldown de 3 turnos)
    private int ultimoTurnoEspecialJ1 = -3;
    private int ultimoTurnoEspecialJ2 = -3;

    // ==============================
    // FLAGS DE ESTADO: SIGILO
    // Indican si cada jugador está en sigilo durante el turno actual.
    // Si un jugador está en sigilo, el rival no puede atacarle ese turno.
    // El flag se activa cuando el jugador usa "Sigilo" y se desactiva
    // automáticamente al inicio del siguiente turno de ese jugador.
    // ==============================
    private boolean enSigiloJ1 = false;
    private boolean enSigiloJ2 = false;

    // ==============================
    // FLAGS DE ESTADO: FURIA (Bárbaro)
    // Indican si el Bárbaro está en modo furia.
    // Mientras está activa: más daño pero menos armadura.
    // Se revierte automáticamente al inicio del siguiente turno del Bárbaro.
    // ==============================
    private boolean enFuriaJ1 = false;
    private boolean enFuriaJ2 = false;

    // Valores originales de poderBase y armadura del Bárbaro antes de entrar en
    // furia.
    // Se guardan para poder revertir los cambios al turno siguiente.
    private int poderBaseOriginalJ1 = 0;
    private int poderBaseOriginalJ2 = 0;
    private int armaduraOriginalJ1 = 0;
    private int armaduraOriginalJ2 = 0;

    // ==============================
    // FLAGS DE ESTADO: POSTURA DEFENSIVA (Guerrero / Paladín)
    // Indica si el personaje activó la postura defensiva el turno anterior.
    // Se revierte el bonus de armadura al inicio del siguiente turno.
    // ==============================
    private boolean enPosturaJ1 = false;
    private boolean enPosturaJ2 = false;

    // Armadura original antes de activar la postura defensiva.
    private int armaduraPosturaJ1 = 0;
    private int armaduraPosturaJ2 = 0;

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
                return new model.personajes.Picaro(
                        nombre, 500, 20, "Humano", 25);

            case 3:
                return new model.personajes.Barbaro(
                        nombre, 600, 18, "Orco", 30);

            case 4:
                /*
                 * return new model.personajes.Clerigo(
                 * nombre, 100, 12, "Humano", 25, 100, 10);
                 */

            case 5:
                /*
                 * return new model.personajes.Mago(
                 * nombre, 80, 10, "Humano", 30, 120);
                 */

            case 6:
                /*
                 * return new model.personajes.Paladin(
                 * nombre, 130, 14, "Humano", 18, 80);
                 */

            case 7:
                return new model.personajes.Picaro(
                        nombre, 500, 16, "Humano", 22);

            default:
                view.mostrarPersonajeInvalido();
                return new model.personajes.Picaro(
                        nombre, 900, 15, "Humano", 20);
        }
    }

    /**
     * Inicia el combate hasta que uno de los jugadores sea derrotado.
     */
    public void iniciarCombate() {

        view.mostrarInicioCombate();

        // Cada jugador recibe sus consumibles iniciales
        jugador1.cargarConsumiblesIniciales();
        jugador2.cargarConsumiblesIniciales();

        // Tirada de iniciativa para decidir quién comienza
        Personaje atacante = tirarIniciativa();
        Personaje defensor = (atacante == jugador1) ? jugador2 : jugador1;

        view.mostrarIniciativa(atacante.getNombre());

        // Bucle principal del combate
        while (jugador1.estaVivo() && jugador2.estaVivo()) {

            view.mostrarCabeceraTurno(atacante);

            // ==============================
            // REVERTIR EFECTOS DEL TURNO ANTERIOR
            // Al inicio de cada turno del atacante, se revierten los efectos
            // temporales que activó en su turno anterior (furia, postura, sigilo).
            // ==============================
            revertirEfectosTemporales(atacante);

            // Incrementar el contador de turnos del atacante
            if (atacante == jugador1) {
                turnosJugador1++;
            } else {
                turnosJugador2++;
            }

            // ==============================
            // COMPROBAR SIGILO DEL DEFENSOR
            // Si el defensor está en sigilo, el atacante no puede atacarle.
            // Se le informa y se pasa el turno automáticamente.
            // ==============================
            boolean defensorEnSigilo = (defensor == jugador1) ? enSigiloJ1 : enSigiloJ2;

            if (defensorEnSigilo) {
                view.mostrarDefensorEnSigilo(defensor.getNombre());
            } else {
                // Ejecutar la acción elegida por el atacante
                ejecutarTurno(atacante, defensor);
            }

            // Comprobar si el defensor ha muerto
            if (!defensor.estaVivo()) {
                view.mostrarDerrota(defensor.getNombre());
                break;
            }

            // Intercambiar roles: el defensor pasa a ser atacante
            Personaje temp = atacante;
            atacante = defensor;
            defensor = temp;
        }

        view.mostrarFinCombate();
    }

    /**
     * Revierte los efectos temporales del turno anterior del personaje dado.
     *
     * Se llama al inicio de cada turno del personaje para deshacer:
     * - La Furia del Bárbaro (restaura poderBase y armadura originales).
     * - La Postura Defensiva del Guerrero/Paladín (restaura armadura original).
     * - El Sigilo del Asesino/Pícaro (desactiva el flag).
     *
     * @param personaje El personaje cuyo turno acaba de comenzar.
     */
    private void revertirEfectosTemporales(Personaje personaje) {

        boolean esJ1 = (personaje == jugador1);

        // ==============================
        // REVERTIR FURIA
        // Si el Bárbaro estaba en furia el turno anterior,
        // restauramos su poderBase y armadura a los valores originales.
        // ==============================
        if (esJ1 && enFuriaJ1) {
            jugador1.setPoderBase(poderBaseOriginalJ1);
            jugador1.setValorArmadura(armaduraOriginalJ1);
            enFuriaJ1 = false;
            view.mostrarFuriaRevertida(jugador1.getNombre());

        } else if (!esJ1 && enFuriaJ2) {
            jugador2.setPoderBase(poderBaseOriginalJ2);
            jugador2.setValorArmadura(armaduraOriginalJ2);
            enFuriaJ2 = false;
            view.mostrarFuriaRevertida(jugador2.getNombre());
        }

        // ==============================
        // REVERTIR POSTURA DEFENSIVA
        // Si el Guerrero/Paladín activó postura defensiva el turno anterior,
        // restauramos su armadura al valor original.
        // ==============================
        if (esJ1 && enPosturaJ1) {
            jugador1.setValorArmadura(armaduraPosturaJ1);
            enPosturaJ1 = false;
            view.mostrarPosturaRevertida(jugador1.getNombre());

        } else if (!esJ1 && enPosturaJ2) {
            jugador2.setValorArmadura(armaduraPosturaJ2);
            enPosturaJ2 = false;
            view.mostrarPosturaRevertida(jugador2.getNombre());
        }

        // ==============================
        // REVERTIR SIGILO
        // El sigilo se desactiva al inicio del turno del personaje que lo activó.
        // ==============================
        if (esJ1 && enSigiloJ1) {
            enSigiloJ1 = false;
        } else if (!esJ1 && enSigiloJ2) {
            enSigiloJ2 = false;
        }
    }

    /**
     * Ejecuta la acción del jugador en su turno.
     */
    private void ejecutarTurno(Personaje atacante, Personaje defensor) {

        // Obtenemos el nombre de la habilidad especial del personaje
        // para que la Vista pueda mostrarlo dinámicamente en el menú.
        String nombreEspecial = obtenerNombreHabilidadEspecial(atacante);

        view.mostrarMenuAcciones(nombreEspecial);
        int opcion = view.leerOpcion();

        switch (opcion) {

            case 1:
                // Menú dinámico de habilidades
                menuHabilidades(atacante, defensor);
                break;

            case 2:
                // Usar habilidad especial del personaje
                usarHabilidadEspecial(atacante);
                break;

            case 3:
                // Usar un consumible del inventario
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
     * Devuelve el nombre de la habilidad especial del personaje.
     * Si el personaje no implementa HabilidadEspecial, devuelve un texto genérico.
     * Este nombre se pasa a la Vista para mostrarlo en el menú de acciones.
     *
     * @param personaje El personaje cuyo turno es.
     * @return Nombre de su habilidad especial.
     */
    private String obtenerNombreHabilidadEspecial(Personaje personaje) {

        // Comprobamos si el personaje implementa la interfaz HabilidadEspecial.
        // Si es así, obtenemos el nombre directamente mediante polimorfismo.
        if (personaje instanceof HabilidadEspecial) {
            return ((HabilidadEspecial) personaje).getNombreHabilidadEspecial();
        }

        // Si el personaje no tiene habilidad especial, mostramos un texto genérico.
        return "Sin habilidad especial";
    }

    /**
     * Gestiona el uso de la habilidad especial del personaje.
     *
     * Comprueba el cooldown (3 turnos entre usos).
     * Según el tipo de habilidad especial del personaje:
     * - Sigilo → activa el flag enSigilo del jugador correspondiente.
     * - Furia → guarda valores originales y aplica el bonus/penalización.
     * - Postura → guarda armadura original y aplica el bonus.
     *
     * @param atacante El personaje que usa su habilidad especial.
     */
    private void usarHabilidadEspecial(Personaje atacante) {

        // Comprobamos si el personaje tiene habilidad especial
        if (!(atacante instanceof HabilidadEspecial)) {
            view.mostrarSinHabilidadEspecial();
            return;
        }

        boolean esJ1 = (atacante == jugador1);
        int turnoActual = esJ1 ? turnosJugador1 : turnosJugador2;
        int ultimoTurno = esJ1 ? ultimoTurnoEspecialJ1 : ultimoTurnoEspecialJ2;

        // Comprobamos el cooldown: deben haber pasado al menos 3 turnos desde el último
        // uso
        if (turnoActual - ultimoTurno < 3) {
            view.mostrarHabilidadEspecialEnCooldown();
            return;
        }

        HabilidadEspecial habilidadEspecial = (HabilidadEspecial) atacante;
        String nombre = habilidadEspecial.getNombreHabilidadEspecial();

        // ==============================
        // SIGILO (Asesino / Pícaro)
        // Activamos el flag de sigilo del jugador correspondiente.
        // El rival no podrá atacarle en su siguiente turno.
        // ==============================
        if (nombre.equals("Sigilo")) {

            if (esJ1) {
                enSigiloJ1 = true;
            } else {
                enSigiloJ2 = true;
            }

            view.mostrarSigiloActivado(atacante.getNombre());

            // ==============================
            // FURIA BERSERKER (Bárbaro)
            // Guardamos los valores originales y aplicamos el bonus de furia.
            // Se revertirán al inicio del siguiente turno del Bárbaro.
            // ==============================
        } else if (nombre.equals("Furia Berserker")) {

            if (esJ1) {
                // Guardamos los valores originales antes de modificarlos
                poderBaseOriginalJ1 = jugador1.getPoderBase();
                armaduraOriginalJ1 = jugador1.getValorArmadura();
                enFuriaJ1 = true;
            } else {
                poderBaseOriginalJ2 = jugador2.getPoderBase();
                armaduraOriginalJ2 = jugador2.getValorArmadura();
                enFuriaJ2 = true;
            }

            // Aplicamos el efecto de furia (más daño, menos armadura)
            habilidadEspecial.usarHabilidadEspecial(atacante);
            view.mostrarFuriaActivada(atacante.getNombre());

            // ==============================
            // POSTURA DEFENSIVA (Guerrero / Paladín)
            // Guardamos la armadura original y aplicamos el bonus defensivo.
            // Se revertirá al inicio del siguiente turno del personaje.
            // ==============================
        } else {

            if (esJ1) {
                // Guardamos la armadura original antes del bonus
                armaduraPosturaJ1 = jugador1.getValorArmadura();
                enPosturaJ1 = true;
            } else {
                armaduraPosturaJ2 = jugador2.getValorArmadura();
                enPosturaJ2 = true;
            }

            // Aplicamos el efecto defensivo
            habilidadEspecial.usarHabilidadEspecial(atacante);
            view.mostrarPosturaActivada(atacante.getNombre());
        }

        // Actualizamos el turno en que se usó la habilidad especial (para el cooldown)
        if (esJ1) {
            ultimoTurnoEspecialJ1 = turnosJugador1;
        } else {
            ultimoTurnoEspecialJ2 = turnosJugador2;
        }
    }

    /**
     * Menú dinámico de habilidades del personaje.
     * Se obtiene la lista de habilidades del atacante y se muestra.
     */
    private void menuHabilidades(Personaje atacante, Personaje defensor) {

        var habilidades = atacante.getHabilidades();

        if (habilidades.isEmpty()) {
            view.mostrarSinHabilidades();
            return;
        }

        view.mostrarMenuHabilidades(habilidades);
        int opcion = view.leerOpcion();

        if (opcion == 0)
            return;

        if (opcion < 1 || opcion > habilidades.size()) {
            view.mostrarHabilidadInvalida();
            return;
        }

        ejecutarHabilidad(atacante, defensor, habilidades.get(opcion - 1));
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
            if (!magico.consumirMana(habilidad.getCoste())) {

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

        // Mostramos el daño final causado
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
     * Permite usar un consumible del inventario del personaje.
     */
    private void usarConsumible(Personaje personaje) {

        if (personaje.getInventario().isEmpty()) {
            view.mostrarInventarioVacio();
            return;
        }

        view.mostrarMenuConsumibles(personaje.getInventario());
        int opcion = view.leerOpcion();

        if (opcion == 0)
            return;

        // Ajustar índice
        int index = opcion - 1;

        if (index < 0 || index >= personaje.getInventario().size()) {
            view.mostrarOpcionInvalida();
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