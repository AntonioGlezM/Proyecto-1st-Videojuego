package model.personajes;

import model.armas.Armas;

/**
 * Clase abstracta intermedia para personajes físicos.
 * 
 * Hereda de Personaje y añade características propias
 * de luchadores cuerpo a cuerpo o usuarios de armas físicas.
 */

/**
 * @author Antonio González Martel
 */
public abstract class PersonajeFisico extends Personaje {

    private int atributoFisico;

    public PersonajeFisico(String nombre, int salud, int poderBase,
            String raza,
            int atributoFisico) {

        super(nombre, salud, poderBase, raza, 0);
        this.atributoFisico = atributoFisico;
    }

    // Getter
    public int getAtributoFisico() {
        return atributoFisico;
    }

    // Setter
    public void setAtributoFisico(int atributoFisico) {
        this.atributoFisico = atributoFisico;
    }

    /**
     * Método común para ataques físicos básicos.
     * Puede ser usado por las clases hijas.
     */
    public int calcularDanioFisico() {

        int danio = getPoderBase();

        Armas arma = getArmaEquipada();

        if (arma != null) {
            danio += arma.calcularDanio();
        }

        return danio;
    }
}