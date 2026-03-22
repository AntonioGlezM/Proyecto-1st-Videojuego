package model.interfaces;

/**
 * @author Antonio González Martel
 */

/**
 * Interfaz que representa la capacidad de usar maná.
 *
 * Cualquier clase que implemente esta interfaz
 * debe tener un atributo de maná y permitir
 * obtenerlo y modificarlo.
 *
 * Se utiliza para diferenciar personajes mágicos
 * de los que no usan maná (por ejemplo, guerreros).
 */
public interface Magico {

    public int getMana();

    public void setMana(int mana);
}