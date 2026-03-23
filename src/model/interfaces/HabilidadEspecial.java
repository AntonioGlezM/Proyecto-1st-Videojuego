package model.interfaces;

import model.personajes.Personaje;

/**
 * Interfaz que representa la habilidad especial de cada personaje.
 *
 * Cada clase que la implemente define su propio comportamiento
 * en el método usarHabilidadEspecial(), que será llamado desde
 * el controlador cuando el jugador elija la opción 2 del menú.
 *
 * También expone getNombreHabilidadEspecial() para que la Vista
 * pueda mostrar el nombre correcto de forma dinámica según el personaje.
 *
 * Personajes y sus habilidades especiales:
 * - Guerrero, Paladín  → Defensa (aumenta armadura)
 * - Pícaro    → Sigilo (el rival no puede atacar el siguiente turno)
 * - Bárbaro            → Furia (sube daño pero baja armadura)
 * - Mago, Clérigo      → Escudo Mágico (absorbe daño el siguiente turno)
 */
public interface HabilidadEspecial {

    /**
     * Devuelve el nombre de la habilidad especial del personaje.
     * Se usa en la Vista para mostrar la opción 2 del menú de forma dinámica.
     *
     * @return Nombre de la habilidad especial.
     */
    String getNombreHabilidadEspecial();

    /**
     * Ejecuta la habilidad especial del personaje.
     * Cada clase implementa su propia lógica aquí.
     *
     * @param personaje El personaje que usa la habilidad especial.
     */
    void usarHabilidadEspecial(Personaje personaje);
}