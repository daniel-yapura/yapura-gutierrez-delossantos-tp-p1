package juego;

import java.awt.Color;
import entorno.Entorno;
// MEJORA: Imports de Image y Herramientas eliminados (no se usan).

/**
 * Representa el objetivo "Regalo" que debe ser protegido.
 * Es un objeto estático (no se mueve) y es inmutable.
 */
public class Regalo {
    
    // --- CONSTANTES ---
    /**
     * MEJORA: Se extrajeron los "números mágicos" (30, 30) a constantes.
     * Si quieres cambiar el tamaño de los regalos, solo lo cambias aquí.
     */
    private static final double ANCHO = 30;
    private static final double ALTO = 30;

    // --- ATRIBUTOS ---
    /**
     * MEJORA CRÍTICA: Se encapsularon las variables como 'private final'.
     * 'final' porque un regalo nunca cambia de posición una vez creado.
     * 'private' para que la clase 'Juego' no pueda modificar su 'x' o 'y'.
     */
    private final double x; // Coordenada X (centro)
    private final double y; // Coordenada Y (centro)

    /**
     * Constructor del Regalo.
     * @param x Posición inicial en x (centro)
     * @param y Posición inicial en y (centro)
     */
    public Regalo(double x, double y) {
        this.x = x;
        this.y = y;
    }

    // --- MÉTODOS PÚBLICOS ---

    /**
     * Dibuja el regalo (un rectángulo azul).
     * @param entorno El contexto gráfico donde se dibujará.
     */
    public void dibujarse(Entorno entorno) {
        // MEJORA: Ahora usa las constantes ANCHO y ALTO
        entorno.dibujarRectangulo(this.x, this.y, ANCHO, ALTO, 0, Color.BLUE);
    }
    
    // --- GETTERS ---
    /**
     * MEJORA: Se añaden 'Getters' públicos.
     * Ya que 'x' e 'y' son 'private', la clase Juego necesita
     * una forma de LEER la posición para el chequeo de colisiones.
     */
    
    /**
     * @return La coordenada X actual del centro del regalo.
     */
    public double getX() {
        return this.x;
    }
    
    /**
     * @return La coordenada Y actual del centro del regalo.
     */
    public double getY() {
        return this.y;
    }
}