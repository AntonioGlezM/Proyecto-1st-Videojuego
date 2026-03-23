package view;

import java.util.List;
import java.util.Scanner;

import model.habilidades.Habilidad;
import model.personajes.Personaje;
import model.personajes.PersonajeMagico;
import model.resultado.ResultadoTurno;

/**
 * Clase Vista del combate RPG.
 *
 * Responsabilidades:
 * - Mostrar toda la información por pantalla (mensajes, menús, resultados).
 * - Recoger la entrada del usuario (opciones, nombres).
 *
 * NO contiene lógica del juego.
 * NO toma decisiones. Solo muestra y pregunta.
 *
 * @author Antonio Gonzalez Martel
 */
public class View {

    private final Scanner scanner = new Scanner(System.in);

    // ============================
    // MENÚ PRINCIPAL
    // ============================

    public void mostrarMenuPrincipal() {
        System.out.println("\n=================================");
        System.out.println("      JUEGO DE COMBATE RPG");
        System.out.println("=================================");
        System.out.println("1 - Iniciar combate");
        System.out.println("2 - Salir");
        System.out.print("Elige opción: ");
    }

    public void mostrarSalida() {
        System.out.println("Saliendo del juego...");
    }

    public void mostrarOpcionInvalida() {
        System.out.println("Opción inválida.");
    }

    public int leerOpcion() {
        return scanner.nextInt();
    }

    // ============================
    // JUGAR DE NUEVO
    // ============================

    /**
     * Muestra el menú de jugar de nuevo tras terminar un combate.
     * Se llama desde Main después de que iniciarCombate() retorne.
     */
    public void mostrarMenuJugarDeNuevo() {
        System.out.println("\n=================================");
        System.out.println("1 - Jugar de nuevo");
        System.out.println("2 - Salir");
        System.out.print("Elige opción: ");
    }

    // ============================
    // RESULTADO DE TURNO
    // ============================

    /**
     * Muestra todos los mensajes acumulados en un ResultadoTurno.
     *
     * Este método es el punto de entrada principal para el front gráfico:
     * en lugar de llamar a métodos individuales uno a uno, el controlador
     * construye un ResultadoTurno con toda la info del turno y la vista
     * lo renderiza de golpe aquí.
     *
     * En consola simplemente imprime los mensajes en orden.
     * En un front gráfico este método se sustituirá por animaciones,
     * barras de salud, efectos visuales, etc.
     *
     * @param resultado El resultado completo del turno generado por el controlador.
     */
    public void mostrarResultadoTurno(ResultadoTurno resultado) {
        for (String mensaje : resultado.getMensajes()) {
            System.out.println(mensaje);
        }
    }

    // ============================
    // SELECCIÓN DE PERSONAJE
    // ============================

    public void mostrarMenuSeleccionPersonaje(String jugador) {
        System.out.println("=================================");
        System.out.println(jugador + " - Selecciona personaje");
        System.out.println("1 - Guerrero");
        System.out.println("2 - Asesino");
        System.out.println("3 - Barbaro");
        System.out.println("4 - Clerigo");
        System.out.println("5 - Mago");
        System.out.println("6 - Paladin");
        System.out.println("7 - Picaro");
        System.out.println("8 - Cazador");
        System.out.print("Opción: ");
    }

    public int leerSeleccionPersonaje() {
        return scanner.nextInt();
    }

    public void pedirNombrePersonaje() {
        System.out.print("Nombre del personaje: ");
    }

    public String leerNombrePersonaje() {
        return scanner.next();
    }

    public void mostrarPersonajeInvalido() {
        System.out.println("Opción no válida. Se asigna Asesino por defecto.");
    }

    // ============================
    // STATS INICIALES
    // ============================

    /**
     * Muestra un resumen de las estadísticas del personaje antes de empezar el combate.
     * Incluye nombre, clase, raza, salud, poder base, armadura y habilidad especial.
     * Si el personaje es mágico, muestra también el maná.
     *
     * @param personaje         El personaje a mostrar.
     * @param nombreHabEspecial Nombre de su habilidad especial.
     */
    public void mostrarStatsIniciales(Personaje personaje, String nombreHabEspecial) {
        System.out.println("\n---------------------------------");
        System.out.println("  " + personaje.getNombre() + " (" + personaje.getRol() + ")");
        System.out.println("---------------------------------");
        System.out.println("Raza          : " + personaje.getRaza());
        System.out.println("Salud         : " + personaje.getSalud());
        System.out.println("Poder base    : " + personaje.getPoderBase());
        System.out.println("Armadura      : " + personaje.getValorArmadura());

        // Si el personaje es mágico, mostramos también su maná
        if (personaje instanceof PersonajeMagico) {
            PersonajeMagico magico = (PersonajeMagico) personaje;
            System.out.println("Maná          : " + magico.getMana());
        }

        System.out.println("Hab. especial : " + nombreHabEspecial);
        System.out.println("---------------------------------");
    }

