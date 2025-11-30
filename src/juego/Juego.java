package juego;
import javax.sound.sampled.*;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import entorno.Entorno;
import entorno.InterfaceJuego;
import java.awt.Color;
import entorno.Herramientas;
import java.awt.Image;

/**
 * Clase principal del juego "La invasión de los Zombies Grinch".
 * Gestiona el bucle principal (tick), todos los objetos del juego
 * (plantas, zombies, tablero, etc.) y los estados del jugador
 * (plantando, moviendo, jugando).
 */
public class Juego extends InterfaceJuego {

    // --- CONSTANTES DEL JUEGO ---
    /**
     * MEJORA: Se re-agregó la palabra 'final'.
     * La convención en Java para constantes es 'static final'
     * para indicar que son valores fijos que no pueden cambiar.
     */
	private Image fondo; //variable de foto
    private static final int FILAS = 5;
    private static final int COLUMNAS = 9;
    private static final int COOLDOWN_DURACION = 100; // Duración del cooldown en ticks
    private Clip musicaFondo;
    // --- VARIABLES DE INSTANCIA ---
    private Entorno entorno;
    private Casilla[] tablero; // Arreglo que simula la matriz del tablero
    private RoseBlade[] rosas;
    private ZombieGrinch[] zombies;
    private Regalo[] regalos;
    private CartaPlanta[] cartas;
    private WallNut[] nueces; // El arreglo de nueces
    private WallNut nuezSeleccionada; 
    private WallNut nuezParaPlantar;
    
    // Contadores para la condición de victoria
    private int cant_zombies; // Objetivo total de zombies a eliminar
    private int cont_zombies; // Contador de zombies eliminados
    
    // --- ESTADOS DEL JUGADOR Y JUEGO ---
    private RoseBlade plantaParaPlantar; // Planta "fantasma" sostenida por el cursor
    private RoseBlade plantaSeleccionada; // Planta en el tablero seleccionada para mover con WASD
    private int proximoTickParaPlantar; // Temporizador para el cooldown de las cartas
    
    private boolean juegoTerminado; // Se vuelve 'true' al ganar o perder
    private int tiempoFinal; // Almacena el tiempo al finalizar la partida

    /**
     * Constructor del Juego. Se ejecuta una sola vez al inicio.
     * Prepara todas las variables y el estado inicial del juego.
     */
    Juego() {
        // 1. Inicializa el entorno
    	this.fondo = Herramientas.cargarImagen("recursos/fotoPatio.png.png");
    	try {
            iniciarMusica();
        } catch (Exception e) {}

    	
        this.entorno = new Entorno(this, "La invasión de los Zombies Grinch", 800, 600); 
        
        // 2. Inicializa los arrays (contenedores)
        this.tablero = new Casilla[FILAS * COLUMNAS];
        this.rosas = new RoseBlade[10];
        this.zombies = new ZombieGrinch[7];
        this.regalos = new Regalo[FILAS];
        this.cartas = new CartaPlanta[3];
        this.nueces = new WallNut[10];
        // 3. Crea las cartas de la UI
        this.cartas[0] = new CartaPlanta(20, 20, 90, 70, 0); 
        this.cartas[1] = new CartaPlanta(120, 20, 90, 70, 1);
        
        // 4. Calcula medidas y crea el tablero
        // MEJORA: Estas variables ahora son 'locales' al constructor.
        // No necesitan ser variables de instancia, lo que limpia la clase.
        int anchoCasilla = this.entorno.ancho() / COLUMNAS;
        int altoCasilla = anchoCasilla;
        int altoTotalTablero = FILAS * altoCasilla;
        int inicioX = 0;
        int inicioY = this.entorno.alto() - altoTotalTablero;
        
        // Llenamos el arreglo del tablero
        for (int f = 0; f < FILAS; f++) {
            for (int c = 0; c < COLUMNAS; c++) {
                double x = inicioX + c * anchoCasilla + anchoCasilla / 2;
                double y = inicioY + f * altoCasilla + altoCasilla / 2;
                boolean plantable = (c >= 1); // Columna 0 (regalos) no es plantable
                
                // Lógica de color para el patrón de ajedrez (tu if-else)
                Color color;
                if ((f % 2 == 0 && c % 2 == 0) || (f % 2 != 0 && c % 2 != 0)) {
                    color = new Color(0, 102, 0, 115); // Verde claro
                } else {
                    color = new Color(0, 50, 0, 115); // Verde oscuro
                }
                
                int indice = (f * COLUMNAS) + c;
                this.tablero[indice] = new Casilla(x, y, anchoCasilla, altoCasilla, plantable, color);
            }
        }

        // 5. Crea los regalos en la primera columna
        for (int f = 0; f < FILAS; f++) {
            int indiceRegalo = (f * COLUMNAS) + 0; // Columna 0, fila 'f'
            this.regalos[f] = new Regalo(this.tablero[indiceRegalo].getCentroX(), this.tablero[indiceRegalo].getCentroY());
        }
        
        // 6. Inicializa los estados y contadores
        this.plantaParaPlantar = null;
        this.proximoTickParaPlantar = 0;
        this.plantaSeleccionada = null;
        this.cont_zombies = 0;
        this.cant_zombies = 50; // Objetivo para ganar
        this.juegoTerminado = false;
        this.tiempoFinal = 0;

        // 7. Inicia el juego
        this.entorno.iniciar();
    }

