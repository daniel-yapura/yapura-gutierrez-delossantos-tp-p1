package juego;

import java.awt.Color;
import java.awt.Image;
import entorno.Entorno;
import entorno.Herramientas;

public class WallNut {
    private double x, y;
    private double targetX, targetY;
    private Image imagen;
    
    // Atributos de juego
    private int vida = 400; 
    private static final double VELOCIDAD_MOVIMIENTO = 3;

    public WallNut(double x, double y) {
        this.x = x;
        this.y = y;
        this.targetX = x;
        this.targetY = y;
        try {
            // Asegurate que esto coincida EXACTO con el nombre del archivo en Eclipse
            this.imagen = Herramientas.cargarImagen("recursos/WALLNUT.png"); 
        } catch (Exception e) {
            System.err.println("No se encontró la imagen. Se usará circulo.");
            this.imagen = null;
        }
    }

    public void dibujarse(Entorno entorno, boolean esSeleccionada) {
        // 1. El borde amarillo si está seleccionada (lo dejamos)
        if (esSeleccionada) {
            entorno.dibujarCirculo(this.x, this.y, 60, Color.YELLOW);
        }
        
        // 2. DIBUJAR LA IMAGEN (EN LUGAR DEL CÍRCULO MARRÓN)
        if (this.imagen != null) {
            // Parámetros: imagen, x, y, ángulo, ESCALA
            // Probá con 0.15, 0.18 o 0.2 hasta que el tamaño quede bien en la casilla.
            entorno.dibujarImagen(this.imagen, this.x, this.y, 0, 0.12); 
        } else {
            // Si falla la carga, usamos el círculo marrón de respaldo
            entorno.dibujarCirculo(this.x, this.y, 40, new Color(139, 69, 19)); 
        }
    }

    // --- CORRECCIÓN IMPORTANTE: Lógica de movimiento precisa ---
    public void actualizarMovimiento() {
        // Si la distancia es menor a la velocidad, "saltamos" directo al destino
        // Esto evita que la planta vibre o quede un poco corrida.
        if (Math.abs(this.x - this.targetX) < VELOCIDAD_MOVIMIENTO) {
            this.x = this.targetX;
        } else {
            if (this.x < this.targetX) this.x += VELOCIDAD_MOVIMIENTO;
            if (this.x > this.targetX) this.x -= VELOCIDAD_MOVIMIENTO;
        }

        if (Math.abs(this.y - this.targetY) < VELOCIDAD_MOVIMIENTO) {
            this.y = this.targetY;
        } else {
            if (this.y < this.targetY) this.y += VELOCIDAD_MOVIMIENTO;
            if (this.y > this.targetY) this.y -= VELOCIDAD_MOVIMIENTO;
        }
    }

    // --- NUEVO MÉTODO: Teletransportar (Para el fantasma del mouse) ---
    public void snapAPosicion(double x, double y) {
        this.x = x;
        this.y = y;
        this.targetX = x;
        this.targetY = y;
    }

    public void setTarget(double x, double y) {
        this.targetX = x;
        this.targetY = y;
    }
    
    public void recibirDanio(int danio) {
        this.vida -= danio;
    }

    public boolean estaMuerta() {
        return this.vida <= 0;
    }
    
    // Método para saber si se está moviendo (útil para la selección)
    public boolean estaMoviendose() {
        return (Math.abs(this.x - this.targetX) > 0.1 || Math.abs(this.y - this.targetY) > 0.1);
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public double getTargetX() { return this.targetX; }
    public double getTargetY() { return this.targetY; }
}