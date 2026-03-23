package model.personajes;

import model.acciones.Ataque;
import model.habilidades.Habilidad;
import model.interfaces.HabilidadEspecial;

/**
 * Clase Cazador.
 *
 * Personaje físico especializado en precisión y conocimiento del rival.
 * Sus ataques son certeros y aprovecha las debilidades del enemigo.
 *
 * Habilidad especial: Marca del Cazador.
 * Marca al rival durante 2 turnos, haciendo que todos los ataques
 * del Cazador ignoren completamente su armadura.
 * El estado es gestionado por el controlador.
 *
 * @author Antonio González Martel
 */
public class Cazador extends PersonajeFisico implements HabilidadEspecial {

    /**
     * Constructor del Cazador.
     *
     * @param nombre    Nombre del personaje
     * @param salud     Vida inicial
     * @param poderBase Daño base
     * @param raza      Raza del personaje
     * @param agilidad  Atributo físico principal
     */
    public Cazador(String nombre,
            int salud,
            int poderBase,
            String raza,
            int agilidad) {

        super(nombre, salud, poderBase, raza, agilidad);

        // ==========================
        // HABILIDADES DEL CAZADOR
        // ==========================

        // Golpe básico: sin coste, siempre disponible
        agregarHabilidad(new Habilidad(
                "Flecha Rápida",
                10,
                0));

        agregarHabilidad(new Habilidad(
                "Disparo Certero",
                28,
                0));

        agregarHabilidad(new Habilidad(
                "Trampa Explosiva",
                42,
                0));

        agregarHabilidad(new Habilidad(
                "Lluvia de Flechas",
                55,
                0));
    }

    /**
     * Ataque básico del Cazador.
     * Usa el cálculo físico heredado.
     */
    @Override
    public Ataque atacar(Personaje objetivo) {
        return new Ataque(calcularDanioFisico());
    }

    // ==============================
    // HABILIDAD ESPECIAL: MARCA DEL CAZADOR
    // ==============================

    /**
     * Devuelve el nombre de la habilidad especial del Cazador.
     * La Vista lo usará para mostrar la opción 2 del menú de forma dinámica.
     */
    @Override
    public String getNombreHabilidadEspecial() {
        return "Marca del Cazador";
    }

    /**
     * Ejecuta la habilidad especial del Cazador.
     *
     * Marca al rival durante 2 turnos: todos los ataques del Cazador
     * durante ese tiempo ignorarán completamente la armadura del defensor.
     *
     * IMPORTANTE: este método no gestiona el estado por sí solo.
     * Es el controlador quien activa el flag marcaDelCazador y lleva
     * la cuenta de los turnos restantes.
     *
     * @param personaje El Cazador que aplica la marca (no se usa directamente aquí).
     */
    @Override
    public void usarHabilidadEspecial(Personaje personaje) {
        // El estado de la marca lo gestiona el controlador mediante
        // los flags marcaActivaJ1/J2 y turnosMarcaJ1/J2.
        // Este método existe para cumplir el contrato de la interfaz HabilidadEspecial
        // y para que la Vista pueda mostrar el nombre correcto en el menú.
    }
}