    // ============================
    // INICIO DE COMBATE
    // ============================

    public void mostrarInicioCombate() {
        System.out.println("\n========== COMBATE ==========");
    }

    public void mostrarIniciativa(String nombreGanador) {
        System.out.println("\nEmpieza atacando: " + nombreGanador);
    }

    // ============================
    // TURNO
    // ============================

    /**
     * Muestra la cabecera del turno con el nombre del personaje,
     * su salud actual y los estados activos que tiene en ese momento.
     *
     * @param atacante       El personaje cuyo turno comienza.
     * @param estadosActivos String con los efectos activos (quemadura, marca, etc.)
     *                       construido por el controlador. Vacío si no hay ninguno.
     */
    public void mostrarCabeceraTurno(Personaje atacante, String estadosActivos) {
        System.out.println("\n=================================");
        System.out.println("Es el turno de " + atacante.getNombre());
        System.out.println("Salud actual : " + atacante.getSalud());

        // Solo mostramos la línea de estados si hay alguno activo
        if (!estadosActivos.isEmpty()) {
            System.out.println("Estados      : " + estadosActivos);
        }

        System.out.println("=================================");
    }

    /**
     * Muestra el menú de acciones con la habilidad especial dinámica.
     *
     * La opción 2 cambia según el personaje:
     * - Guerrero           → "Postura Defensiva"
     * - Paladín            → "Furia Divina"
     * - Asesino / Pícaro   → "Sigilo"
     * - Bárbaro            → "Furia Berserker"
     * - Mago               → "Distorsión Temporal"
     * - Clérigo            → "Himno Divino"
     * - Cazador            → "Marca del Cazador"
     *
     * @param nombreHabilidadEspecial Nombre obtenido del personaje en el controlador.
     */
    public void mostrarMenuAcciones(String nombreHabilidadEspecial) {
        System.out.println("1 - Atacar");
        System.out.println("2 - " + nombreHabilidadEspecial);
        System.out.println("3 - Usar consumible");
        System.out.println("4 - Pasar turno");
        System.out.print("Elige opción: ");
    }

    public void mostrarTurnoPasado() {
        System.out.println("Turno pasado.");
    }

    // ============================
    // HABILIDADES
    // ============================

    public void mostrarMenuHabilidades(List<Habilidad> habilidades) {
        System.out.println("\n--- HABILIDADES ---");
        for (int i = 0; i < habilidades.size(); i++) {
            System.out.println((i + 1) + " - " + habilidades.get(i).getNombre()
                    + " (Daño: " + habilidades.get(i).getDaño()
                    + " | Coste: " + habilidades.get(i).getCoste() + ")");
        }
        System.out.println("0 - Volver");
        System.out.print("Elige habilidad: ");
    }

    public void mostrarSinHabilidades() {
        System.out.println("Este personaje no tiene habilidades.");
    }

    public void mostrarHabilidadInvalida() {
        System.out.println("Habilidad inválida.");
    }

    // ============================
    // RESULTADO DEL ATAQUE
    // ============================

    public void mostrarAtaqueFallado() {
        System.out.println("El ataque ha FALLADO.");
    }

    public void mostrarGolpeCritico() {
        System.out.println("¡GOLPE CRÍTICO!");
    }

    public void mostrarUsoHabilidad(String nombreHabilidad) {
        System.out.println("Usando habilidad: " + nombreHabilidad);
    }

    public void mostrarResultadoAtaque(int dañoReal, String nombreDefensor, int saludRestante) {
        System.out.println("Daño causado: " + dañoReal);
        System.out.println("Salud restante de " + nombreDefensor + ": " + saludRestante);
    }

    public void mostrarManaInsuficiente() {
        System.out.println("No tienes suficiente mana.");
    }

    public void mostrarManaRestante(int mana) {
        System.out.println("Mana restante: " + mana);
    }

    // ============================
    // HABILIDADES ESPECIALES — GENERALES
    // ============================

    /**
     * Informa de que la habilidad especial está en cooldown.
     */
    public void mostrarHabilidadEspecialEnCooldown() {
        System.out.println("Habilidad especial en cooldown. Debes esperar 3 turnos.");
    }

