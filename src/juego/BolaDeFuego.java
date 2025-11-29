package juego;

import java.awt.Color;
import entorno.Entorno;
// MEJORA: Imports de Image y Herramientas eliminados (no se usan).

/**
 * Representa el proyectil "BolaDeFuego" disparado por la RoseBlade.
 * Es responsable de gestionar su propia posición, dibujado y movimiento.
 */
public class BolaDeFuego {
    
    // --- CONSTANTES ---
    /**
     * MEJORA: Se extrajeron los "números mágicos" (6 y 15) a constantes.
     * Si quieres que el disparo sea más rápido o más grande,
     * solo cambias estos valores aquí, en un solo lugar.
     */
    private static final double VELOCIDAD = 6;
    private static final double DIAMETRO = 15;

    // --- ATRIBUTOS ---
    /**
     * MEJORA CRÍTICA: Se encapsularon las variables como 'private'.
     * Esto previene que otras clases (como 'Juego') modifiquen
     * la posición de la bola por accidente. La bola es la ÚNICA
     * responsable de modificar su 'x' (en su método moverse()).
     */
    private double x; // Coordenada X (centro)
    private double y; // Coordenada Y (centro)

    /**
     * Constructor de la bola de fuego.
     * @param x Posición inicial en x (centro)
     * @param y Posición inicial en y (centro)
     */
    public BolaDeFuego(double x, double y) {
        this.x = x;
        this.y = y;
    }

    // --- MÉTODOS PÚBLICOS ---

    /**
     * Dibuja la bola de fuego (un círculo negro).
     * MEJORA: Añadido @param para el JavaDoc.
     * @param entorno El contexto gráfico donde se dibujará.
     */
    public void dibujarse(Entorno entorno) {
        entorno.dibujarCirculo(this.x, this.y, DIAMETRO, Color.BLACK);
    }

    /**
     * Mueve la bola de fuego un paso hacia la derecha,
     * basado en su constante de VELOCIDAD.
     */
    public void moverse() {
        this.x += VELOCIDAD;
    }
    
    // --- GETTERS ---
    /**
     * MEJORA: Se añaden 'Getters' públicos.
     * Ya que 'x' e 'y' son 'private', la clase Juego necesita
     * una forma de LEER (pero no escribir) la posición
     * para el chequeo de colisiones.
     */
    
    /**
     * @return La coordenada X actual del centro de la bola.
     */
    public double getX() {
        return this.x;
    }
    
    /**
     * @return La coordenada Y actual del centro de la bola.
     */
    public double getY() {
        return this.y;
    }
}