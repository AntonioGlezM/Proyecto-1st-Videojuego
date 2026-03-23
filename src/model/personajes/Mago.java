package model.personajes;

import model.acciones.Ataque;
import model.habilidades.Habilidad;
import model.interfaces.HabilidadEspecial;

/**
 * Clase Mago.
 *
 * Personaje mágico especializado en hechizos de alto daño.
 * Consume maná en cada habilidad ofensiva.
 *
 * Cuando no tiene maná suficiente, recurre al golpe básico
 * que no tiene coste.
 *
 * Habilidad especial: Distorsión Temporal.
 * Permite al Mago atacar dos veces en su siguiente turno.
 * El estado es gestionado por el controlador.
 */
public class Mago extends PersonajeMagico implements HabilidadEspecial {

    /**
     * Constructor del Mago.
     *
     * @param nombre         Nombre del personaje
     * @param salud          Vida inicial
     * @param poderBase      Daño base
     * @param raza           Raza del personaje
     * @param mana           Maná inicial
     * @param atributoMagico Potencia mágica
     */
    public Mago(String nombre,
            int salud,
            int poderBase,
            String raza,
            int mana,
            int atributoMagico) {

        super(nombre, salud, poderBase, raza, mana, atributoMagico);

        // ==========================
        // HABILIDADES DEL MAGO
        // ==========================

        // Golpe básico: sin coste, siempre disponible
        agregarHabilidad(new Habilidad(
                "Golpe de varita",
                10,
                0));

        agregarHabilidad(new Habilidad(
                "Piroexplosión",
                50,
                20));

        agregarHabilidad(new Habilidad(
                "Rayo de Escarcha",
                40,
                15));

        agregarHabilidad(new Habilidad(
                "Combustión",
                70,
                35));
    }

    /**
     * Ataque básico del Mago.
     * Usa el cálculo mágico heredado.
     */
    @Override
    public Ataque atacar(Personaje objetivo) {
        return new Ataque(calcularDanioMagico());
    }

    // ==============================
    // HABILIDAD ESPECIAL: DISTORSIÓN TEMPORAL
    // ==============================

    /**
     * Devuelve el nombre de la habilidad especial del Mago.
     * La Vista lo usará para mostrar la opción 2 del menú de forma dinámica.
     */
    @Override
    public String getNombreHabilidadEspecial() {
        return "Distorsión Temporal";
    }

    /**
     * Ejecuta la habilidad especial del Mago.
     *
     * Activa el estado de doble ataque: en el siguiente turno
     * el Mago podrá elegir y ejecutar dos habilidades en lugar de una.
     *
     * IMPORTANTE: este método no gestiona el estado por sí solo.
     * Es el controlador quien detecta que la habilidad especial
     * es "Distorsión Temporal" y activa el flag correspondiente (dobleAtaque).
     *
     * @param personaje El Mago que activa la distorsión (no se usa directamente
     *                  aquí).
     */
    @Override
    public void usarHabilidadEspecial(Personaje personaje) {
        // El estado de doble ataque lo gestiona el controlador mediante el flag
        // dobleAtaque.
        // Este método existe para cumplir el contrato de la interfaz HabilidadEspecial
        // y para que la Vista pueda mostrar el nombre correcto en el menú.
    }
}