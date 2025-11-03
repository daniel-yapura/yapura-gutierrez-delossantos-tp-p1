package juego;
import java.awt.Color;
import entorno.Entorno;

public class ZombieGrinch {
    private static final double VELOCIDAD = 0.5;
    private static final int VIDA_INICIAL = 4;
    private static final double DIAMETRO_ZOMBIE = 45;
    private static final double DIAMETRO_AURA = 50;
    private double x, y;
    private int vida;

    public ZombieGrinch(double x, double y) {
        this.x = x; this.y = y;
        this.vida = VIDA_INICIAL;
    }
    public void dibujarse(Entorno entorno) {
        if (this.vida == VIDA_INICIAL) {
            entorno.dibujarCirculo(this.x, this.y, DIAMETRO_AURA, Color.WHITE);
        } else if (this.vida > 1) {
            entorno.dibujarCirculo(this.x, this.y, DIAMETRO_AURA, Color.YELLOW);
        } else {
            entorno.dibujarCirculo(this.x, this.y, DIAMETRO_AURA, Color.RED);
        }
        entorno.dibujarCirculo(this.x, this.y, DIAMETRO_ZOMBIE, Color.GREEN);
    }
    public void moverse() { this.x -= VELOCIDAD; }
    public void recibirDisparo() { this.vida -= 1; }
    public double getX() { return this.x; }
    public double getY() { return this.y; }
    public int getVida() { return this.vida; }
}