    /**
     * Método tick(). Es el corazón del juego, se ejecuta en cada fotograma.
     * Separa la lógica de Dibujado (que siempre ocurre) de la lógica de
     * Actualización (que se "congela" si el juego ha terminado).
     */
    @Override
    
    public void tick() {
    	entorno.dibujarImagen(fondo, 400, 310, 0, 0.35);
        // --- 1. DIBUJADO (Siempre se ejecuta) ---
        // Dibuja el estado actual del juego, incluso si está "congelado".
        this.dibujarTablero();
        this.dibujarCartas();
        this.dibujarObjetos(this.regalos);
        this.dibujarObjetos(this.rosas);
        this.dibujarZombies();
        this.dibujarDisparos();
        this.dibujarInformacionUI();

        // --- 2. LÓGICA DE JUEGO (Solo si el juego NO ha terminado) ---
        if (!this.juegoTerminado) {
            
            // Actualizamos (movemos) los objetos dinámicos
            this.actualizarPlantas();
            this.actualizarZombies();
            this.actualizarDisparos();

            // Ejecutamos la lógica de spawns, colisiones y disparos
            this.ejecutarLogicaJuego();

            // Manejamos el input del jugador (clics y WASD)
            this.manejarEstadoJugador();
        
        } else {
            // --- 3. JUEGO TERMINADO ---
            // Si el juego terminó, dibujamos el mensaje de derrota o victoria
            this.dibujarMensajeFinDeJuego();
        }
        for (WallNut w : this.nueces) {
            if (w != null) {
                boolean esSel = (w == this.nuezSeleccionada);
                w.dibujarse(this.entorno, esSel);
            }
        }
        for (WallNut w : this.nueces) {
            if (w != null) w.actualizarMovimiento();
        }
    }

    // --- MÉTODOS DE AYUDA (Dividen la lógica de tick()) ---
    
    // --- MÉTODOS DE DIBUJADO Y ACTUALIZACIÓN ---

    /**
     * Dibuja todas las casillas del tablero iterando el arreglo.
     */
    private void dibujarTablero() {
        for (int i = 0; i < this.tablero.length; i++) {
            this.tablero[i].dibujarse(this.entorno);
        }
    }
    private void iniciarMusica() throws Exception {
        AudioInputStream audio = AudioSystem.getAudioInputStream(
            getClass().getResource("/recursos/026491_pixel-song-8-72675.wav")
        );

        musicaFondo = AudioSystem.getClip();
        musicaFondo.open(audio);
        musicaFondo.loop(Clip.LOOP_CONTINUOUSLY);
        musicaFondo.start();
    }

   
    /**
     * Dibuja las cartas de la UI, mostrando el estado de cooldown.
     */
    private void dibujarCartas() {
        for (int i = 0; i < this.cartas.length; i++) {
            if (this.cartas[i] != null) {
                // Calcula el progreso del cooldown (0.0 a 1.0)
                double progreso = 1.0; 
                if (this.entorno.numeroDeTick() < this.proximoTickParaPlantar) {
                    double tickActual = this.entorno.numeroDeTick();
                    double tickFin = this.proximoTickParaPlantar;
                    double tickInicio = tickFin - COOLDOWN_DURACION;
                    double tiempoTranscurrido = tickActual - tickInicio;
                    progreso = tiempoTranscurrido / COOLDOWN_DURACION;
                }
                this.cartas[i].dibujarse(this.entorno, progreso);
            }
        }
    }

    
    /**
     * Dibuja la información de texto (puntaje, tiempo) en la pantalla.
     * Muestra el tiempo "congelado" si el juego ha terminado.
     */
    private void dibujarInformacionUI() {
        this.entorno.cambiarFont("Arial", 18, Color.WHITE);
        
        String textoEliminados = "Eliminados: " + this.cont_zombies + " / " + this.cant_zombies;
        this.entorno.escribirTexto(textoEliminados, 600, 50);
        
        int tiempoAMostrar;
        if (this.juegoTerminado) {
            tiempoAMostrar = this.tiempoFinal; // Muestra el tiempo congelado
        } else {
            tiempoAMostrar = this.entorno.numeroDeTick() / 60; // Muestra el tiempo en vivo
        }
        
        this.entorno.escribirTexto("Tiempo: " + tiempoAMostrar, 600, 80);
    }

