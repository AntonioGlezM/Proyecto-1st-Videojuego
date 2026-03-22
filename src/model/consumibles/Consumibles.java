
package model.consumibles;

import model.personajes.Personaje;
import model.interfaces.Usable;

/**
 * @author Antonio González Martel
 */

// Clase para todos los consumibles
public abstract class Consumibles implements Usable{

    protected String nombre;

    public Consumibles(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }

    // Cada consumible hace algo distinto
    public abstract void usar(Personaje personaje);

    @Override
    public String toString() {
        return "Consumible: " + nombre;
    }
}
