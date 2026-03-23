package model.personajes;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import model.acciones.Ataque;
import model.armas.Armas;
import model.consumibles.Consumibles;
import model.consumibles.PocionCuracion;
import model.consumibles.PocionFuerza;
import model.consumibles.PocionMana;
import model.habilidades.Habilidad;

/**
 * Clase abstracta base de todos los personajes.
 *
 * Incluye:
 * - Sistema de habilidades
 * - Inventario de consumibles
 * - Sistema de combate
 *
 * Las clases hijas (Guerrero, Mago, etc.)
 * serán las encargadas de definir sus habilidades.
 * 
 * @author Antonio Gonzalez Martel
 */
public abstract class Personaje {

    private final String id;

    private String nombre;
    private int salud;
    protected int saludMaxima;
    private int poderBase;
    private String raza;

    private int valorArmadura;

    private Armas armaEquipada;

    // Inventario de consumibles
    private List<Consumibles> inventario;

    // Lista de habilidades del personaje
    private List<Habilidad> habilidades;

    public Personaje(String nombre, int salud, int poderBase,
            String raza, int claseArmadura) {

        this.id = UUID.randomUUID().toString();
        this.nombre = nombre;
        this.salud = salud;
        this.saludMaxima = salud;
        this.poderBase = poderBase;
        this.raza = raza;
        this.valorArmadura = claseArmadura;

        // Inicialización de listas
        this.inventario = new ArrayList<>();
        this.habilidades = new ArrayList<>();
    }

    // ============================
    // GETTERS
    // ============================

    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public int getSalud() {
        return salud;
    }

    public int getSaludMaxima() {
        return saludMaxima;
    }

    public int getPoderBase() {
        return poderBase;
    }

    public String getRaza() {
        return raza;
    }

    public int getValorArmadura() {
        return valorArmadura;
    }

    public Armas getArmaEquipada() {
        return armaEquipada;
    }

    public List<Consumibles> getInventario() {
        return inventario;
    }

    /**
     * Devuelve la lista de habilidades del personaje.
     */
    public List<Habilidad> getHabilidades() {
        return habilidades;
    }

    // ============================
    // SETTERS
    // ============================

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setSalud(int salud) {

        if (salud < 0) {
            this.salud = 0;
            return;
        }

        if (salud > this.saludMaxima) {
            this.salud = this.saludMaxima;
            return;
        }

        this.salud = salud;
    }

    public void setSaludMaxima(int saludMaxima) {

        if (saludMaxima < 0) {
            this.saludMaxima = 0;
        } else {
            this.saludMaxima = saludMaxima;
        }

        if (this.salud > this.saludMaxima) {
            this.salud = this.saludMaxima;
        }
    }

    public void setValorArmadura(int valor) {
        this.valorArmadura = valor;
    }

    public void setPoderBase(int poderBase) {
        this.poderBase = poderBase;
    }

    // ============================
    // HABILIDADES
    // ============================

    /**
     * Permite añadir una habilidad al personaje.
     * Se usará en las clases hijas (Guerrero, Mago, etc.).
     */
    public void agregarHabilidad(Habilidad habilidad) {
        this.habilidades.add(habilidad);
    }

    // ============================
    // COMBATE
    // ============================

    /**
     * Método abstracto que define cómo ataca cada personaje.
     */
    public abstract Ataque atacar(Personaje objetivo);

    /**
     * Reduce la salud teniendo en cuenta la armadura.
     */
    public void recibirDanio(int danio) {

        int danioFinal = danio - valorArmadura;

        if (danioFinal < 0)
            danioFinal = 0;

        this.salud -= danioFinal;

        if (this.salud < 0)
            this.salud = 0;

        System.out.println(nombre + " recibe " + danioFinal + " de daño.");
    }

    public boolean estaVivo() {
        return this.salud > 0;
    }

    // ============================
    // INVENTARIO
    // ============================

    public void cargarConsumiblesIniciales() {

        inventario.clear();

        inventario.add(new PocionCuracion("Poción de Curación", 30));
        inventario.add(new PocionMana("Poción de Maná", 20));
        inventario.add(new PocionFuerza("Poción de Fuerza", 10));

        System.out.println(nombre + " recibe consumibles iniciales.");
    }

    public void usarConsumible(int index, Personaje objetivo) {

        if (index < 0 || index >= inventario.size()) {
            System.out.println("Consumible inválido.");
            return;
        }

        Consumibles consumible = inventario.get(index);

        consumible.usar(objetivo);

        inventario.remove(index);
    }

    public String getRol() {
        return this.getClass().getSimpleName();
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj)
            return true;

        if (obj == null || getClass() != obj.getClass())
            return false;

        Personaje that = (Personaje) obj;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}