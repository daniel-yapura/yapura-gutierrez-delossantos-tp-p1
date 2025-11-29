package juego;

import java.awt.Color;
import entorno.Entorno;


/**
 * Representa una "carta" seleccionable en la UI superior.
 * Sabe cómo dibujarse (con su ícono) y si está en cooldown.
 */
public class CartaPlanta {
    
    // --- ATRIBUTOS ---
    
    /**
     * MEJORA: Se añadió 'final' a todas las variables.
     * Las propiedades de una carta (posición, tamaño, tipo)
     * se definen en el constructor y nunca cambian.
     * Esto hace la clase "inmutable" y previene bugs.
     */
    private final int x; // Coordenada X (esquina superior izquierda)
    private final int y; // Coordenada Y (esquina superior izquierda)
    private final int alto;
    private final int ancho;
    private final int tipoPlanta; // 0 = RoseBlade
    
    /**
     * Constructor de la Carta.
     * MEJORA: Se añadió JavaDoc completo para los parámetros.
     * @param x Coordenada X (esquina superior izquierda)
     * @param y Coordenada Y (esquina superior izquierda)
     * @param alto Alto de la carta
     * @param ancho Ancho de la carta
     * @param tipoPlanta El tipo de planta que representa (0, 1, 2...)
     */
    CartaPlanta(int x, int y, int alto, int ancho, int tipoPlanta){
        this.x = x;
        this.y = y;
        this.alto = alto;
        this.ancho = ancho;
        this.tipoPlanta = tipoPlanta;
    }
    
    // --- MÉTODOS PÚBLICOS ---
    
    /**
     * Dibuja la carta, su ícono, y el velo de cooldown progresivo.
     * @param entorno El entorno de dibujado.
     * @param progresoCooldown El progreso de la recarga (un valor de 0.0 a 1.0).
     */
    public void dibujarse(Entorno entorno, double progresoCooldown) {
        // Calcula el centro para los métodos de dibujado del entorno
        double centroX = this.x + this.ancho / 2;
        double centroY = this.y + this.alto / 2;
            
        // 1. Dibujar el fondo blanco
        entorno.dibujarRectangulo(centroX, centroY, this.ancho, this.alto, 0, Color.WHITE);
        
        // 2. Dibujar el ícono de la planta (RoseBlade)
        if (this.tipoPlanta == 0) { // 0 = RoseBlade
            entorno.dibujarCirculo(centroX, centroY, this.ancho / 2, Color.RED);
        }
        // (Se eliminó el 'else if' de WallNut, ya que no es obligatorio)
        
        // --- 3. DIBUJAR EL VELO PROGRESIVO ---
        
        // Si el progreso es menor a 1.0 (no está cargada)...
        if (progresoCooldown < 1.0) {
            
            // 1. Calculamos la altura de la parte "cargada" (en la parte de abajo)
            double alturaCargada = this.alto * progresoCooldown;

            // 2. Calculamos la altura de la parte "vacía" (el velo gris de arriba)
            double alturaVacia = this.alto - alturaCargada;
            
            // 3. Calculamos el centroY del velo "vacío"
            // (Alineado con la parte SUPERIOR de la carta)
            double centroYVeloVacio = this.y + (alturaVacia / 2);

            // 4. Dibujar el velo gris
            Color veloGris = new Color(50, 50, 50, 150); // Semitransparente
            entorno.dibujarRectangulo(centroX, centroYVeloVacio, this.ancho, alturaVacia, 0, veloGris);
        }
    }
    
    /**
     * Comprueba si un clic fue dentro de los bordes de esta carta.
     * MEJORA: Se añadieron etiquetas @param para los parámetros.
     * @param mouseX La coordenada X del clic del mouse.
     * @param mouseY La coordenada Y del clic del mouse.
     * @return true si el clic fue dentro de los bordes, false si no.
     */
    public boolean fueClickeado(int mouseX, int mouseY ) {
        // La lógica es correcta: comprueba si el punto (mouseX, mouseY)
        // está entre los bordes izquierdo/derecho E y entre los bordes sup/inf.
        return (mouseX > this.x && mouseX < this.x + this.ancho &&
                mouseY > this.y && mouseY < this.y + this.alto);
    }
}