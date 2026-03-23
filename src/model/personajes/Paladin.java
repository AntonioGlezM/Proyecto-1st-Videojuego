package model.personajes;

import model.personajes.*;
import model.acciones.Ataque;
import model.acciones.Defensa;
import model.habilidades.Habilidad;
import model.interfaces.Defensor;

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
 */
public class Paladin extends PersonajeMagico implements Defensor {

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
}