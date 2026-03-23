package model.personajes;

import model.acciones.Ataque;
import model.acciones.Defensa;
import model.habilidades.Habilidad;
import model.interfaces.Defensor;
import model.interfaces.HabilidadEspecial;

/**
 * Clase Guerrero.
 *
 * Personaje físico equilibrado entre daño y resistencia.
 * Implementa Defensor, pudiendo usar la acción de defensa en combate.
 * Implementa HabilidadEspecial con la habilidad "Postura Defensiva",
 * que aumenta su armadura durante el siguiente turno.
 *
 * @author Antonio González Martel
 */
public class Guerrero extends PersonajeFisico implements Defensor, HabilidadEspecial {

    // Bonus de armadura que se aplica al usar la habilidad especial
    private static final int BONUS_ARMADURA = 15;

    /**
     * Constructor del Guerrero.
     *
     * @param nombre    Nombre del personaje
     * @param salud     Vida inicial
     * @param poderBase Daño base
     * @param raza      Raza del personaje
     * @param fuerza    Atributo físico principal
     * @param defensa   Valor de armadura inicial
     */
    public Guerrero(String nombre,
            int salud,
            int poderBase,
            String raza,
            int fuerza,
            int defensa) {

        super(nombre, salud, poderBase, raza, fuerza);

        // Aplicar armadura inicial según el parámetro de defensa
        setValorArmadura(defensa);

        // ==========================
        // HABILIDADES DEL GUERRERO
        // ==========================

        // Golpe básico: sin coste, siempre disponible
        agregarHabilidad(new Habilidad(
                "Golpe Básico",
                10,
                0));

        agregarHabilidad(new Habilidad(
                "Corte Poderoso",
                20,
                0));

        agregarHabilidad(new Habilidad(
                "Golpe de Escudo",
                30,
                0));

        agregarHabilidad(new Habilidad(
                "Espadazo Brutal",
                45,
                0));
    }

    /**
     * Ataque básico del Guerrero.
     * Usa el cálculo físico heredado.
     */
    @Override
    public Ataque atacar(Personaje objetivo) {
        return new Ataque(calcularDanioFisico());
    }

    /**
     * Implementación de la interfaz Defensor.
     * El Guerrero puede usar la acción de defensa en combate
     * (sujeta al cooldown gestionado por el controlador).
     */
    @Override
    public Defensa defender() {
        return new Defensa();
    }

    // ==============================
    // HABILIDAD ESPECIAL: POSTURA DEFENSIVA
    // ==============================

    /**
     * Devuelve el nombre de la habilidad especial del Guerrero.
     * La Vista lo usará para mostrar la opción 2 del menú de forma dinámica.
     */
    @Override
    public String getNombreHabilidadEspecial() {
        return "Postura Defensiva";
    }

    /**
     * Ejecuta la habilidad especial del Guerrero.
     * Aumenta su armadura en BONUS_ARMADURA puntos.
     * El controlador es responsable de revertir este bonus al turno siguiente.
     *
     * @param personaje El Guerrero que activa la postura.
     */
    @Override
    public void usarHabilidadEspecial(Personaje personaje) {
        personaje.setValorArmadura(personaje.getValorArmadura() + BONUS_ARMADURA);
    }
}