package juego;

import java.awt.Color;
import entorno.Entorno;

public class BolaDeNieve {
    private double x, y;
    private double angulo;
    private double velocidad = 3; // Velocidad del disparo

    public BolaDeNieve(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void dibujarse(Entorno entorno) {
        
        entorno.dibujarCirculo(this.x, this.y, 20, Color.CYAN);
    }

    public void mover() {
        // Se mueve hacia la IZQUIERDA (restamos X)
        this.x -= velocidad;
    }

    // Getters para las colisiones
    public double getX() { return x; }
    public double getY() { return y; }
}