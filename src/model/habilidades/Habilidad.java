package model.habilidades;

/**
 * Clase que representa una habilidad de combate.
 *
 * Cada personaje podrá tener una lista de habilidades
 * que serán mostradas dinámicamente en el controlador.
 *
 * La habilidad contiene:
 * - Nombre
 * - Daño
 * - Coste (para futuros sistemas de maná o energía)
 */
public class Habilidad {

    private String nombre;
    private int daño;
    private int coste;

    /**
     * Constructor de la habilidad.
     *
     * @param nombre Nombre de la habilidad
     * @param daño   Daño que inflige
     * @param coste  Coste de uso
     */
    public Habilidad(String nombre, int daño, int coste) {
        this.nombre = nombre;
        this.daño = daño;
        this.coste = coste;
    }

    // ============================
    // GETTERS
    // ============================

    public String getNombre() {
        return nombre;
    }

    public int getDaño() {
        return daño;
    }

    public int getCoste() {
        return coste;
    }

    // ============================
    // SETTERS
    // ============================

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setDaño(int daño) {
        this.daño = daño;
    }

    public void setCoste(int coste) {
        this.coste = coste;
    }

    @Override
    public String toString() {
        return nombre + " (Daño: " + daño + ", Coste: " + coste + ")";
    }
}