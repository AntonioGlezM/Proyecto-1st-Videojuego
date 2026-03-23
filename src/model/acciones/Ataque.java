package model.acciones;

/**
 * Clase que representa el resultado de un ataque.
 *
 * Es devuelta por el método atacar() de cada personaje
 * y contiene el daño calculado que se aplicará al defensor.
 */
public class Ataque {

    private int danio;

    /**
     * Constructor del Ataque.
     *
     * @param danio Daño calculado por el personaje atacante.
     */
    public Ataque(int danio) {
        this.danio = danio;
    }

    // ============================
    // GETTER
    // ============================

    public int getDanio() {
        return danio;
    }
}