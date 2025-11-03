package juego;
import java.awt.Color;
import entorno.Entorno;

public class RoseBlade {
    private static final double VELOCIDAD_MOVIMIENTO = 4;
    private static final double DIAMETRO_PLANTA = 45;
    private static final double DIAMETRO_BORDE = 50;
    private static final double MARGEN_MOVIMIENTO = 0.1;
    private double x, y, targetX, targetY;
    private BolaDeFuego bola;

    public RoseBlade(double x, double y) {
        this.x = x; this.y = y; this.bola = null;
        this.targetX = x; this.targetY = y;
    }
    public void dibujarse(Entorno entorno, boolean estaSeleccionada) {
        if (estaSeleccionada) {
            entorno.dibujarCirculo(this.x, this.y, DIAMETRO_BORDE, Color.MAGENTA);
        }
        entorno.dibujarCirculo(this.x, this.y, DIAMETRO_PLANTA, Color.RED);
    }
    public void disparar() {
        if (this.bola == null) { this.bola = new BolaDeFuego(this.x, this.y); }
    }
    public void actualizarMovimiento() {
        if (this.x < this.targetX) { this.x = Math.min(this.x + VELOCIDAD_MOVIMIENTO, this.targetX); }
        else if (this.x > this.targetX) { this.x = Math.max(this.x - VELOCIDAD_MOVIMIENTO, this.targetX); }
        if (this.y < this.targetY) { this.y = Math.min(this.y + VELOCIDAD_MOVIMIENTO, this.targetY); }
        else if (this.y > this.targetY) { this.y = Math.max(this.y - VELOCIDAD_MOVIMIENTO, this.targetY); }
    }
    public void setTarget(double newX, double newY) { this.targetX = newX; this.targetY = newY; }
    public void snapAPosicion(double newX, double newY) {
        this.x = newX; this.y = newY;
        this.targetX = newX; this.targetY = newY;
    }
    public boolean estaMoviendose() {
        boolean enMovimientoX = Math.abs(this.x - this.targetX) > MARGEN_MOVIMIENTO;
        boolean enMovimientoY = Math.abs(this.y - this.targetY) > MARGEN_MOVIMIENTO;
        return enMovimientoX || enMovimientoY;
    }
    public double getX() { return this.x; }
    public double getY() { return this.y; }
    public BolaDeFuego getBola() { return this.bola; }
    public void setBola(BolaDeFuego bola) { this.bola = bola; }
}