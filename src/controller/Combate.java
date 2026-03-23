package controller;

import java.util.Random;

import model.personajes.*;
import model.acciones.Defensa;
import model.habilidades.Habilidad;
import model.interfaces.Defensor;
import model.interfaces.HabilidadEspecial;
import model.resultado.ResultadoTurno;
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
    // FLAGS DE ESTADO: SIGILO (Asesino / Pícaro)
    // Indican si cada jugador está en sigilo durante el turno actual.
    // Si un jugador está en sigilo, el rival no puede atacarle ese turno.
    // El flag se activa cuando el jugador usa "Sigilo" y se desactiva
    // automáticamente al inicio del siguiente turno de ese jugador.
    // ==============================
    private boolean enSigiloJ1 = false;
    private boolean enSigiloJ2 = false;

    // ==============================
    // FLAGS DE ESTADO: FURIA BERSERKER (Bárbaro)
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

    // ==============================
    // FLAGS DE ESTADO: DISTORSIÓN TEMPORAL (Mago)
    // Indica si el Mago puede atacar dos veces en este turno.
    // Se activa el turno anterior y se desactiva tras ejecutar el doble ataque.
    // ==============================
    private boolean dobleAtaqueJ1 = false;
    private boolean dobleAtaqueJ2 = false;

    // ==============================
    // FLAGS DE ESTADO: HIMNO DIVINO (Clérigo)
    // Indica si el próximo ataque del Clérigo dejará quemadura sagrada.
    // himnoDivinoActivo → el siguiente ataque activará la quemadura.
    // danioQuemadura → 20% del daño real causado por el ataque que activó la
    // quemadura.
    // turnosQuemadura → turnos restantes de quemadura sobre el defensor (máx. 2).
    // La quemadura se aplica al inicio del turno del personaje quemado.
    // ==============================
    private boolean himnoDivinoActivoJ1 = false;
    private boolean himnoDivinoActivoJ2 = false;

    // Daño de quemadura pendiente de aplicar sobre cada jugador
    private int danioQuemaduraJ1 = 0;
    private int danioQuemaduraJ2 = 0;

    // Turnos restantes de quemadura sobre cada jugador
    private int turnosQuemaduraJ1 = 0;
    private int turnosQuemaduraJ2 = 0;

    // ==============================
    // FLAGS DE ESTADO: FURIA DIVINA (Paladín)
    // El Paladín dedica 2 turnos a cargar. Al completar la carga,
    // su siguiente ataque hace el triple del daño normal.
    // furiaDivinaActiva → el Paladín está cargando o listo para descargar.
    // turnosCargaFuria → turnos de carga restantes (empieza en 2, baja a 0).
    // furiaDivinaLista → la carga está completa, el siguiente ataque hace x3.
    // ==============================
    private boolean furiaDivinaActivaJ1 = false;
    private boolean furiaDivinaActivaJ2 = false;

    private int turnosCargaFuriaJ1 = 0;
    private int turnosCargaFuriaJ2 = 0;

    // Indica que la carga está completa y el próximo ataque hará x3 de daño
    private boolean furiaDivinaListaJ1 = false;
    private boolean furiaDivinaListaJ2 = false;

    // ==============================
    // FLAGS DE ESTADO: MARCA DEL CAZADOR (Cazador)
    // Indica si el defensor está marcado por el Cazador.
    // Mientras la marca esté activa, los ataques del Cazador
    // ignoran completamente la armadura del defensor.
    // turnosMarca → turnos restantes de la marca (máx. 2).
    // ==============================
    private boolean marcaActivaJ1 = false;
    private boolean marcaActivaJ2 = false;

    // Turnos restantes de marca sobre cada jugador (el marcado es el defensor)
    private int turnosMarcaJ1 = 0;
    private int turnosMarcaJ2 = 0;

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
                        nombre, 500, 16, "Humano", 22);
            case 3:
                return new model.personajes.Barbaro(
                        nombre, 600, 18, "Orco", 30);

            case 4:
                return new model.personajes.Clerigo(
                        nombre, 600, 12, "Humano", 120, 20);

            case 5:
                return new model.personajes.Mago(
                        nombre, 450, 10, "Humano", 150, 30);

            case 6:
                return new model.personajes.Paladin(
                        nombre, 700, 14, "Humano", 15, 100, 18);

            case 7:
                return new model.personajes.Cazador(
                        nombre, 550, 17, "Humano", 28);

            default:
                view.mostrarPersonajeInvalido();
                return new model.personajes.Picaro(
                        nombre, 500, 20, "Humano", 25);
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

        // ==============================
        // MOSTRAR STATS INICIALES
        // Se muestran las estadísticas de ambos jugadores antes de empezar
        // para que los jugadores sepan con qué personaje están jugando.
        // ==============================
        view.mostrarStatsIniciales(jugador1, obtenerNombreHabilidadEspecial(jugador1));
        view.mostrarStatsIniciales(jugador2, obtenerNombreHabilidadEspecial(jugador2));

        view.mostrarIniciativa(atacante.getNombre());

        // Bucle principal del combate
        while (jugador1.estaVivo() && jugador2.estaVivo()) {

            // ==============================
            // MOSTRAR CABECERA DEL TURNO CON ESTADOS ACTIVOS
            // Se muestra el turno actual junto a los efectos que tiene
            // activos el personaje (quemadura, marca, furia, etc.)
            // ==============================
            view.mostrarCabeceraTurno(atacante, construirEstadosActivos(atacante));

            // ==============================
            // APLICAR QUEMADURA SAGRADA
            // Si el atacante tiene quemadura activa, se le aplica el daño
            // al inicio de su turno y se reduce el contador de turnos restantes.
            // ==============================
            aplicarQuemadura(atacante);

            // ==============================
            // REVERTIR EFECTOS DEL TURNO ANTERIOR
            // Al inicio de cada turno del atacante, se revierten los efectos
            // temporales que activó en su turno anterior (furia, postura, sigilo).
            // ==============================
            revertirEfectosTemporales(atacante);

            // ==============================
            // REDUCIR TURNOS DE MARCA DEL CAZADOR
            // Si el atacante está marcado, reducimos el contador de la marca.
            // Cuando llega a 0 la marca se desactiva.
            // ==============================
            reducirMarca(atacante);

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

                // ==============================
                // COMPROBAR FURIA DIVINA EN CARGA (Paladín)
                // Si el Paladín está cargando la Furia Divina, su turno se
                // consume automáticamente reduciendo el contador de carga.
                // Cuando llega a 0, la carga se completa y queda listo para disparar.
                // ==============================
            } else if (estaCarrandoFuriaDivina(atacante)) {
                gestionarCargaFuriaDivina(atacante);

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

        // ==============================
        // MOSTRAR VICTORIA
        // Se determina el ganador y se muestra el mensaje final.
        // El bucle de jugar de nuevo lo gestiona Main.
        // ==============================
        Personaje ganador = jugador1.estaVivo() ? jugador1 : jugador2;
        view.mostrarVictoria(ganador.getNombre());
        view.mostrarFinCombate();
    }

    /**
     * Construye un String con los estados activos del personaje.
     * Se usa en la cabecera del turno para informar al jugador
     * de los efectos que tiene activos en ese momento.
     *
     * Estados que se muestran:
     * - Quemadura sagrada (turnos restantes)
     * - Marca del Cazador (turnos restantes)
     * - Furia Berserker activa
     * - Postura Defensiva activa
     * - Distorsión Temporal lista
     * - Furia Divina cargada
     *
     * @param personaje El personaje cuyo turno comienza.
     * @return String con los estados activos, o cadena vacía si no hay ninguno.
     */
    private String construirEstadosActivos(Personaje personaje) {

        boolean esJ1 = (personaje == jugador1);
        StringBuilder estados = new StringBuilder();

        // Quemadura sagrada activa sobre este personaje
        int turnosQuemadura = esJ1 ? turnosQuemaduraJ1 : turnosQuemaduraJ2;
        if (turnosQuemadura > 0) {
            estados.append("🔥 Quemadura sagrada (").append(turnosQuemadura).append(" turnos) | ");
        }

        // Marca del Cazador activa sobre este personaje
        int turnosMarca = esJ1 ? turnosMarcaJ1 : turnosMarcaJ2;
        if (turnosMarca > 0) {
            estados.append("🎯 Marcado (").append(turnosMarca).append(" turnos) | ");
        }

        // Furia Berserker activa
        boolean enFuria = esJ1 ? enFuriaJ1 : enFuriaJ2;
        if (enFuria) {
            estados.append("💢 Furia Berserker | ");
        }

        // Postura Defensiva activa
        boolean enPostura = esJ1 ? enPosturaJ1 : enPosturaJ2;
        if (enPostura) {
            estados.append("🛡️ Postura Defensiva | ");
        }

        // Distorsión Temporal lista (doble ataque disponible)
        boolean dobleAtaque = esJ1 ? dobleAtaqueJ1 : dobleAtaqueJ2;
        if (dobleAtaque) {
            estados.append("⚡ Distorsión Temporal lista | ");
        }

        // Furia Divina cargada y lista para disparar
        boolean furiaDivinaLista = esJ1 ? furiaDivinaListaJ1 : furiaDivinaListaJ2;
        if (furiaDivinaLista) {
            estados.append("💥 Furia Divina cargada | ");
        }

        // Eliminamos el separador final si hay estados
        String resultado = estados.toString();
        if (resultado.endsWith(" | ")) {
            resultado = resultado.substring(0, resultado.length() - 3);
        }

        return resultado;
    }

    /**
     * Aplica el daño de quemadura sagrada al personaje si tiene turnos activos.
     * Se llama al inicio del turno del personaje quemado.
     * Reduce en 1 el contador de turnos restantes tras aplicar el daño.
     *
     * @param personaje El personaje sobre el que se comprueba la quemadura.
     */
    private void aplicarQuemadura(Personaje personaje) {

        boolean esJ1 = (personaje == jugador1);
        int turnosRestantes = esJ1 ? turnosQuemaduraJ1 : turnosQuemaduraJ2;
        int danio = esJ1 ? danioQuemaduraJ1 : danioQuemaduraJ2;

        // Solo aplicamos si hay turnos de quemadura activos
        if (turnosRestantes <= 0)
            return;

        // Aplicamos el daño de quemadura directamente (sin contar armadura,
        // es daño sagrado que atraviesa defensas físicas)
        personaje.setSalud(personaje.getSalud() - danio);

        view.mostrarQuemaduraAplicada(personaje.getNombre(), danio, turnosRestantes - 1);

        // Reducimos el contador de turnos restantes
        if (esJ1) {
            turnosQuemaduraJ1--;
        } else {
            turnosQuemaduraJ2--;
        }
    }

    /**
     * Reduce en 1 el contador de turnos de la Marca del Cazador sobre el personaje.
     * Se llama al inicio del turno del personaje marcado.
     * Cuando el contador llega a 0, la marca se desactiva.
     *
     * @param personaje El personaje sobre el que se comprueba la marca.
     */
    private void reducirMarca(Personaje personaje) {

        boolean esJ1 = (personaje == jugador1);
        int turnosRestantes = esJ1 ? turnosMarcaJ1 : turnosMarcaJ2;

        // Solo reducimos si hay turnos de marca activos
        if (turnosRestantes <= 0)
            return;

        if (esJ1) {
            turnosMarcaJ1--;
            if (turnosMarcaJ1 == 0) {
                marcaActivaJ1 = false;
                view.mostrarMarcaExpirada(personaje.getNombre());
            }
        } else {
            turnosMarcaJ2--;
            if (turnosMarcaJ2 == 0) {
                marcaActivaJ2 = false;
                view.mostrarMarcaExpirada(personaje.getNombre());
            }
        }
    }

    /**
     * Revierte los efectos temporales del turno anterior del personaje dado.
     *
     * Se llama al inicio de cada turno del personaje para deshacer:
     * - La Furia del Bárbaro (restaura poderBase y armadura originales).
     * - La Postura Defensiva del Guerrero/Paladín (restaura armadura original).
     * - El Sigilo del Asesino/Pícaro (desactiva el flag).
     * - La Distorsión Temporal del Mago (desactiva el doble ataque tras usarlo).
     *
     * @param personaje El personaje cuyo turno acaba de comenzar.
     */
    private void revertirEfectosTemporales(Personaje personaje) {

        boolean esJ1 = (personaje == jugador1);

        // ==============================
        // REVERTIR FURIA BERSERKER
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

        // ==============================
        // REVERTIR DISTORSIÓN TEMPORAL
        // El doble ataque se desactiva al inicio del turno siguiente al que se usó,
        // independientemente de si el Mago lo aprovechó o no.
        // ==============================
        if (esJ1 && dobleAtaqueJ1) {
            dobleAtaqueJ1 = false;
        } else if (!esJ1 && dobleAtaqueJ2) {
            dobleAtaqueJ2 = false;
        }
    }

    /**
     * Comprueba si el Paladín está en proceso de carga de Furia Divina.
     *
     * @param personaje El personaje a comprobar.
     * @return true si está cargando (turnosCarga > 0), false en caso contrario.
     */
    private boolean estaCarrandoFuriaDivina(Personaje personaje) {
        boolean esJ1 = (personaje == jugador1);
        int turnosCarga = esJ1 ? turnosCargaFuriaJ1 : turnosCargaFuriaJ2;
        return turnosCarga > 0;
    }

    /**
     * Gestiona un turno de carga de la Furia Divina del Paladín.
     *
     * Reduce el contador de carga en 1. Si llega a 0, la carga se completa
     * y se activa el flag furiaDivinaLista para que el próximo ataque haga x3.
     * Durante la carga el turno se consume automáticamente.
     *
     * @param personaje El Paladín que está cargando.
     */
    private void gestionarCargaFuriaDivina(Personaje personaje) {

        boolean esJ1 = (personaje == jugador1);

        if (esJ1) {
            turnosCargaFuriaJ1--;

            // Si la carga ha terminado, marcamos la furia como lista
            if (turnosCargaFuriaJ1 == 0) {
                furiaDivinaActivaJ1 = false;
                furiaDivinaListaJ1 = true;
                view.mostrarFuriaDivinaLista(personaje.getNombre());
            } else {
                view.mostrarFuriaDivinaCargando(personaje.getNombre(), turnosCargaFuriaJ1);
            }

        } else {
            turnosCargaFuriaJ2--;

            if (turnosCargaFuriaJ2 == 0) {
                furiaDivinaActivaJ2 = false;
                furiaDivinaListaJ2 = true;
                view.mostrarFuriaDivinaLista(personaje.getNombre());
            } else {
                view.mostrarFuriaDivinaCargando(personaje.getNombre(), turnosCargaFuriaJ2);
            }
        }
    }

    /**
     * Ejecuta la acción del jugador en su turno.
     * Si el Mago tiene Distorsión Temporal activa, ejecuta dos acciones de ataque.
     */
    private void ejecutarTurno(Personaje atacante, Personaje defensor) {

        // Obtenemos el nombre de la habilidad especial del personaje
        // para que la Vista pueda mostrarlo dinámicamente en el menú.
        String nombreEspecial = obtenerNombreHabilidadEspecial(atacante);

        boolean esJ1 = (atacante == jugador1);
        boolean tieneDobleAtaque = esJ1 ? dobleAtaqueJ1 : dobleAtaqueJ2;

        // ==============================
        // DISTORSIÓN TEMPORAL: DOBLE ATAQUE
        // Si el Mago tiene el flag de doble ataque activo,
        // ejecutamos el menú de habilidades dos veces seguidas.
        // El segundo ataque también puede fallar o ser crítico de forma independiente.
        // ==============================
        if (tieneDobleAtaque) {
            view.mostrarDobleAtaqueActivo(atacante.getNombre());

            // Primer ataque
            view.mostrarPrimerAtaque();
            menuHabilidades(atacante, defensor);

            // Comprobamos si el defensor sigue vivo antes del segundo ataque
            if (defensor.estaVivo()) {
                view.mostrarSegundoAtaque();
                menuHabilidades(atacante, defensor);
            }

            // Desactivamos el doble ataque tras usarlo
            if (esJ1) {
                dobleAtaqueJ1 = false;
            } else {
                dobleAtaqueJ2 = false;
            }

            return;
        }

        view.mostrarMenuAcciones(nombreEspecial);
        int opcion = view.leerOpcion();

        switch (opcion) {

            case 1:
                // Menú dinámico de habilidades
                menuHabilidades(atacante, defensor);
                break;

            case 2:
                // Usar habilidad especial del personaje
                usarHabilidadEspecial(atacante, defensor);
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
     * Según el tipo de habilidad especial:
     * - Sigilo → activa el flag enSigilo.
     * - Furia Berserker → guarda valores originales y aplica bonus/penalización.
     * - Postura Defensiva → guarda armadura original y aplica bonus.
     * - Distorsión Temporal → activa el flag de doble ataque para el siguiente
     * turno.
     * - Himno Divino → activa el flag para que el próximo ataque deje quemadura.
     * - Furia Divina → inicia la carga de 2 turnos del Paladín.
     * - Marca del Cazador → marca al defensor durante 2 turnos (ignora su
     * armadura).
     *
     * @param atacante El personaje que usa su habilidad especial.
     * @param defensor El rival (necesario para aplicar la Marca del Cazador).
     */
    private void usarHabilidadEspecial(Personaje atacante, Personaje defensor) {

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
                poderBaseOriginalJ1 = jugador1.getPoderBase();
                armaduraOriginalJ1 = jugador1.getValorArmadura();
                enFuriaJ1 = true;
            } else {
                poderBaseOriginalJ2 = jugador2.getPoderBase();
                armaduraOriginalJ2 = jugador2.getValorArmadura();
                enFuriaJ2 = true;
            }
            habilidadEspecial.usarHabilidadEspecial(atacante);
            view.mostrarFuriaActivada(atacante.getNombre());

            // ==============================
            // DISTORSIÓN TEMPORAL (Mago)
            // Activamos el flag de doble ataque para el siguiente turno del Mago.
            // En ese turno podrá ejecutar dos habilidades en lugar de una.
            // ==============================
        } else if (nombre.equals("Distorsión Temporal")) {

            if (esJ1) {
                dobleAtaqueJ1 = true;
            } else {
                dobleAtaqueJ2 = true;
            }
            view.mostrarDistorsionActivada(atacante.getNombre());

            // ==============================
            // HIMNO DIVINO (Clérigo)
            // Activamos el flag para que el próximo ataque del Clérigo
            // deje una quemadura sagrada sobre el defensor.
            // El daño de quemadura se calcula en ejecutarHabilidad() tras el ataque.
            // ==============================
        } else if (nombre.equals("Himno Divino")) {

            if (esJ1) {
                himnoDivinoActivoJ1 = true;
            } else {
                himnoDivinoActivoJ2 = true;
            }
            view.mostrarHimnoDivinoActivado(atacante.getNombre());

            // ==============================
            // FURIA DIVINA (Paladín)
            // Iniciamos la carga: el Paladín dedicará 2 turnos a cargar.
            // Durante la carga no puede actuar. Al completarla, el siguiente
            // ataque hará el triple del daño normal.
            // ==============================
        } else if (nombre.equals("Furia Divina")) {

            if (esJ1) {
                furiaDivinaActivaJ1 = true;
                // La carga dura 1 turno adicional (este turno ya se consume al activar)
                turnosCargaFuriaJ1 = 1;
            } else {
                furiaDivinaActivaJ2 = true;
                turnosCargaFuriaJ2 = 1;
            }
            view.mostrarFuriaDivinaIniciada(atacante.getNombre());

            // ==============================
            // MARCA DEL CAZADOR (Cazador)
            // Marcamos al defensor durante 2 turnos.
            // Los ataques del Cazador ignorarán su armadura mientras dure la marca.
            // La marca se aplica sobre el defensor (no sobre el atacante).
            // ==============================
        } else if (nombre.equals("Marca del Cazador")) {

            boolean defensorEsJ1 = (defensor == jugador1);

            if (defensorEsJ1) {
                marcaActivaJ1 = true;
                turnosMarcaJ1 = 2;
            } else {
                marcaActivaJ2 = true;
                turnosMarcaJ2 = 2;
            }
            view.mostrarMarcaAplicada(atacante.getNombre(), defensor.getNombre());

            // ==============================
            // POSTURA DEFENSIVA (Guerrero)
            // Guardamos la armadura original y aplicamos el bonus defensivo.
            // Se revertirá al inicio del siguiente turno del personaje.
            // ==============================
        } else {

            if (esJ1) {
                armaduraPosturaJ1 = jugador1.getValorArmadura();
                enPosturaJ1 = true;
            } else {
                armaduraPosturaJ2 = jugador2.getValorArmadura();
                enPosturaJ2 = true;
            }
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
     * Ejecuta la habilidad seleccionada y construye un ResultadoTurno
     * con toda la información generada durante el ataque.
     *
     * El ResultadoTurno acumula mensajes, daño, estados y flags de eventos
     * y se entrega a la Vista de una sola vez al final del método.
     * Esto facilita la transición al front gráfico: la vista recibirá
     * un único objeto con todo lo que necesita renderizar.
     *
     * Gestiona también los efectos post-ataque:
     * - Si el Clérigo tiene Himno Divino activo, aplica quemadura al defensor.
     * - Si el Paladín tiene Furia Divina lista, triplica el daño del ataque.
     * - Si el defensor está marcado por el Cazador, el ataque ignora su armadura.
     */
    private void ejecutarHabilidad(Personaje atacante,
            Personaje defensor,
            Habilidad habilidad) {

        // ==============================
        // CREAR EL RESULTADO DEL TURNO
        // Acumularemos aquí toda la información generada durante el ataque.
        // Al final lo entregamos a la vista de una sola vez.
        // ==============================
        ResultadoTurno resultado = new ResultadoTurno();
        resultado.setNombreDefensor(defensor.getNombre());

        // Comprobamos si el personaje que ataca es de tipo PersonajeMagico
        // Esto es necesario porque solo los mágicos tienen sistema de mana.
        boolean esMagico = atacante instanceof model.personajes.PersonajeMagico;

        // ==============================
        // 1️⃣ SI ES MÁGICO → CONSUMIR MANA
        // ==============================
        if (esMagico) {

            // Convertimos el objeto atacante a PersonajeMagico
            // (Casting) para poder usar sus métodos específicos como consumirMana().
            var magico = (model.personajes.PersonajeMagico) atacante;

            // Intentamos gastar el mana necesario según el coste de la habilidad.
            // El método consumirMana() devuelve:
            // - true → si hay suficiente mana (y lo resta)
            // - false → si no hay suficiente mana
            if (!magico.consumirMana(habilidad.getCoste())) {

                // Si no hay mana suficiente, se informa al jugador
                // La habilidad NO se ejecuta.
                resultado.agregarMensaje("No tienes suficiente mana.");
                view.mostrarResultadoTurno(resultado);
                return;
            }
        }

        resultado.agregarMensaje("Usando habilidad: " + habilidad.getNombre());

        int dado = random.nextInt(6); // 0-5

        if (dado == 0 || dado == 1) {
            // El ataque falla: registramos el evento y entregamos el resultado
            resultado.setAtaqueFallado(true);
            resultado.agregarMensaje("El ataque ha FALLADO.");
            view.mostrarResultadoTurno(resultado);
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
            resultado.setGolpeCritico(true);
            resultado.agregarMensaje("¡GOLPE CRÍTICO!");
            dañoTotal = (int) (dañoTotal * 1.5); // 50% más daño
        }

        // ==============================
        // FURIA DIVINA: TRIPLICAR DAÑO
        // Si el Paladín tiene la Furia Divina lista (carga completa),
        // el daño de este ataque se triplica y se desactiva el flag.
        // ==============================
        boolean esJ1 = (atacante == jugador1);
        boolean furiaDivinaLista = esJ1 ? furiaDivinaListaJ1 : furiaDivinaListaJ2;

        if (furiaDivinaLista) {
            dañoTotal = dañoTotal * 3;
            resultado.agregarMensaje("💥 ¡" + atacante.getNombre()
                    + " descarga la FURIA DIVINA! Daño devastador: " + dañoTotal);

            // Desactivamos el flag tras el golpe
            if (esJ1) {
                furiaDivinaListaJ1 = false;
            } else {
                furiaDivinaListaJ2 = false;
            }
        }

        // ==============================
        // MARCA DEL CAZADOR: IGNORAR ARMADURA
        // Si el defensor está marcado por el Cazador,
        // guardamos su armadura, la ponemos a 0 para el ataque
        // y la restauramos inmediatamente después.
        // ==============================
        boolean defensorEsJ1 = (defensor == jugador1);
        boolean defensorMarcado = defensorEsJ1 ? marcaActivaJ1 : marcaActivaJ2;

        int armaduraOriginal = defensor.getValorArmadura();

        if (defensorMarcado) {
            // Anulamos temporalmente la armadura del defensor
            defensor.setValorArmadura(0);
            resultado.agregarMensaje("🎯 ¡El ataque ignora la armadura de " + defensor.getNombre() + "!");
        }

        // Guardamos la salud del defensor antes de recibir daño
        // Esto nos permite calcular el daño real aplicado.
        int saludAntes = defensor.getSalud();

        // Aplicamos el daño de la habilidad al defensor
        defensor.recibirDanio(dañoTotal);

        // Restauramos la armadura del defensor tras el ataque (si estaba marcado)
        if (defensorMarcado) {
            defensor.setValorArmadura(armaduraOriginal);
        }

        // Calculamos el daño realmente recibido
        // (puede variar si la armadura reduce parte del daño)
        int dañoReal = saludAntes - defensor.getSalud();

        // Registramos el daño y la salud restante en el resultado
        resultado.setDañoReal(dañoReal);
        resultado.setSaludDefensor(defensor.getSalud());
        resultado.agregarMensaje("Daño causado: " + dañoReal);
        resultado.agregarMensaje("Salud restante de " + defensor.getNombre() + ": " + defensor.getSalud());

        // ==============================
        // HIMNO DIVINO: APLICAR QUEMADURA SAGRADA
        // Si el Clérigo tiene el Himno Divino activo, este ataque
        // deja una quemadura que hará el 20% del daño real durante 2 turnos.
        // El daño de quemadura se aplica sobre el defensor (no el atacante).
        // ==============================
        boolean himnoDivinoActivo = esJ1 ? himnoDivinoActivoJ1 : himnoDivinoActivoJ2;

        if (himnoDivinoActivo && dañoReal > 0) {

            // Calculamos el 20% del daño real causado por el ataque
            int danioQuemadura = (int) (dañoReal * 0.20);

            // Determinamos si el defensor es J1 o J2 para guardar la quemadura
            // correctamente
            if (defensorEsJ1) {
                danioQuemaduraJ1 = danioQuemadura;
                turnosQuemaduraJ1 = 2;
            } else {
                danioQuemaduraJ2 = danioQuemadura;
                turnosQuemaduraJ2 = 2;
            }

            // Desactivamos el himno (se usa una sola vez por activación)
            if (esJ1) {
                himnoDivinoActivoJ1 = false;
            } else {
                himnoDivinoActivoJ2 = false;
            }

            resultado.agregarMensaje("🔥 " + defensor.getNombre() + " sufre " + danioQuemadura
                    + " de quemadura sagrada. (2 turnos restantes)");
        }

        // ==============================
        // 4️⃣ SI ES MÁGICO → MOSTRAR MANA
        // ==============================
        if (esMagico) {

            // Volvemos a convertir el atacante a PersonajeMagico
            var magico = (model.personajes.PersonajeMagico) atacante;

            // Mostramos el mana restante después de haber gastado el coste
            resultado.agregarMensaje("Mana restante: " + magico.getMana());
        }

        // ==============================
        // ENTREGAR EL RESULTADO A LA VISTA
        // Una vez acumulada toda la información del ataque,
        // la enviamos a la vista de una sola vez.
        // En consola imprime los mensajes uno a uno.
        // En un front gráfico este método renderizará todo de golpe.
        // ==============================
        view.mostrarResultadoTurno(resultado);
    }

    /**
     * Permite usar un consumible del inventario del personaje.
     * Si el personaje es físico y usa una Poción de Maná, se le informa
     * de que no tiene efecto y se le devuelve el consumible.
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

        // ==============================
        // POCIÓN DE MANÁ EN PERSONAJE FÍSICO
        // Si el personaje es físico y selecciona una Poción de Maná,
        // no tiene ningún efecto ya que los físicos no tienen maná.
        // Se le informa y NO se consume el ítem.
        // ==============================
        String nombreConsumible = personaje.getInventario().get(index).getNombre();
        boolean esFisico = personaje instanceof model.personajes.PersonajeFisico;

        if (esFisico && nombreConsumible.equalsIgnoreCase("Poción de Maná")) {
            view.mostrarPocionManaInutil();
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