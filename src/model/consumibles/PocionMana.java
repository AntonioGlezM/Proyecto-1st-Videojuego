package model.consumibles;

import model.personajes.*;
import model.interfaces.Magico;

/**
 * @author Antonio González Martel
 */

public class PocionMana extends Consumibles {

    private int cantidadMana;

    public PocionMana(String nombre, int cantidadMana) {
        super(nombre);
        this.cantidadMana = cantidadMana;
    }

    @Override
    public void usar(Personaje personaje) {

        if (personaje instanceof Magico) {

            Magico magico = (Magico) personaje;

            magico.setMana(magico.getMana() + cantidadMana);

            System.out.println(personaje.getNombre()
                    + " ha recuperado " + cantidadMana + " de maná.");

        } else {

            System.out.println(personaje.getNombre()
                    + " no puede usar maná.");
        }
    }
}