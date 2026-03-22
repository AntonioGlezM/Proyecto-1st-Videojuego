package model.personajes;

import model.armas.Armas;
import model.interfaces.Magico;

/**
 * Clase abstracta intermedia para personajes mágicos.
 *
 * Hereda de Personaje e implementa la interfaz Magico,
 * añadiendo el atributo de maná y la lógica de daño mágico
 * propia de personajes que usan hechizos o poderes arcanos.
 *
 * @author Antonio González Martel
 */
public abstract class PersonajeMagico extends Personaje implements Magico {

    private int mana;
    private int manaMaximo;
    private int atributoMagico;

    public PersonajeMagico(String nombre, int salud, int poderBase,
            String raza, int mana, int atributoMagico) {

        super(nombre, salud, poderBase, raza, 0);
        this.mana = mana;
        this.manaMaximo = mana;
        this.atributoMagico = atributoMagico;
    }

    // ============================
    // GETTERS
    // ============================

    @Override
    public int getMana() {
        return mana;
    }

    public int getManaMaximo() {
        return manaMaximo;
    }

    public int getAtributoMagico() {
        return atributoMagico;
    }

    // ============================
    // SETTERS
    // ============================

    /**
     * Establece el maná actual del personaje.
     * No puede ser negativo ni superar el máximo.
     */
    @Override
    public void setMana(int mana) {

        if (mana < 0) {
            this.mana = 0;
            return;
        }

        if (mana > this.manaMaximo) {
            this.mana = this.manaMaximo;
            return;
        }

        this.mana = mana;
    }

    public void setManaMaximo(int manaMaximo) {

        if (manaMaximo < 0) {
            this.manaMaximo = 0;
        } else {
            this.manaMaximo = manaMaximo;
        }

        if (this.mana > this.manaMaximo) {
            this.mana = this.manaMaximo;
        }
    }

    public void setAtributoMagico(int atributoMagico) {
        this.atributoMagico = atributoMagico;
    }

    // ============================
    // COMBATE
    // ============================

    /**
     * Calcula el daño mágico base del personaje.
     * Combina el poderBase con el atributoMagico.
     * Las clases hijas pueden sobreescribir este método
     * para añadir lógica específica (hechizos, costes de maná, etc.).
     */
    public int calcularDanioMagico() {
        return getPoderBase() + atributoMagico;
    }

    /**
     * Comprueba si el personaje tiene suficiente maná para lanzar un hechizo.
     *
     * @param coste Coste en maná del hechizo.
     * @return true si tiene maná suficiente, false en caso contrario.
     */
    public boolean tieneMana(int coste) {
        return this.mana >= coste;
    }

    /**
     * Consume maná al lanzar un hechizo.
     * Si no hay suficiente maná, imprime un aviso y no realiza la acción.
     *
     * @param coste Cantidad de maná a consumir.
     * @return true si se pudo consumir, false si no había suficiente.
     */
    public boolean consumirMana(int coste) {

        if (!tieneMana(coste)) {
            System.out.println(getNombre() + " no tiene suficiente maná.");
            return false;
        }

        this.mana -= coste;
        return true;
    }
}