    /**
     * Método genérico para dibujar los regalos.
     * @param arrayDeRegalos El arreglo de objetos Regalo a dibujar.
     */
    private void dibujarObjetos(Regalo[] arrayDeRegalos) {
        for (int i = 0; i < arrayDeRegalos.length; i++) {
            if (arrayDeRegalos[i] != null) {
                arrayDeRegalos[i].dibujarse(this.entorno);
            }
        }
    }

    /**
     * Método genérico para dibujar las plantas.
     * Pasa el estado de selección a cada planta para el efecto de borde.
     * @param arrayDeRosas El arreglo de objetos RoseBlade a dibujar.
     */
    private void dibujarObjetos(RoseBlade[] arrayDeRosas) {
        for (int i = 0; i < arrayDeRosas.length; i++) {
            if (arrayDeRosas[i] != null) {
                boolean esLaSeleccionada = (arrayDeRosas[i] == this.plantaSeleccionada);
                arrayDeRosas[i].dibujarse(this.entorno, esLaSeleccionada);
            }
        }
    }

    /**
     * Dibuja todos los zombis activos (sin moverlos).
     */
    private void dibujarZombies() {
        for (int z = 0; z < this.zombies.length; z++) {
            if (this.zombies[z] != null) {
                this.zombies[z].dibujarse(this.entorno);
            }
        }
    }
    
    /**
     * Dibuja todos los disparos activos (sin moverlos).
     */
    private void dibujarDisparos() {
        for (int i = 0; i < this.rosas.length; i++) {
            // MEJORA: Usar getBola() para respetar la encapsulación.
            if (this.rosas[i] != null && this.rosas[i].getBola() != null) {
                this.rosas[i].getBola().dibujarse(this.entorno);
            }
        }
    }

    /**
     * Actualiza la posición de todos los zombis activos.
     */
    private void actualizarZombies() {
        for (int z = 0; z < this.zombies.length; z++) {
            if (this.zombies[z] != null) {
                this.zombies[z].moverse();
            }
        }
    }
    
    /**
     * Actualiza la posición de todas las plantas (para la animación de movimiento WASD).
     */
    private void actualizarPlantas() {
        for (int i = 0; i < this.rosas.length; i++) {
            if (this.rosas[i] != null) {
                this.rosas[i].actualizarMovimiento();
            }
        }
    }

    /**
     * Actualiza la posición de todos los disparos activos
     * y los elimina si salen de la pantalla.
     */
    private void actualizarDisparos() {
        for (int i = 0; i < this.rosas.length; i++) {
            // MEJORA: Usar getBola() y setBola() para respetar la encapsulación.
            if (this.rosas[i] != null && this.rosas[i].getBola() != null) {
                this.rosas[i].getBola().moverse();

                if (this.rosas[i].getBola().getX() > this.entorno.ancho()) {
                    this.rosas[i].setBola(null);
                }
            }
        }
    }
    
    // --- MÉTODOS DE LÓGICA DE JUEGO ---

    /**
     * Ejecuta las reglas principales del juego: spawns, disparos y colisiones.
     * Se llama en cada tick mientras el juego no haya terminado.
     */
    private void ejecutarLogicaJuego() {
        // Genera zombis periódicamente
        if (this.entorno.numeroDeTick() % 100 == 0) { 
            spawnZombie();
        }
        
        // Disparos automáticos de las plantas (si no están seleccionadas)
        if (this.entorno.numeroDeTick() % 150 == 0) {
            for (int i = 0; i < this.rosas.length; i++) {
                if (this.rosas[i] != null && this.rosas[i] != this.plantaSeleccionada) {
                    this.rosas[i].disparar();
                }
            }
        }
        chequearMordiscosDeZombies();
        chequearColisionesDisparosZombies();
        chequearColisionesDisparos();
        chequearCondicionDerrota();
        chequearCondicionVictoria();
        chequearColisionesPlantas();
    }

