package model.interfaces;

import model.acciones.Defensa;

/**
 * Interfaz que representa la capacidad de defenderse.
 *
 * Los personajes que la implementen podrán usar la acción
 * de defensa en combate, sujeta al cooldown del controlador.
 *
 * Actualmente la implementan: Guerrero, Paladín.
 */
public interface Defensor {

    public Defensa defender();
}