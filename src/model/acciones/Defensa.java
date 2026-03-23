package model.acciones;

import model.personajes.Personaje;

/**
 * Clase que representa la acción de defensa.
 *
 * Es devuelta por el método defender() de los personajes
 * que implementan la interfaz Defensor.
 *
 * Al realizarse, aumenta temporalmente el valor de armadura
 * del personaje que la ejecuta.
 */
public class Defensa {

    // Cantidad de armadura que se añade al defenderse
    private static final int BONUS_ARMADURA = 10;

    /**
     * Aplica el efecto de defensa sobre el personaje.
     * Aumenta su valorArmadura durante ese turno.
     *
     * @param personaje Personaje que ejecuta la defensa.
     */
    public void realizarAccion(Personaje personaje) {
        int nuevaArmadura = personaje.getValorArmadura() + BONUS_ARMADURA;
        personaje.setValorArmadura(nuevaArmadura);
    }
}