    /**
     * Genera un nuevo zombi en una fila aleatoria a la derecha de la pantalla.
     */
    private void spawnZombie() {
        for (int z = 0; z < this.zombies.length; z++) { 
            if (this.zombies[z] == null) { 
                int f = (int)(Math.random() * FILAS); // Fila aleatoria
                int c = COLUMNAS - 1; // Última columna
                int indiceCasilla = (f * COLUMNAS) + c;
                
                Casilla casillaSpawn = this.tablero[indiceCasilla];
                
                // GENERAR TIPO ALEATORIO (1, 2 o 3)
                // Math.random() da entre 0.0 y 0.999...
                // * 3 da entre 0.0 y 2.999...
                // (int) lo corta a 0, 1 o 2.
                // + 1 lo transforma en 1, 2 o 3.
                int tipoAleatorio = (int)(Math.random() * 3) + 1;
                
                // Creamos el zombie pasando el tipo nuevo
                this.zombies[z] = new ZombieGrinch(this.entorno.ancho() + 50, casillaSpawn.getCentroY(), tipoAleatorio);
                break; // Solo genera un zombi a la vez
            } 
        } 
    }
    private void chequearMordiscosDeZombies() {
        for (ZombieGrinch z : this.zombies) {
            if (z != null) {
                boolean estaTocandoAlgunaNuez = false;

                for (int i = 0; i < this.nueces.length; i++) {
                    if (this.nueces[i] != null) {
                        // Si colisionan CUERPO A CUERPO (distancia corta)
                        if (hayColision(z.getX(), z.getY(), this.nueces[i].getX(), this.nueces[i].getY())) {
                            
                            // 1. El zombie se frena
                            estaTocandoAlgunaNuez = true;
                            z.setComiendo(true);
                            
                            // 2. La nuez recibe daño (poquito pero constante, porque es por tick)
                            // Si le ponés mucho daño acá, la nuez desaparece en 1 segundo.
                            this.nueces[i].recibirDanio(1); 
                            
                            // 3. Si la nuez muere
                            if (this.nueces[i].estaMuerta()) {
                                this.nueces[i] = null;
                                z.setComiendo(false); // El zombie vuelve a caminar
                            }
                        }
                    }
                }
                
                // Si el zombie no está tocando ninguna nuez (ej: ya se la comió), camina.
                if (!estaTocandoAlgunaNuez) {
                    z.setComiendo(false);
                }
            }
        }
    }

