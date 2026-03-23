package model.personajes;

import model.acciones.Ataque;
import model.habilidades.Habilidad;
import model.interfaces.HabilidadEspecial;

/**
 * Clase Picaro.
 *
 * Personaje rápido con ataques ágiles.
 * Especializado en golpes frecuentes de daño moderado.
 * No usa maná.
 * Implementa HabilidadEspecial con la habilidad "Sigilo",
 * igual que el Asesino: ambos comparten la misma mecánica
 * de volverse invisibles durante un turno.
 * El estado de sigilo es gestionado por el controlador.
 *
 * @author Antonio González Martel
 */
public class Picaro extends PersonajeFisico implements HabilidadEspecial {

    /**
     * Constructor del Pícaro.
     *
     * @param nombre    Nombre del personaje
     * @param salud     Vida inicial
     * @param poderBase Daño base
     * @param raza      Raza del personaje
     * @param agilidad  Atributo físico principal
     */
    public Picaro(String nombre,
            int salud,
            int poderBase,
            String raza,
            int agilidad) {

        super(nombre, salud, poderBase, raza, agilidad);

        // ==========================
        // HABILIDADES DEL PÍCARO
        // ==========================

        // Golpe básico: sin coste, siempre disponible
        agregarHabilidad(new Habilidad(
                "Golpe Veloz",
                10,
                0));

        agregarHabilidad(new Habilidad(
                "Puñalada Rápida",
                20,
                0));

        agregarHabilidad(new Habilidad(
                "Ataque Sombra",
                35,
                0));

        agregarHabilidad(new Habilidad(
                "Golpe Fantasma",
                50,
                0));
    }

    /**
     * Ataque básico del Pícaro.
     * Usa el cálculo físico heredado.
     */
    @Override
    public Ataque atacar(Personaje objetivo) {
        return new Ataque(calcularDanioFisico());
    }

    // ==============================
    // HABILIDAD ESPECIAL: SIGILO
    // ==============================

    /**
     * Devuelve el nombre de la habilidad especial del Pícaro.
     * La Vista lo usará para mostrar la opción 2 del menú de forma dinámica.
     */
    @Override
    public String getNombreHabilidadEspecial() {
        return "Sigilo";
    }

    /**
     * Ejecuta la habilidad especial del Pícaro.
     *
     * Igual que el Asesino, activa el estado de sigilo:
     * el rival no puede atacarle durante su siguiente turno.
     *
     * IMPORTANTE: el estado de sigilo lo gestiona el controlador
     * mediante el flag enSigilo. Este método existe para cumplir
     * el contrato de la interfaz HabilidadEspecial.
     *
     * @param personaje El Pícaro que entra en sigilo (no se usa directamente aquí).
     */
    @Override
    public void usarHabilidadEspecial(Personaje personaje) {
        // El estado de sigilo lo gestiona el controlador mediante el flag enSigilo.
        // Este método existe para cumplir el contrato de la interfaz HabilidadEspecial
        // y para que la Vista pueda mostrar el nombre correcto en el menú.
    }
}