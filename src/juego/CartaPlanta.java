package juego;
import java.awt.Color;
import entorno.Entorno;

/**
 * Representa una "carta" seleccionable en la UI superior.
 * Sabe cómo dibujarse (con su ícono) y si está en cooldown.
 */
public class CartaPlanta {
    
    private final int x; // Coordenada X (esquina superior izquierda)
    private final int y; // Coordenada Y (esquina superior izquierda)
    private final int alto;
    private final int ancho;
    private final int tipoPlanta; // 0 = RoseBlade
    
    /**
     * Constructor de la Carta.
     */
    CartaPlanta(int x, int y, int alto, int ancho, int tipoPlanta){
        this.x = x;
        this.y = y;
        this.alto = alto;
        this.ancho = ancho;
        this.tipoPlanta = tipoPlanta;
    }
    
    /**
     * Dibuja la carta, su ícono, y el velo de cooldown progresivo.
     * @param entorno El entorno de dibujado.
     * @param progresoCooldown El progreso de la recarga (un valor de 0.0 a 1.0).
     */
    public void dibujarse(Entorno entorno, double progresoCooldown) {
        double centroX = this.x + this.ancho / 2;
        double centroY = this.y + this.alto / 2;
            
        // 1. Dibujar el fondo blanco
        entorno.dibujarRectangulo(centroX, centroY, this.ancho, this.alto, 0, Color.WHITE);
        
        // 2. Dibujar el ícono de la planta (RoseBlade)
        if (this.tipoPlanta == 0) { // 0 = RoseBlade
            entorno.dibujarCirculo(centroX, centroY, this.ancho / 2, Color.RED);
        }
        
        // 3. Dibujar el velo progresivo
        if (progresoCooldown < 1.0) {
            double alturaCargada = this.alto * progresoCooldown;
            double alturaVacia = this.alto - alturaCargada;
            double centroYVeloVacio = this.y + (alturaVacia / 2);
            Color veloGris = new Color(50, 50, 50, 150); // Semitransparente
            entorno.dibujarRectangulo(centroX, centroYVeloVacio, this.ancho, alturaVacia, 0, veloGris);
        }
    }
    
    /**
     * Comprueba si un clic fue dentro de los bordes de esta carta.
     * @return true si el clic fue dentro de los bordes, false si no.
     */
    public boolean fueClickeado(int mouseX, int mouseY ) {
        return (mouseX > this.x && mouseX < this.x + this.ancho &&
                mouseY > this.y && mouseY < this.y + this.alto);
    }
}