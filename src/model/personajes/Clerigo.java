package model.personajes;

import model.acciones.Ataque;
import model.personajes.Personaje;
import model.habilidades.Habilidad;

/**
 * Clase Clerigo.
 *
 * Personaje mágico de naturaleza sagrada.
 * Combina habilidades ofensivas de luz divina
 * con una habilidad de curación propia.
 *
 * El golpe básico no consume maná, garantizando
 * que siempre puede actuar aunque se quede sin recursos.
 */
public class Clerigo extends PersonajeMagico {

    /**
     * Constructor del Clérigo.
     *
     * @param nombre         Nombre del personaje
     * @param salud          Vida inicial
     * @param poderBase      Daño base
     * @param raza           Raza del personaje
     * @param mana           Maná inicial
     * @param atributoMagico Potencia mágica
     */
    public Clerigo(String nombre,
            int salud,
            int poderBase,
            String raza,
            int mana,
            int atributoMagico) {

        super(nombre, salud, poderBase, raza, mana, atributoMagico);

        // ==========================
        // HABILIDADES DEL CLÉRIGO
        // ==========================

        // Golpe básico: sin coste, siempre disponible
        agregarHabilidad(new Habilidad(
                "Punición",
                12,
                0));

        agregarHabilidad(new Habilidad(
                "Nova sagrada",
                40,
                15));

        agregarHabilidad(new Habilidad(
                "Luz Divina",
                55,
                25));

        agregarHabilidad(new Habilidad(
                "Ira Celestial",
                70,
                40));

    }

    /**
     * Ataque básico del Clérigo.
     * Usa el cálculo mágico heredado.
     */
    @Override
    public Ataque atacar(Personaje objetivo) {
        return new Ataque(calcularDanioMagico());
    }
}