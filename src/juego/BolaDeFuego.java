package juego;
import java.awt.Color;
import entorno.Entorno;

public class BolaDeFuego {
    private static final double VELOCIDAD = 6;
    private static final double DIAMETRO = 15;
    private double x, y;

    public BolaDeFuego(double x, double y) { this.x = x; this.y = y; }
    public void dibujarse(Entorno entorno) {
        entorno.dibujarCirculo(this.x, this.y, DIAMETRO, Color.BLACK);
    }
    public void moverse() { this.x += VELOCIDAD; }
    public double getX() { return this.x; }
    public double getY() { return this.y; }
}