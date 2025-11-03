package juego;

import java.awt.Color;
import entorno.Entorno;

/**
 * Representa la planta de ataque "RoseBlade".
 * Es responsable de gestionar su propia posición (actual y objetivo),
 * su dibujado (normal o seleccionada), y sus acciones (disparar).
 */
public class RoseBlade {

    // --- CONSTANTES ---
    /**
     * MEJORA: Se extrajeron los "números mágicos" a constantes.
     * Si quieres que la planta se mueva más rápido o cambie de tamaño,
     * solo modificas estos valores en un solo lugar.
     */
    private static final double VELOCIDAD_MOVIMIENTO = 4;
    private static final double DIAMETRO_PLANTA = 45;
    private static final double DIAMETRO_BORDE = 50;
    private static final double MARGEN_MOVIMIENTO = 0.1; // Margen de error para 'estaMoviendose'

    // --- ATRIBUTOS ---
    /**
     * MEJORA CRÍTICA: Se encapsularon las variables como 'private'.
     * Esto previene que la clase 'Juego' modifique la posición (x, y)
     * o la bola de la planta por accidente. La planta es la ÚNICA
     * responsable de modificar sus propios atributos.
     */
    private double x; // Posición actual en X
    private double y; // Posición actual en Y
    private BolaDeFuego bola;
    
    // Atributos de animación
    private double targetX; // Posición objetivo en X
    private double targetY; // Posición objetivo en Y

    /**
     * Constructor de la planta.
     * @param x Posición inicial en x (centro)
     * @param y Posición inicial en y (centro)
     */
    public RoseBlade(double x, double y) {
        this.x = x;
        this.y = y;
        this.bola = null;
        
        // El objetivo inicial es la misma posición (no se mueve)
        this.targetX = x;
        this.targetY = y;
    }

    // --- MÉTODOS PÚBLICOS (Acciones) ---

    /**
     * Dibuja la planta en la pantalla.
     * Si 'estaSeleccionada' es true, dibuja un borde violeta debajo.
     * @param entorno El contexto gráfico donde se dibujará.
     * @param estaSeleccionada true si la planta está activa.
     */
    public void dibujarse(Entorno entorno, boolean estaSeleccionada) {
        if (estaSeleccionada) {
            entorno.dibujarCirculo(this.x, this.y, DIAMETRO_BORDE, Color.MAGENTA);
        }
        entorno.dibujarCirculo(this.x, this.y, DIAMETRO_PLANTA, Color.RED);
    }
    
    /**
     * Intenta crear una nueva BolaDeFuego si no hay una activa.
     * El disparo se origina desde la posición actual de la planta.
     */
    public void disparar() {
        if (this.bola == null) {
            this.bola = new BolaDeFuego(this.x, this.y);
        }
    }

    /**
     * Actualiza la posición de la planta en cada frame,
     * moviéndola suavemente hacia su 'target'.
     * Este método debe ser llamado en CADA tick() desde Juego.java.
     */
    public void actualizarMovimiento() {
        // Mover en el eje X
        if (this.x < this.targetX) {
            this.x = Math.min(this.x + VELOCIDAD_MOVIMIENTO, this.targetX);
        } else if (this.x > this.targetX) {
            this.x = Math.max(this.x - VELOCIDAD_MOVIMIENTO, this.targetX);
        }
        
        // Mover en el eje Y
        if (this.y < this.targetY) {
            this.y = Math.min(this.y + VELOCIDAD_MOVIMIENTO, this.targetY);
        } else if (this.y > this.targetY) {
            this.y = Math.max(this.y - VELOCIDAD_MOVIMIENTO, this.targetY);
        }
    }

    /**
     * Establece un nuevo destino (objetivo) para la animación de movimiento.
     * Llamado por 'Juego' cuando se presiona WASD.
     * @param newX Nueva coordenada X objetivo
     * @param newY Nueva coordenada Y objetivo
     */
    public void setTarget(double newX, double newY) {
        this.targetX = newX;
        this.targetY = newY;
    }

    /**
     * MEJORA: Nuevo método para "teletransportar" la planta.
     * Fija la posición actual Y el objetivo en el mismo lugar.
     * Se usa al plantar para evitar la animación de "regreso".
     * @param newX Nueva coordenada X
     * @param newY Nueva coordenada Y
     */
    public void snapAPosicion(double newX, double newY) {
        this.x = newX;
        this.y = newY;
        this.targetX = newX;
        this.targetY = newY;
    }

    // --- MÉTODOS PÚBLICOS (Consultas / Getters y Setters) ---

    /**
     * Comprueba si la planta está actualmente en animación (moviéndose).
     * @return true si la posición actual no coincide con el objetivo.
     */
    public boolean estaMoviendose() {
        boolean enMovimientoX = Math.abs(this.x - this.targetX) > MARGEN_MOVIMIENTO;
        boolean enMovimientoY = Math.abs(this.y - this.targetY) > MARGEN_MOVIMIENTO;
        return enMovimientoX || enMovimientoY;
    }

    /**
     * MEJORA: Getters para permitir a 'Juego' LEER las posiciones
     * privadas para las colisiones.
     */
    public double getX() {
        return this.x;
    }
    
    public double getY() {
        return this.y;
    }
    
    public BolaDeFuego getBola() {
        return this.bola;
    }
    
    /**
     * MEJORA: Setter para permitir a 'Juego' poner la bola en 'null'
     * después de una colisión, respetando la encapsulación.
     * @param bola La nueva bola (generalmente 'null')
     */
    public void setBola(BolaDeFuego bola) {
        this.bola = bola;
    }
}