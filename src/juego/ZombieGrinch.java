package juego;

import java.awt.Color;
import java.awt.Image;
import entorno.Entorno;
import entorno.Herramientas;

public class ZombieGrinch {
    
    // --- CONSTANTES ---
    private BolaDeNieve bola;
    private int temporizadorDisparo = 0;
    
    private double velocidad; 
    private int vidaInicial; 
    
    private static final double DIAMETRO_AURA = 50;
    private boolean estaComiendo = false;

    // --- ATRIBUTOS ---
    private double x;
    private double y;
    private int vida;
    private int tipo; // 1: Normal, 2: Rápido, 3: Tanque
    private Image imagen;

    /**
     * Constructor modificado que acepta el TIPO de zombie.
     */
    public ZombieGrinch(double x, double y, int tipo) {
        this.x = x;
        this.y = y;
        this.tipo = tipo;

        String rutaImagen = ""; 

        if (this.tipo == 1) { 
            // TIPO 1: NORMAL (Equilibrado)
            this.vida = 4;
            this.vidaInicial = 4;
            this.velocidad = 0.5;
            rutaImagen = "recursos/ZOMBIEGRINCH.png"; 
        } 
        else if (this.tipo == 2) { 
            // TIPO 2: RUNNER (Rápido pero débil)
            this.vida = 2;
            this.vidaInicial = 2;
            this.velocidad = 0.95; 
            rutaImagen = "recursos/ZOMBIERAPIDO.png";        } 
        else if (this.tipo == 3) { 
            // TIPO 3: TANK (Lento pero muy resistente)
            this.vida = 8;       
            this.vidaInicial = 8;
            this.velocidad = 0.15; 
            rutaImagen = "recursos/ZOMBIETANQUE.png"; 
            } else {
            // Por defecto
            this.vida = 4;
            this.vidaInicial = 4;
            this.velocidad = 0.3;
            rutaImagen = "recursos/ZOMBIEGRINCH.png";
        }

        // CARGA DE IMAGEN CENTRALIZADA
        // Usamos la variable 'rutaImagen' que definimos arriba
        try {
            this.imagen = Herramientas.cargarImagen(rutaImagen);
        } catch (Exception e) {
            System.err.println("Error cargando imagen: " + rutaImagen);
            this.imagen = null;
        }
    }

    public void dibujarse(Entorno entorno) {
        // 1. DIBUJAR EL AURA (Vida)
       
        
        // 2. DIBUJAR EL ZOMBIE
        if (this.imagen != null) {
            double escala = 0.2;
            if (this.tipo == 3) escala = 0.2; 
            
            entorno.dibujarImagen(this.imagen, this.x, this.y, 0, escala); 
        } else {
            Color c = Color.GREEN;
            if (this.tipo == 2) c = Color.ORANGE;
            if (this.tipo == 3) c = Color.DARK_GRAY;
            entorno.dibujarCirculo(this.x, this.y, 45, c);
        }

        // 3. DIBUJAR LA BOLA
        if (this.bola != null) {
            this.bola.dibujarse(entorno);
        }
    }

    public void moverse() {
        this.temporizadorDisparo++;
        
        if (this.temporizadorDisparo > 300 && this.bola == null) {
            this.bola = new BolaDeNieve(this.x - 20, this.y);
            this.temporizadorDisparo = 0;
        }

        if (this.bola != null) {
            this.bola.mover();
            if (this.bola.getX() < 0) {
                this.bola = null;
            }
        }
        
        if (!this.estaComiendo) {
            this.x -= this.velocidad; 
        }
    }

    public void recibirDisparo() {
        this.vida -= 1;
    }
    
    // --- GETTERS y SETTERS ---
    public double getX() { return this.x; }
    public double getY() { return this.y; }
    public int getVida() { return this.vida; }
    
    public BolaDeNieve getBola() { return this.bola; }
    public void borrarBola() { this.bola = null; }
    public void setComiendo(boolean comiendo) { this.estaComiendo = comiendo; }
}