package model.personajes;

import model.acciones.Ataque;
import model.acciones.Defensa;
import model.habilidades.Habilidad;
import model.interfaces.Defensor;
import model.interfaces.HabilidadEspecial;

/**
 * Clase Paladin.
 *
 * Personaje híbrido: hereda de PersonajeMagico para el sistema de maná,
 * e implementa Defensor para poder usar la acción de defensa en combate.
 *
 * Combina golpes físicos potenciados con energía sagrada
 * y una resistencia elevada gracias a su armadura.
 *
 * El golpe básico no consume maná, garantizando
 * que siempre puede actuar aunque se quede sin recursos.
 *
 * Habilidad especial: Furia Divina.
 * El Paladín carga un ataque devastador durante 2 turnos
 * (el turno que lo activa y el siguiente) y al descargar
 * hace el triple del daño normal.
 * El estado es gestionado por el controlador.
 */
public class Paladin extends PersonajeMagico implements Defensor, HabilidadEspecial {

    /**
     * Constructor del Paladín.
     *
     * @param nombre         Nombre del personaje
     * @param salud          Vida inicial
     * @param poderBase      Daño base
     * @param raza           Raza del personaje
     * @param valorArmadura  Reducción de daño recibido
     * @param mana           Maná inicial
     * @param atributoMagico Potencia mágica
     */
    public Paladin(String nombre,
            int salud,
            int poderBase,
            String raza,
            int valorArmadura,
            int mana,
            int atributoMagico) {

        super(nombre, salud, poderBase, raza, mana, atributoMagico);

        // El Paladín lleva armadura pesada
        setValorArmadura(valorArmadura);

        // ==========================
        // HABILIDADES DEL PALADÍN
        // ==========================

        // Golpe básico: sin coste, siempre disponible
        agregarHabilidad(new Habilidad(
                "Golpe del cruzado",
                15,
                0));

        agregarHabilidad(new Habilidad(
                "Martillo Sagrado",
                45,
                20));

        agregarHabilidad(new Habilidad(
                "Juicio Divino",
                60,
                30));

        agregarHabilidad(new Habilidad(
                "Consagración",
                35,
                15));
    }

    /**
     * Ataque básico del Paladín.
     * Usa el cálculo mágico heredado (daño imbuido de energía sagrada).
     */
    @Override
    public Ataque atacar(Personaje objetivo) {
        return new Ataque(calcularDanioMagico());
    }

    /**
     * Implementación de la interfaz Defensor.
     * El Paladín puede usar la acción de defensa en combate
     * (sujeta al cooldown gestionado por el controlador).
     */
    @Override
    public Defensa defender() {
        return new Defensa();
    }

    // ==============================
    // HABILIDAD ESPECIAL: FURIA DIVINA
    // ==============================

    /**
     * Devuelve el nombre de la habilidad especial del Paladín.
     * La Vista lo usará para mostrar la opción 2 del menú de forma dinámica.
     */
    @Override
    public String getNombreHabilidadEspecial() {
        return "Furia Divina";
    }

    /**
     * Ejecuta la habilidad especial del Paladín.
     *
     * Inicia la carga de Furia Divina: el Paladín dedica este turno
     * y el siguiente a cargar energía divina. Al completar la carga,
     * el siguiente ataque que realice hará el triple del daño normal.
     *
     * Durante la carga el Paladín no puede realizar otras acciones
     * (el controlador bloquea el menú y pasa el turno automáticamente).
     *
     * IMPORTANTE: este método no gestiona el estado por sí solo.
     * Es el controlador quien lleva la cuenta de los turnos de carga
     * mediante los flags furiaDivinaActiva y turnosCargaFuria.
     *
     * @param personaje El Paladín que inicia la carga (no se usa directamente
     *                  aquí).
     */
    @Override
    public void usarHabilidadEspecial(Personaje personaje) {
        // El estado de carga lo gestiona el controlador mediante
        // los flags furiaDivinaActiva y turnosCargaFuria.
        // Este método existe para cumplir el contrato de la interfaz HabilidadEspecial
        // y para que la Vista pueda mostrar el nombre correcto en el menú.
    }
}