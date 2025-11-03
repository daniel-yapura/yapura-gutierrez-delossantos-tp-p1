package juego;
import java.awt.Color;
import entorno.Entorno;

public class Regalo {
    private static final double ANCHO = 30; 
    private static final double ALTO = 30;
    private final double x; 
    private final double y;

    public Regalo(double x, double y) { this.x = x; this.y = y; }
    public void dibujarse(Entorno entorno) {
        entorno.dibujarRectangulo(this.x, this.y, ANCHO, ALTO, 0, Color.BLUE);
    }
    public double getX() { return this.x; }
    public double getY() { return this.y; }
}