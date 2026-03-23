package model.consumibles;

import model.personajes.*;

/**
 * @author Antonio González Martel
 */

public class PocionFuerza extends Consumibles {

    private int cantidadFuerza;

    public PocionFuerza(String nombre, int cantidadFuerza) {
        super(nombre);
        this.cantidadFuerza = cantidadFuerza;
    }

    @Override
    public void usar(Personaje personaje) {
        personaje.setPoderBase(personaje.getPoderBase() + cantidadFuerza);
        System.out.println(personaje.getNombre() + 
            " ha conseguido " + cantidadFuerza + " puntos de Fuerza extra.");
    }
}