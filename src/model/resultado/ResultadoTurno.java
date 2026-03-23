package model.resultado;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase que representa el resultado completo de un turno de combate.
 *
 * En lugar de que el controlador llame a métodos de la vista uno por uno,
 * acumula toda la información generada durante un turno y la entrega
 * a la vista de una sola vez al final.
 *
 * Esto facilita enormemente la transición al front gráfico (JavaFX, Swing, etc.),
 * ya que la vista recibirá un objeto con todo lo que necesita renderizar
 * sin depender del orden de llamadas del controlador.
 *
 * Información que contiene:
 * - Mensajes generales del turno (en orden cronológico).
 * - Daño causado y salud restante del defensor.
 * - Si hubo golpe crítico, fallo, o efectos especiales.
 * - Estados activos del personaje al inicio del turno.
 * - Si el combate ha terminado y quién ganó.
 *
 * @author Antonio González Martel
 */
public class ResultadoTurno {

    // ==============================
    // MENSAJES DEL TURNO
    // Lista de mensajes generados durante el turno, en orden cronológico.
    // La vista los mostrará uno a uno o todos a la vez según el front.
    // ==============================
    private final List<String> mensajes = new ArrayList<>();

    // ==============================
    // DATOS DEL ATAQUE
    // ==============================

    // Daño real causado al defensor (ya descontada la armadura)
    private int dañoReal = 0;

    // Salud restante del defensor tras recibir el daño
    private int saludDefensor = 0;

    // Nombre del defensor que recibió el daño
    private String nombreDefensor = "";

    // ==============================
    // FLAGS DE EVENTOS DEL TURNO
    // ==============================

    // Indica si el ataque fue un golpe crítico
    private boolean golpeCritico = false;

    // Indica si el ataque falló (dado 0 o 1)
    private boolean ataqueFallado = false;

    // Indica si el turno fue bloqueado por sigilo del defensor
    private boolean turnoBloquedoPorSigilo = false;

    // Indica si el turno fue consumido por la carga de Furia Divina
    private boolean turnoConsumidoPorCarga = false;

    // Indica si el combate ha terminado tras este turno
    private boolean combateTerminado = false;

    // Nombre del ganador si el combate ha terminado (null si sigue)
    private String nombreGanador = null;

    // ==============================
    // MÉTODOS PARA AÑADIR MENSAJES
    // ==============================

    /**
     * Añade un mensaje al log del turno.
     * Los mensajes se acumulan en orden cronológico.
     *
     * @param mensaje Texto del mensaje a añadir.
     */
    public void agregarMensaje(String mensaje) {
        mensajes.add(mensaje);
    }

    // ==============================
    // GETTERS
    // ==============================

    public List<String> getMensajes() {
        return mensajes;
    }

    public int getDañoReal() {
        return dañoReal;
    }

    public int getSaludDefensor() {
        return saludDefensor;
    }

    public String getNombreDefensor() {
        return nombreDefensor;
    }

    public boolean isGolpeCritico() {
        return golpeCritico;
    }

    public boolean isAtaqueFallado() {
        return ataqueFallado;
    }

    public boolean isTurnoBloquedoPorSigilo() {
        return turnoBloquedoPorSigilo;
    }

    public boolean isTurnoConsumidoPorCarga() {
        return turnoConsumidoPorCarga;
    }

    public boolean isCombateTerminado() {
        return combateTerminado;
    }

    public String getNombreGanador() {
        return nombreGanador;
    }

    // ==============================
    // SETTERS
    // ==============================

    public void setDañoReal(int dañoReal) {
        this.dañoReal = dañoReal;
    }

    public void setSaludDefensor(int saludDefensor) {
        this.saludDefensor = saludDefensor;
    }

    public void setNombreDefensor(String nombreDefensor) {
        this.nombreDefensor = nombreDefensor;
    }

    public void setGolpeCritico(boolean golpeCritico) {
        this.golpeCritico = golpeCritico;
    }

    public void setAtaqueFallado(boolean ataqueFallado) {
        this.ataqueFallado = ataqueFallado;
    }

    public void setTurnoBloquedoPorSigilo(boolean turnoBloquedoPorSigilo) {
        this.turnoBloquedoPorSigilo = turnoBloquedoPorSigilo;
    }

    public void setTurnoConsumidoPorCarga(boolean turnoConsumidoPorCarga) {
        this.turnoConsumidoPorCarga = turnoConsumidoPorCarga;
    }

    public void setCombateTerminado(boolean combateTerminado) {
        this.combateTerminado = combateTerminado;
    }

    public void setNombreGanador(String nombreGanador) {
        this.nombreGanador = nombreGanador;
    }
}