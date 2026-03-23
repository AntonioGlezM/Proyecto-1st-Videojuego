package model.personajes;

import model.acciones.Ataque;
import model.habilidades.Habilidad;
import model.interfaces.HabilidadEspecial;

/**
 * Clase Barbaro.
 *
 * Personaje físico con altísimo daño y gran cantidad de salud.
 * No usa maná ni defensa: su filosofía es atacar sin parar.
 * Implementa HabilidadEspecial con la habilidad "Furia Berserker",
 * que aumenta su poderBase pero reduce su armadura,
 * reflejando su estilo de combate agresivo y descuidado.
 *
 * @author Antonio González Martel
 */
public class Barbaro extends PersonajeFisico implements HabilidadEspecial {

    // Bonus de poder que se aplica al entrar en furia
    private static final int BONUS_PODER = 20;

    // Penalización de armadura al entrar en furia (queda expuesto)
    private static final int PENALIZACION_ARMADURA = 5;

    /**
     * Constructor del Bárbaro.
     *
     * @param nombre    Nombre del personaje
     * @param salud     Vida inicial
     * @param poderBase Daño base
     * @param raza      Raza del personaje
     * @param fuerza    Atributo físico principal
     */
    public Barbaro(String nombre,
            int salud,
            int poderBase,
            String raza,
            int fuerza) {

        super(nombre, salud, poderBase, raza, fuerza);

        // ==========================
        // HABILIDADES DEL BÁRBARO
        // ==========================

        // Golpe básico: sin coste, siempre disponible
        agregarHabilidad(new Habilidad(
                "Golpe Bruto",
                12,
                0));

        agregarHabilidad(new Habilidad(
                "Golpe Salvaje",
                25,
                0));

        agregarHabilidad(new Habilidad(
                "Hachazo Brutal",
                40,
                0));

        agregarHabilidad(new Habilidad(
                "Furia del Bárbaro",
                60,
                0));
    }

    /**
     * Ataque básico del Bárbaro.
     * Usa el cálculo físico heredado.
     */
    @Override
    public Ataque atacar(Personaje objetivo) {
        return new Ataque(calcularDanioFisico());
    }

    // ==============================
    // HABILIDAD ESPECIAL: FURIA BERSERKER
    // ==============================

    /**
     * Devuelve el nombre de la habilidad especial del Bárbaro.
     * La Vista lo usará para mostrar la opción 2 del menú de forma dinámica.
     */
    @Override
    public String getNombreHabilidadEspecial() {
        return "Furia Berserker";
    }

    /**
     * Ejecuta la habilidad especial del Bárbaro.
     *
     * Entra en modo berserker:
     * - Aumenta el poderBase en BONUS_PODER (más daño en ataques).
     * - Reduce la armadura en PENALIZACION_ARMADURA (queda más expuesto).
     *
     * El controlador es responsable de revertir estos cambios al turno siguiente.
     *
     * @param personaje El Bárbaro que entra en furia.
     */
    @Override
    public void usarHabilidadEspecial(Personaje personaje) {
        personaje.setPoderBase(personaje.getPoderBase() + BONUS_PODER);
        personaje.setValorArmadura(Math.max(0, personaje.getValorArmadura() - PENALIZACION_ARMADURA));
    }
}