    /**
     * Revisa colisiones entre BolasDeFuego y Zombies.
     * Asigna daño y actualiza el contador de victoria.
     */
    private void chequearColisionesDisparos() {
        for (int i = 0; i < this.rosas.length; i++) {
            for (int z = 0; z < this.zombies.length; z++) {
                // MEJORA: Usar getters para respetar la encapsulación.
                if (this.rosas[i] != null && this.rosas[i].getBola() != null && this.zombies[z] != null) {
                    if (hayColision(this.rosas[i].getBola().getX(), this.rosas[i].getBola().getY(), this.zombies[z].getX(), this.zombies[z].getY())) {
                        this.rosas[i].setBola(null); // El disparo desaparece
                        this.zombies[z].recibirDisparo();
                        if (this.zombies[z].getVida() == 0) { // Usa getter
                            this.zombies[z] = null; // El zombi muere
                            this.cont_zombies++; // Sumamos 1 al contador
                        }
                    }
                }
            }
        }
    }
    private void chequearColisionesDisparosZombies() {
        for (int z = 0; z < this.zombies.length; z++) {
            if (this.zombies[z] != null && this.zombies[z].getBola() != null) {
                
                // 1. REVISAR ROSAS (Esto ya lo tenías)
                for (int i = 0; i < this.rosas.length; i++) {
                    if (this.rosas[i] != null) {
                        if (hayColision(this.zombies[z].getBola().getX(), this.zombies[z].getBola().getY(), this.rosas[i].getX(), this.rosas[i].getY())) {
                            this.zombies[z].borrarBola();
                            this.rosas[i].recibirDanio(25);
                            if (this.rosas[i].estaMuerta()) {
                                if (this.plantaSeleccionada == this.rosas[i]) this.plantaSeleccionada = null;
                                this.rosas[i] = null;
                            }
                            break; // Romper loop si golpea rosa
                        }
                    }
                }

                // 2. REVISAR NUECES (¡ESTO FALTABA!)
                // Si la bola sigue existiendo (no chocó contra rosa), chequeamos nueces
                if (this.zombies[z].getBola() != null) {
                    for (int k = 0; k < this.nueces.length; k++) {
                        if (this.nueces[k] != null) {
                            if (hayColision(this.zombies[z].getBola().getX(), this.zombies[z].getBola().getY(), this.nueces[k].getX(), this.nueces[k].getY())) {
                                this.zombies[z].borrarBola();
                                this.nueces[k].recibirDanio(25);
                                if (this.nueces[k].estaMuerta()) {
                                    if (this.nuezSeleccionada == this.nueces[k]) this.nuezSeleccionada = null;
                                    this.nueces[k] = null;
                                }
                                break; // Romper loop si golpea nuez
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Revisa colisiones entre Zombies y Regalos (Condición de derrota).
     * Si ocurre, activa el estado 'juegoTerminado'.
     */
    private void chequearCondicionDerrota() {
        for (int z = 0; z < this.zombies.length; z++) {
            for (int i = 0; i < this.regalos.length; i++) {
                // MEJORA: Usar getters para respetar la encapsulación.
                if (this.zombies[z] != null && this.regalos[i] != null) {
                    if (hayColision(this.zombies[z].getX(), this.zombies[z].getY(), this.regalos[i].getX(), this.regalos[i].getY())) {
                        // Solo se ejecuta la primera vez que ocurre la colisión
                        if (!this.juegoTerminado) {
                            System.out.println("¡Juego Terminado! Un zombi ha robado un regalo.");
                            this.juegoTerminado = true;
                            this.tiempoFinal = this.entorno.numeroDeTick() / 60; // Guarda el tiempo
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Revisa si el jugador ha ganado (Condición de victoria).
     * Si ocurre, activa el estado 'juegoTerminado'.
     */
    private void chequearCondicionVictoria() {
        if (this.cont_zombies >= this.cant_zombies) {
            // Solo se ejecuta la primera vez que se cumple
            if (!this.juegoTerminado) {
                System.out.println("¡FELICIDADES! Has ganado el juego.");
                this.juegoTerminado = true;
                this.tiempoFinal = this.entorno.numeroDeTick() / 60; // Guarda el tiempo
            }
        }
    }
    
    /**
     * Dibuja el mensaje de "¡Has Perdido!" o "¡Has Ganado!"
     * centrado en la pantalla cuando el juego termina.
     */
    private void dibujarMensajeFinDeJuego() {
        // MEJORA: Se calcula el centro para centrar el texto.
        int centroAncho = this.entorno.ancho() / 2;
        int centroAlto = this.entorno.alto() / 2;
        
        if (this.cont_zombies >= this.cant_zombies) {
            this.entorno.cambiarFont("Arial", 50, Color.YELLOW);
            this.entorno.escribirTexto("¡Has Ganado!", centroAncho - 150, centroAlto);
        } else {
            this.entorno.cambiarFont("Arial", 50, Color.RED);
            this.entorno.escribirTexto("¡Has Perdido!", centroAncho - 150, centroAlto);
        }
    }
    
    /**
     * Revisa colisiones entre Zombies y Plantas (Muerte de planta).
     */
    private void chequearColisionesPlantas() {
        for (int z = 0; z < this.zombies.length; z++) {
            for (int i = 0; i < this.rosas.length; i++) {
                // MEJORA: Usar getters
                if (this.zombies[z] != null && this.rosas[i] != null) {
                    if (hayColision(this.zombies[z].getX(), this.zombies[z].getY(), this.rosas[i].getX(), this.rosas[i].getY())) {
                        RoseBlade plantaComida = this.rosas[i];
                        this.rosas[i] = null; // La planta desaparece
                        
                        // Si la planta comida era la seleccionada, la deseleccionamos
                        if (this.plantaSeleccionada == plantaComida) {
                            this.plantaSeleccionada = null;
                        }
                    }
                }
            }
        }
    }
    
    // --- MÉTODOS DE ESTADO Y MOVIMIENTO DEL JUGADOR ---

    /**
     * Maneja la lógica principal del jugador (Plantar, Moverse, Seleccionar).
     * Este método actúa como un "router" o "aiguillaje".
     */
    private void manejarEstadoJugador() {
        // Estado 1: Sosteniendo una ROSA
        if (this.plantaParaPlantar != null) {
            manejarEstadoSosteniendoPlanta(); 
        } 
        // Estado 2: Sosteniendo una NUEZ (NUEVO)
        else if (this.nuezParaPlantar != null) {
            manejarEstadoSosteniendoNuez();
        }
        // Estado 3: Jugando normal
        else {
            manejarClicsModoJugando();
        }
        
        // Movimiento WASD (router)
        manejarMovimientoConTeclado1();
    }
    private void manejarMovimientoConTeclado1() {
        // CASO A: MOVIENDO ROSA
        if (this.plantaSeleccionada != null && !this.plantaSeleccionada.estaMoviendose()) {
            moverCualquierPlanta(this.plantaSeleccionada);
        }
        
        // CASO B: MOVIENDO NUEZ
        // Nota: Agregale un método 'estaMoviendose()' a WallNut similar al de RoseBlade para que esto compile bien
        // O saca la condición !estaMoviendose() si no te importa que se mueva raro.
        else if (this.nuezSeleccionada != null) { 
            moverCualquierNuez(this.nuezSeleccionada);
        }
    }
    private void manejarEstadoSosteniendoNuez() {
        // CORRECCIÓN: Usar snapAPosicion para que siga al mouse INSTANTÁNEAMENTE
        this.nuezParaPlantar.snapAPosicion(this.entorno.mouseX(), this.entorno.mouseY());
        this.nuezParaPlantar.dibujarse(this.entorno, false);

        if (this.entorno.sePresionoBoton(entorno.BOTON_IZQUIERDO)) {
            for (int i = 0; i < this.tablero.length; i++) {
                Casilla celda = this.tablero[i];
                if (celda.fueClickeada(this.entorno.mouseX(), this.entorno.mouseY())) {
                    if (celda.esPlantable() && !estaCasillaOcupada(celda.getCentroX(), celda.getCentroY())) {
                        for (int k = 0; k < this.nueces.length; k++) {
                            if (this.nueces[k] == null) {
                                // CORRECCIÓN: Al crearla, usá snap para que nazca centrada
                                this.nueces[k] = new WallNut(celda.getCentroX(), celda.getCentroY());
                                this.nuezParaPlantar = null; 
                                this.proximoTickParaPlantar = this.entorno.numeroDeTick() + COOLDOWN_DURACION;
                                return;
                            }
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Maneja los clics del mouse cuando no estamos sosteniendo una planta.
     * Se encarga de la selección de cartas o la selección/deselección de plantas.
     */
    private void manejarClicsModoJugando() {
        if (this.entorno.sePresionoBoton(entorno.BOTON_IZQUIERDO)) {
            
            // Prioridad 1: Si ya tenemos una planta seleccionada, el clic es para deseleccionar.
            if (this.plantaSeleccionada != null) {
                manejarDeseleccion();
            } 
            // Prioridad 2: Si no, intentamos seleccionar algo.
            else {
                // Primero revisa si el clic fue en una carta...
                if (!intentarAgarrarCarta()) {
                    // ...si no, intenta seleccionar una planta del tablero.
                    intentarSeleccionarPlanta();
                }
            }
        }
    }

    /**
     * Revisa si el clic fue en una carta y, si puede, agarra una planta.
     * @return true si el clic fue en una carta, false si no.
     */
    private boolean intentarAgarrarCarta() {
        for (int i = 0; i < this.cartas.length; i++) {
            if (this.cartas[i] != null && this.cartas[i].fueClickeado(this.entorno.mouseX(), this.entorno.mouseY())) {
                
                // Comprobación 1: ¿El cooldown terminó?
                if (this.entorno.numeroDeTick() >= this.proximoTickParaPlantar) {
                    
                    int idCarta = this.cartas[i].getId(); 

                    // SI ES ROSEBLADE (ID 0)
                    if (idCarta == 0 && hayEspacioEnArray(this.rosas)) {
                        this.plantaParaPlantar = new RoseBlade(this.entorno.mouseX(), this.entorno.mouseY());
                        this.nuezParaPlantar = null; // Aseguramos que la otra mano esté vacía
                        return true;
                    }
                    
                    // SI ES WALLNUT (ID 1)
                    else if (idCarta == 1 && hayEspacioEnArray(this.nueces)) {
                        this.nuezParaPlantar = new WallNut(this.entorno.mouseX(), this.entorno.mouseY());
                        this.plantaParaPlantar = null; // Aseguramos que la otra mano esté vacía
                        return true;
                    }
                }
            }
        }
        return false;
    }
    private boolean hayEspacioEnArray(Object[] array) {
        for (int i = 0; i < array.length; i++) {
            if (array[i] == null) {
                return true; // Encontró un lugar vacío
            }
        }
        return false; // El array está lleno
    }

    /**
     * Revisa si el clic fue sobre una planta plantada y la selecciona.
     */
    private boolean intentarSeleccionarPlanta() {
        // 1. Probar con ROSAS
        for (int i = 0; i < this.rosas.length; i++) {
            if (this.rosas[i] != null && hayColision(this.rosas[i].getX(), this.rosas[i].getY(), this.entorno.mouseX(), this.entorno.mouseY())) {
                this.plantaSeleccionada = this.rosas[i];
                this.nuezSeleccionada = null; // Soltamos la nuez si teníamos una
                return true; 
            }
        }
        
        // 2. Probar con NUECES
        for (int i = 0; i < this.nueces.length; i++) {
            if (this.nueces[i] != null && hayColision(this.nueces[i].getX(), this.nueces[i].getY(), this.entorno.mouseX(), this.entorno.mouseY())) {
                this.nuezSeleccionada = this.nueces[i];
                this.plantaSeleccionada = null; // Soltamos la rosa si teníamos una
                return true; // ¡Encontramos una!
            }
        }
        
        return false; // No clickeamos ninguna planta
    }
    
    
    /**
     * Lógica para deseleccionar una planta movible.
     * Un clic en sí misma o en cualquier otro lugar la deselecciona.
     */
    private void manejarDeseleccion() {
        this.plantaSeleccionada = null;
        this.nuezSeleccionada = null;
    }

    /**
     * Lógica para cuando el jugador está "SOSTENIENDO" una planta.
     * Se encarga de seguir el mouse y plantar en una casilla válida.
     */
    private void manejarEstadoSosteniendoPlanta() {
        // La planta "fantasma" sigue al mouse
        // MEJORA: Usar el método 'snapAPosicion' de la planta
        this.plantaParaPlantar.snapAPosicion(this.entorno.mouseX(), this.entorno.mouseY());
        this.plantaParaPlantar.dibujarse(this.entorno, false); // 'false' para no dibujarle borde
        
        if (this.entorno.sePresionoBoton(entorno.BOTON_IZQUIERDO)) {
            // Recorremos el tablero buscando qué casilla fue clickeada
            for (int i = 0; i < this.tablero.length; i++) {
                Casilla celdaClickeada = this.tablero[i];
                
                if (celdaClickeada.fueClickeada(this.entorno.mouseX(), this.entorno.mouseY())) {
                    
                    // 1. ¿Es plantable? Y 2. ¿No está ocupada?
                    if (celdaClickeada.esPlantable() && !estaCasillaOcupada(celdaClickeada.getCentroX(), celdaClickeada.getCentroY())) {
                        
                        // ¡Casilla válida! Buscamos espacio en el array 'rosas'
                        for (int k = 0; k < this.rosas.length; k++) {
                            if (this.rosas[k] == null) {
                                this.rosas[k] = this.plantaParaPlantar;
                                
                                // "Pega" la planta a la casilla y resetea su 'target'
                                this.rosas[k].snapAPosicion(celdaClickeada.getCentroX(), celdaClickeada.getCentroY());
                                
                                this.plantaParaPlantar = null; // Suelta la planta
                                this.proximoTickParaPlantar = this.entorno.numeroDeTick() + COOLDOWN_DURACION;
                                break; 
                            }
                        }
                    }
                    break; // Deja de buscar casillas
                }
            }
        }
    }
    
    /**
     * Se encarga de FIJAR EL OBJETIVO de la planta seleccionada
     * cuando se presiona WASD, solo si la planta no se está moviendo.
     */
    private void manejarMovimientoConTeclado() {
        // 1. Si hay una ROSA seleccionada y quieta, la movemos
        if (this.plantaSeleccionada != null && !this.plantaSeleccionada.estaMoviendose()) {
            moverCualquierPlanta(this.plantaSeleccionada);
        }
        
        // 2. Si hay una NUEZ seleccionada (aunque no tenga animación de moverse, usamos la lógica igual)
        else if (this.nuezSeleccionada != null) {
            moverCualquierNuez(this.nuezSeleccionada);
        }
    }
    
    private void moverCualquierNuez(WallNut n) {
        double saltoHorizontal = this.tablero[0].getAncho(); 
        double saltoVertical = this.tablero[0].getAlto(); 

        // --- Arriba (W) ---
        if (this.entorno.sePresiono('w') || this.entorno.sePresiono(this.entorno.TECLA_ARRIBA)) {
            // CORRECCIÓN: Usamos getTargetY() en vez de getY()
            double nuevaY = n.getTargetY() - saltoVertical;
            double limiteSuperior = this.tablero[0].getCentroY();
            
            // Validamos contra nuevaY (destino) y getTargetX (destino actual)
            if (nuevaY >= limiteSuperior && !estaCasillaOcupada(n.getTargetX(), nuevaY)) {
                n.setTarget(n.getTargetX(), nuevaY);
            }
        }
        
        // --- Abajo (S) ---
        if (this.entorno.sePresiono('s') || this.entorno.sePresiono(this.entorno.TECLA_ABAJO)) {
            double nuevaY = n.getTargetY() + saltoVertical;
            int indiceLimite = ((FILAS - 1) * COLUMNAS);
            double limiteInferior = this.tablero[indiceLimite].getCentroY();
            
            if (nuevaY <= limiteInferior && !estaCasillaOcupada(n.getTargetX(), nuevaY)) {
                n.setTarget(n.getTargetX(), nuevaY);
            }
        }

        // --- Izquierda (A) ---
        if (this.entorno.sePresiono('a') || this.entorno.sePresiono(this.entorno.TECLA_IZQUIERDA)) {
            // CORRECCIÓN: Usamos getTargetX() en vez de getX()
            double nuevaX = n.getTargetX() - saltoHorizontal;
            int indiceLimite = (0 * COLUMNAS) + 1; 
            double limiteIzquierdo = this.tablero[indiceLimite].getCentroX();
            
            if (nuevaX >= limiteIzquierdo && !estaCasillaOcupada(nuevaX, n.getTargetY())) {
                n.setTarget(nuevaX, n.getTargetY());
            }
        }

        // --- Derecha (D) ---
        if (this.entorno.sePresiono('d') || this.entorno.sePresiono(this.entorno.TECLA_DERECHA)) {
            double nuevaX = n.getTargetX() + saltoHorizontal;
            int indiceLimite = (0 * COLUMNAS) + (COLUMNAS - 1);
            double limiteDerecho = this.tablero[indiceLimite].getCentroX();
            
            if (nuevaX <= limiteDerecho && !estaCasillaOcupada(nuevaX, n.getTargetY())) {
                n.setTarget(nuevaX, n.getTargetY());
            }
        }
    }
    private void moverCualquierPlanta(RoseBlade p) {
        double saltoHorizontal = this.tablero[0].getAncho(); 
        double saltoVertical = this.tablero[0].getAlto(); 

        // --- Arriba (W) ---
        if (this.entorno.sePresiono('w') || this.entorno.sePresiono(this.entorno.TECLA_ARRIBA)) {
            double nuevaY = p.getY() - saltoVertical;
            double limiteSuperior = this.tablero[0].getCentroY();
            
            if (nuevaY >= limiteSuperior && !estaCasillaOcupada(p.getX(), nuevaY)) {
                p.setTarget(p.getX(), nuevaY);
            }
        }
        
        // --- Abajo (S) ---
        if (this.entorno.sePresiono('s') || this.entorno.sePresiono(this.entorno.TECLA_ABAJO)) {
            double nuevaY = p.getY() + saltoVertical;
            int indiceLimite = ((FILAS - 1) * COLUMNAS); // Última fila
            double limiteInferior = this.tablero[indiceLimite].getCentroY();
            
            if (nuevaY <= limiteInferior && !estaCasillaOcupada(p.getX(), nuevaY)) {
                p.setTarget(p.getX(), nuevaY);
            }
        }

        // --- Izquierda (A) ---
        if (this.entorno.sePresiono('a') || this.entorno.sePresiono(this.entorno.TECLA_IZQUIERDA)) {
            double nuevaX = p.getX() - saltoHorizontal;
            int indiceLimite = (0 * COLUMNAS) + 1; // Columna 1 (límite izquierdo jugable)
            double limiteIzquierdo = this.tablero[indiceLimite].getCentroX();
            
            if (nuevaX >= limiteIzquierdo && !estaCasillaOcupada(nuevaX, p.getY())) {
                p.setTarget(nuevaX, p.getY());
            }
        }

        // --- Derecha (D) ---
        if (this.entorno.sePresiono('d') || this.entorno.sePresiono(this.entorno.TECLA_DERECHA)) {
            double nuevaX = p.getX() + saltoHorizontal;
            int indiceLimite = (0 * COLUMNAS) + (COLUMNAS - 1); // Última columna
            double limiteDerecho = this.tablero[indiceLimite].getCentroX();
            
            if (nuevaX <= limiteDerecho && !estaCasillaOcupada(nuevaX, p.getY())) {
                p.setTarget(nuevaX, p.getY());
            }
        }
    }
    
    // --- MÉTODOS DE UTILIDAD ---
    
    /**
     * Comprueba si hay al menos un espacio vacío (null) en el array 'rosas'.
     * @return true si hay espacio para plantar, false si el array está lleno.
     */
    private boolean hayEspacioParaPlantar() {
        for (int i = 0; i < this.rosas.length; i++) {
            if (this.rosas[i] == null) {
                return true;
            }
        }
        return false;
    }

    /**
     * Comprueba si una casilla específica ya está ocupada por otra planta.
     * @param x La coordenada X del centro de la casilla a comprobar.
     * @param y La coordenada Y del centro de la casilla a comprobar.
     * @return true si la casilla está ocupada, false en caso contrario.
     */
    private boolean estaCasillaOcupada(double x, double y) {
        // 1. Chequear ROSAS
        for (int i = 0; i < this.rosas.length; i++) {
            if (this.rosas[i] != null && this.rosas[i] != this.plantaSeleccionada) {
                if (Math.abs(this.rosas[i].getX() - x) < 10 && Math.abs(this.rosas[i].getY() - y) < 10) {
                    return true;
                }
            }
        }
        for (int k = 0; k < this.nueces.length; k++) {
            if (this.nueces[k] != null && this.nueces[k] != this.nuezSeleccionada) {
                if (Math.abs(this.nueces[k].getX() - x) < 10 && Math.abs(this.nueces[k].getY() - y) < 10) {
                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * Método de ayuda para calcular la distancia entre dos puntos (colisión).
     * @param x1 Coordenada X del primer objeto
     * @param y1 Coordenada Y del primer objeto
     * @param x2 Coordenada X del segundo objeto
     * @param y2 Coordenada Y del segundo objeto
     * @return true si la distancia es menor al umbral, false en caso contrario.
     */
    private boolean hayColision(double x1, double y1, double x2, double y2) {
        double diffX = x1 - x2;
        double diffY = y1 - y2;
        double distancia = Math.sqrt(diffX * diffX + diffY * diffY);
        // MEJORA: Umbral de colisión aumentado a 30 para ser más generoso
        // con los círculos de 45px de diámetro.
        return distancia < 30; 
    }

    /**
     * Punto de entrada principal para ejecutar el juego.
     * @param args Argumentos de línea de comandos (no utilizados).
     */
    @SuppressWarnings("unused")
    public static void main(String[] args) {
        Juego juego = new Juego();
    }
}