    /**
     * Informa de que el personaje no tiene habilidad especial.
     */
    public void mostrarSinHabilidadEspecial() {
        System.out.println("Este personaje no tiene habilidad especial.");
    }

    // ============================
    // HABILIDADES ESPECIALES — SIGILO (Asesino / Pícaro)
    // ============================

    /**
     * Informa de que el personaje ha entrado en sigilo.
     *
     * @param nombre Nombre del personaje que entra en sigilo.
     */
    public void mostrarSigiloActivado(String nombre) {
        System.out.println(nombre + " se funde con las sombras. ¡El rival no puede atacarle este turno!");
    }

    /**
     * Informa de que el defensor está en sigilo y el atacante pierde su turno.
     *
     * @param nombreDefensor Nombre del personaje en sigilo.
     */
    public void mostrarDefensorEnSigilo(String nombreDefensor) {
        System.out.println(nombreDefensor + " está oculto en las sombras. ¡No puedes atacarle!");
        System.out.println("Tu turno se pierde.");
    }

    // ============================
    // HABILIDADES ESPECIALES — FURIA BERSERKER (Bárbaro)
    // ============================

    /**
     * Informa de que el Bárbaro ha activado la Furia Berserker.
     *
     * @param nombre Nombre del Bárbaro.
     */
    public void mostrarFuriaActivada(String nombre) {
        System.out.println("¡" + nombre + " entra en FURIA BERSERKER! Más daño, menos defensa.");
    }

    /**
     * Informa de que la Furia Berserker ha terminado al inicio del nuevo turno.
     *
     * @param nombre Nombre del Bárbaro.
     */
    public void mostrarFuriaRevertida(String nombre) {
        System.out.println("La furia de " + nombre + " se disipa. Sus stats vuelven a la normalidad.");
    }

    // ============================
    // HABILIDADES ESPECIALES — POSTURA DEFENSIVA (Guerrero)
    // ============================

    /**
     * Informa de que el Guerrero ha activado la Postura Defensiva.
     *
     * @param nombre Nombre del personaje.
     */
    public void mostrarPosturaActivada(String nombre) {
        System.out.println(nombre + " adopta una Postura Defensiva. ¡Su armadura aumenta!");
    }

    /**
     * Informa de que la Postura Defensiva ha terminado al inicio del nuevo turno.
     *
     * @param nombre Nombre del personaje.
     */
    public void mostrarPosturaRevertida(String nombre) {
        System.out.println("La postura defensiva de " + nombre + " termina. Su armadura vuelve a la normalidad.");
    }

    // ============================
    // HABILIDADES ESPECIALES — DISTORSIÓN TEMPORAL (Mago)
    // ============================

    /**
     * Informa de que el Mago ha activado la Distorsión Temporal.
     *
     * @param nombre Nombre del Mago.
     */
    public void mostrarDistorsionActivada(String nombre) {
        System.out.println(nombre + " distorsiona el tiempo. ¡Podrá atacar dos veces en su siguiente turno!");
    }

    /**
     * Informa de que el doble ataque de la Distorsión Temporal está activo.
     *
     * @param nombre Nombre del Mago.
     */
    public void mostrarDobleAtaqueActivo(String nombre) {
        System.out.println("⚡ ¡DISTORSIÓN TEMPORAL! " + nombre + " ataca dos veces este turno.");
    }

    /**
     * Indica que el Mago va a ejecutar su primer ataque del doble turno.
     */
    public void mostrarPrimerAtaque() {
        System.out.println("\n--- Primer ataque ---");
    }

    /**
     * Indica que el Mago va a ejecutar su segundo ataque del doble turno.
     */
    public void mostrarSegundoAtaque() {
        System.out.println("\n--- Segundo ataque ---");
    }

    // ============================
    // HABILIDADES ESPECIALES — HIMNO DIVINO (Clérigo)
    // ============================

    /**
     * Informa de que el Clérigo ha activado el Himno Divino.
     *
     * @param nombre Nombre del Clérigo.
     */
    public void mostrarHimnoDivinoActivado(String nombre) {
        System.out.println(nombre + " entona el Himno Divino. ¡Su próximo ataque dejará quemadura sagrada!");
    }

    /**
     * Informa de que la quemadura sagrada ha sido aplicada o está haciendo daño.
     *
     * @param nombreVictima   Nombre del personaje quemado.
     * @param danio           Daño de quemadura aplicado este turno.
     * @param turnosRestantes Turnos de quemadura que quedan tras este.
     */
    public void mostrarQuemaduraAplicada(String nombreVictima, int danio, int turnosRestantes) {
        System.out.println("🔥 " + nombreVictima + " sufre " + danio
                + " de quemadura sagrada. (" + turnosRestantes + " turnos restantes)");
    }

