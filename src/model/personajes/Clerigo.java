package model.personajes;

import model.acciones.Ataque;
import model.habilidades.Habilidad;
import model.interfaces.HabilidadEspecial;

/**
 * Clase Clerigo.
 *
 * Personaje mágico de naturaleza sagrada.
 * Combina habilidades ofensivas de luz divina
 * con una habilidad de curación propia.
 *
 * El golpe básico no consume maná, garantizando
 * que siempre puede actuar aunque se quede sin recursos.
 *
 * Habilidad especial: Himno Divino.
 * El siguiente ataque del Clérigo deja una quemadura sagrada
 * que aplica el 20% del daño causado durante 2 turnos seguidos.
 * El estado es gestionado por el controlador.
 */
public class Clerigo extends PersonajeMagico implements HabilidadEspecial {

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

    // ==============================
    // HABILIDAD ESPECIAL: HIMNO DIVINO
    // ==============================

    /**
     * Devuelve el nombre de la habilidad especial del Clérigo.
     * La Vista lo usará para mostrar la opción 2 del menú de forma dinámica.
     */
    @Override
    public String getNombreHabilidadEspecial() {
        return "Himno Divino";
    }

    /**
     * Ejecuta la habilidad especial del Clérigo.
     *
     * Activa el estado de quemadura sagrada: el siguiente ataque
     * del Clérigo dejará una quemadura que aplica el 20% del daño
     * causado en ese ataque durante los 2 turnos siguientes del defensor.
     *
     * IMPORTANTE: este método no gestiona el estado por sí solo.
     * Es el controlador quien detecta que la habilidad especial
     * es "Himno Divino" y activa el flag correspondiente (himnoDivino).
     *
     * @param personaje El Clérigo que activa el himno (no se usa directamente aquí).
     */
    @Override
    public void usarHabilidadEspecial(Personaje personaje) {
        // El estado de quemadura sagrada lo gestiona el controlador mediante
        // los flags himnoDivinoActivo, danioQuemadura y turnosQuemadura.
        // Este método existe para cumplir el contrato de la interfaz HabilidadEspecial
        // y para que la Vista pueda mostrar el nombre correcto en el menú.
    }
}