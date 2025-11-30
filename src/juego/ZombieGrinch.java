package juego;
import java.awt.Image;
import entorno.Herramientas;
import java.awt.Color;
import entorno.Entorno;

/**
 * Representa al enemigo "ZombieGrinch".
 * Es responsable de gestionar su propio movimiento, dibujado y vida.
 */
public class ZombieGrinch {
    
    // --- CONSTANTES ---
    /**
     * MEJORA: Se extrajeron los "números mágicos" a constantes.
     * Si quieres que los zombies sean más rápidos, resistentes o grandes,
     * solo modificas estos valores en un solo lugar.
     */
	private BolaDeNieve bola;
	private int temporizadorDisparo = 0;
    private static final double VELOCIDAD = 0.5;
    private static final int VIDA_INICIAL = 4;
    private static final double DIAMETRO_ZOMBIE = 45;
    private static final double DIAMETRO_AURA = 50;
    private Image imagen;
    private boolean estaComiendo = false;

    // --- ATRIBUTOS ---
    /**
     * MEJORA CRÍTICA: Se encapsularon las variables como 'private'.
     * Esto previene que la clase 'Juego' modifique la 'vida' o 'x'
     * del zombi por accidente. El zombi es el ÚNICO responsable
     * de modificar sus propios atributos.
     */
    private double x; // Coordenada X (centro)
    private double y; // Coordenada Y (centro)
    private int vida; // Salud del zombi

    /**
     * Constructor del Zombi.
     * @param x Posición inicial en x (centro)
     * @param y Posición inicial en y (centro)
     */
    public ZombieGrinch(double x, double y) {
        this.x = x;
        this.y = y;
        this.vida = VIDA_INICIAL; // Usa la constante
        try {
            this.imagen = Herramientas.cargarImagen("recursos/ZOMBIEGRINCH.png");
        } catch (Exception e) {
            System.err.println("No se encontró la imagen del zombie.");
            this.imagen = null;
        }
    }
    
    
    // --- MÉTODOS PÚBLICOS ---

    /**
     * Dibuja el zombi (círculo verde) y un "aura" o "capa"
     * que indica su vida restante (basado en 4 de vida total).
     * @param entorno El contexto gráfico donde se dibujará.
     */
    public void dibujarse(Entorno entorno) {
        
    	
        
        // 2. DIBUJAR EL ZOMBIE (Imagen)
        if (this.imagen != null) {
            // Ajustá la escala (0.2) según el tamaño de tu foto
            entorno.dibujarImagen(this.imagen, this.x, this.y, 0, 0.2); 
        } else {
            // Si falla la imagen, dibujamos el viejo círculo verde
            entorno.dibujarCirculo(this.x, this.y, 45, Color.GREEN);
        }

        // 3. DIBUJAR LA BOLA DE NIEVE (Si disparó)
        if (this.bola != null) {
            this.bola.dibujarse(entorno);
        }
    }

    /**
     * Mueve el zombi un paso hacia la izquierda,
     * basado en su constante de VELOCIDAD.
     */
    public void moverse() {
       // this.x -= VELOCIDAD;
        this.temporizadorDisparo++;
        
        // Si pasaron 300 frames (aprox 4 seg) y no hay bola, DISPARA
        if (this.temporizadorDisparo > 300 && this.bola == null) {
            // Crea la bola un poco a la izquierda del zombie
            this.bola = new BolaDeNieve(this.x - 20, this.y);
            this.temporizadorDisparo = 0; // Reinicia el contador
        }

        // Si la bola existe, moverla
        if (this.bola != null) {
            this.bola.mover();
            
            // Si se sale de la pantalla por la izquierda, borrarla
            if (this.bola.getX() < 0) {
                this.bola = null;
            }
        }
        if (!this.estaComiendo) {
            this.x -= VELOCIDAD;
        }
    }

    /**
     * Reduce la vida del zombi en 1.
     * Llamado por 'Juego' durante una colisión.
     */
    public void recibirDisparo() {
        this.vida -= 1;
    }
    
    // --- GETTERS ---
    /**
     * MEJORA: Se añaden 'Getters' públicos.
     * Ya que los atributos son 'private', la clase Juego necesita
     * una forma de LEER (pero no escribir) la posición y la vida
     * para el chequeo de colisiones y condiciones de victoria.
     */
    
    /**
     * @return La coordenada X actual del centro del zombi.
     */
    public double getX() {
        return this.x;
    }
    
    /**
     * @return La coordenada Y actual del centro del zombi.
     */
    public double getY() {
        return this.y;
    }
    
    /**
     * @return La vida restante actual del zombi.
     */
    public int getVida() {
        return this.vida;
    }
    public BolaDeNieve getBola() {
        return this.bola;
    }

    public void borrarBola() {
        this.bola = null;
    }
    public void setComiendo(boolean comiendo) {
        this.estaComiendo = comiendo;
    }
}