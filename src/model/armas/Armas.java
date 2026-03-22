package model.armas;

/**
 * @author Antonio González Martel
 */

// Clase guía para la creación de armas
public abstract class Armas {

    protected String nombre;
    protected int danioBase;
    protected CategoriaArma categoria;

    public Armas(String nombre, int danioBase, CategoriaArma categoria) {
        this.nombre = nombre;
        this.danioBase = danioBase;
        this.categoria = categoria;
    }

    public String getNombre() {
        return nombre;
    }

    public int getDanioBase() {
        return danioBase;
    }

    public CategoriaArma getCategoria() {
        return categoria;
    }

    // Método común para calcular según categoría
    protected int calcularBonusCategoria() {

        switch (categoria) {
            case RARA:
                return 3;
            case EPICA:
                return 5;
            case LEGENDARIA:
                return 10;
            case COMUN:
            default:
                return 0;
        }
    }

    public abstract int calcularDanio();

    @Override
    public String toString() {
        return "Arma: " + nombre +
                " | Daño base: " + danioBase +
                " | Categoría: " + categoria +
                " | Daño total: " + calcularDanio();
    }
}