    // ============================
    // HABILIDADES ESPECIALES — FURIA DIVINA (Paladín)
    // ============================

    /**
     * Informa de que el Paladín ha iniciado la carga de Furia Divina.
     *
     * @param nombre Nombre del Paladín.
     */
    public void mostrarFuriaDivinaIniciada(String nombre) {
        System.out.println(nombre + " comienza a cargar la FURIA DIVINA. ¡Necesita 1 turno más!");
    }

    /**
     * Informa de que el Paladín sigue cargando la Furia Divina.
     *
     * @param nombre          Nombre del Paladín.
     * @param turnosRestantes Turnos de carga que quedan.
     */
    public void mostrarFuriaDivinaCargando(String nombre, int turnosRestantes) {
        System.out.println(nombre + " sigue cargando... (" + turnosRestantes + " turno/s restante/s)");
    }

    /**
     * Informa de que la carga de Furia Divina ha completado.
     *
     * @param nombre Nombre del Paladín.
     */
    public void mostrarFuriaDivinaLista(String nombre) {
        System.out.println("⚡ ¡La FURIA DIVINA de " + nombre + " está cargada! El siguiente ataque hará x3 de daño.");
    }

    /**
     * Informa de que el Paladín ha descargado la Furia Divina.
     *
     * @param nombre     Nombre del Paladín.
     * @param danioTotal Daño total del golpe devastador.
     */
    public void mostrarFuriaDivinaDescargada(String nombre, int danioTotal) {
        System.out.println("💥 ¡" + nombre + " descarga la FURIA DIVINA! Daño devastador: " + danioTotal);
    }

    // ============================
    // HABILIDADES ESPECIALES — MARCA DEL CAZADOR (Cazador)
    // ============================

    /**
     * Informa de que el Cazador ha marcado al rival.
     *
     * @param nombreCazador  Nombre del Cazador.
     * @param nombreObjetivo Nombre del personaje marcado.
     */
    public void mostrarMarcaAplicada(String nombreCazador, String nombreObjetivo) {
        System.out.println("🎯 " + nombreCazador + " marca a " + nombreObjetivo
                + ". ¡Sus ataques ignorarán la armadura durante 2 turnos!");
    }

    /**
     * Informa de que el ataque del Cazador ignora la armadura del defensor marcado.
     *
     * @param nombreDefensor Nombre del personaje marcado.
     */
    public void mostrarAtaqueIgnoraArmadura(String nombreDefensor) {
        System.out.println("🎯 ¡El ataque ignora la armadura de " + nombreDefensor + "!");
    }

    /**
     * Informa de que la Marca del Cazador ha expirado sobre el personaje.
     *
     * @param nombreMarcado Nombre del personaje cuya marca ha expirado.
     */
    public void mostrarMarcaExpirada(String nombreMarcado) {
        System.out.println("La marca del Cazador sobre " + nombreMarcado + " ha expirado.");
    }

    // ============================
    // CONSUMIBLES
    // ============================

    public void mostrarInventarioVacio() {
        System.out.println("No tienes consumibles.");
    }

    public void mostrarMenuConsumibles(List<? extends model.consumibles.Consumibles> inventario) {
        for (int i = 0; i < inventario.size(); i++) {
            System.out.println((i + 1) + " - " + inventario.get(i).getNombre());
        }
        System.out.println("0 - Volver");
        System.out.print("Selecciona consumible: ");
    }

    /**
     * Informa de que la Poción de Maná no tiene efecto en personajes físicos.
     * El consumible NO se gasta.
     */
    public void mostrarPocionManaInutil() {
        System.out.println("¡No tienes maná! La Poción de Maná no tiene efecto en tu personaje.");
    }

    // ============================
    // FIN DE COMBATE
    // ============================

    public void mostrarDerrota(String nombreDerrotado) {
        System.out.println("\n" + nombreDerrotado + " ha sido derrotado.");
    }

    /**
     * Muestra el mensaje de victoria con el nombre del ganador.
     *
     * @param nombreGanador Nombre del personaje que ha ganado el combate.
     */
    public void mostrarVictoria(String nombreGanador) {
        System.out.println("\n🏆 ¡" + nombreGanador + " ha ganado el combate!");
    }

    public void mostrarFinCombate() {
        System.out.println("\n========= FIN DEL COMBATE =========");
    }
}