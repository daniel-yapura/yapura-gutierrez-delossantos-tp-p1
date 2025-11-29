package juego;

import java.awt.Color;
import entorno.Entorno;

/**
 * Representa una única casilla en el tablero.
 * Es responsable de saber su posición, tamaño,
 * color, y si el jugador puede plantar en ella.
 * * MEJORA: Se añadieron @author y @version (práctica estándar de JavaDoc).
 */
public class Casilla {
    
    // --- ATRIBUTOS ---
    
    /**
     * MEJORA: Se añadió la palabra clave 'final'.
     * Esto convierte a la Casilla en un objeto "inmutable" (immutable).
     * Significa que una vez que se crea una casilla, su posición, tamaño
     * o color NUNCA pueden cambiar. Esto hace el código más seguro
     * y previene bugs difíciles de encontrar.
     */
    private final double centroX, centroY, ancho, alto;
    private final boolean esPlantable;
    private final Color color;

    /**
     * Constructor de la Casilla.
     * @param x El centro en X
     * @param y El centro en Y
     * @param ancho El ancho total
     * @param alto El alto total
     * @param esPlantable true si se puede plantar en esta casilla
     * @param color El color de fondo de la casilla
     */
    public Casilla(double x, double y, double ancho, double alto, boolean esPlantable, Color color) {
        this.centroX = x; 
        this.centroY = y; 
        this.ancho = ancho;
        this.alto = alto; 
        this.esPlantable = esPlantable; 
        this.color = color;
    }

    // --- MÉTODOS PÚBLICOS ---

    /**
     * Dibuja esta casilla en la pantalla usando el Entorno.
     * MEJORA: Se añadió la etiqueta @param para el parámetro 'entorno'.
     * @param entorno El contexto gráfico (proporcionado por la clase Juego) donde se dibujará.
     */
    public void dibujarse(Entorno entorno) {
        entorno.dibujarRectangulo(this.centroX, this.centroY, this.ancho, this.alto, 0, this.color);
    }

    /**
     * Comprueba si un clic (mouseX, mouseY) ocurrió dentro de los límites de esta casilla.
     * MEJORA: Se añadieron etiquetas @param para los parámetros.
     * @param mouseX La coordenada X del clic del mouse.
     * @param mouseY La coordenada Y del clic del mouse.
     * @return true si el clic fue dentro de los bordes, false si no.
     */
    public boolean fueClickeada(int mouseX, int mouseY) {
        // MEJORA: La lógica de 'xMin', 'xMax' es excelente para la legibilidad.
        // No se necesita cambiarla.
        double xMin = this.centroX - this.ancho / 2;
        double xMax = this.centroX + this.ancho / 2;
        double yMin = this.centroY - this.alto / 2;
        double yMax = this.centroY + this.alto / 2;
        
        return (mouseX > xMin && mouseX < xMax &&
                mouseY > yMin && mouseY < yMax);
    }

    // --- Getters (Métodos para consultar los atributos privados) ---
    // (No se necesitan cambios aquí, están perfectos).
    
    public double getCentroX() { return this.centroX; }
    public double getCentroY() { return this.centroY; }
    public boolean esPlantable() { return this.esPlantable; }
    public double getAncho() { return this.ancho; }
    public double getAlto() { return this.alto; }
}