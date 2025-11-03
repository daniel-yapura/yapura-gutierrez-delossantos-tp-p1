package juego;

import entorno.Entorno;
import entorno.InterfaceJuego;
import java.awt.Color;

public class Juego extends InterfaceJuego {

    // --- CONSTANTES ---
    private static final int FILAS = 5;
    private static final int COLUMNAS = 9;
    private static final int COOLDOWN_DURACION = 100; // NUEVO

    // --- VARIABLES ---
    private Entorno entorno;
    private Casilla[] tablero;
    private Regalo[] regalos;
    private CartaPlanta[] cartas; // NUEVO
    private RoseBlade[] rosas; // NUEVO
    private ZombieGrinch[] zombies; // NUEVO
    private int cant_zombies; // NUEVO
    private int cont_zombies; // NUEVO
    
    // --- ESTADOS ---
    private RoseBlade plantaParaPlantar; // NUEVO
    private RoseBlade plantaSeleccionada; // NUEVO
    private int proximoTickParaPlantar; // NUEVO
    private boolean juegoTerminado; // NUEVO
    private int tiempoFinal; // NUEVO

    Juego() {
        this.entorno = new Entorno(this, "La invasión de los Zombies Grinch", 810, 600);
        
        this.tablero = new Casilla[FILAS * COLUMNAS];
        this.regalos = new Regalo[FILAS];
        this.cartas = new CartaPlanta[3]; // NUEVO
        this.rosas = new RoseBlade[10]; // NUEVO (límite de 10)
        this.zombies = new ZombieGrinch[10]; // NUEVO
        
        // --- NUEVO: CREAR CARTAS ---
        this.cartas[0] = new CartaPlanta(20, 20, 90, 70, 0); // 0 = RoseBlade
        
        // ... (Crear Tablero) ...
        int anchoCasilla = this.entorno.ancho() / COLUMNAS;
        int altoCasilla = anchoCasilla;
        int altoTotalTablero = FILAS * altoCasilla;
        int inicioX = 0;
        int inicioY = this.entorno.alto() - altoTotalTablero;
        for (int f = 0; f < FILAS; f++) {
            for (int c = 0; c < COLUMNAS; c++) {
                double x = inicioX + c * anchoCasilla + anchoCasilla / 2;
                double y = inicioY + f * altoCasilla + altoCasilla / 2;
                boolean plantable = (c >= 1);
                Color color;
                if ((f % 2 == 0 && c % 2 == 0) || (f % 2 != 0 && c % 2 != 0)) {
                    color = new Color(0, 102, 0);
                } else {
                    color = new Color(0, 50, 0);
                }
                int indice = (f * COLUMNAS) + c;
                this.tablero[indice] = new Casilla(x, y, anchoCasilla, altoCasilla, plantable, color);
            }
        }
        
        // ... (Crear Regalos) ...
        for (int f = 0; f < FILAS; f++) {
            int indiceRegalo = (f * COLUMNAS) + 0;
            this.regalos[f] = new Regalo(this.tablero[indiceRegalo].getCentroX(), this.tablero[indiceRegalo].getCentroY());
        }
        
        // --- NUEVO: INICIALIZAR ESTADOS ---
        this.proximoTickParaPlantar = 0;
        this.plantaParaPlantar = null; // NUEVO
        this.cont_zombies = 0; // NUEVO
        this.cant_zombies = 50; // NUEVO
        this.juegoTerminado = false; // NUEVO
        this.tiempoFinal = 0; // NUEVO

        this.entorno.iniciar();
    }

    @Override
    public void tick() {
    	// --- DIBUJADO ---
        this.dibujarTablero();
        this.dibujarCartas(); // NUEVO
        this.dibujarObjetos(this.regalos);
        this.dibujarObjetos(this.rosas); // (Ahora usará 'plantaSeleccionada')
        this.dibujarZombies(); // NUEVO
        this.dibujarDisparos(); // NUEVO
        this.dibujarInformacionUI(); // MODIFICADO (ahora se llama siempre)
        
        // --- LÓGICA ---
        if (!this.juegoTerminado) { // NUEVO
        	this.actualizarPlantas(); // NUEVO
            this.actualizarZombies(); // NUEVO
            this.actualizarDisparos(); // NUEVO
            this.ejecutarLogicaJuego(); // (Ahora revisará 'plantaSeleccionada' para disparar)
            this.manejarEstadoJugador(); // (Ahora incluirá lógica WASD)
        } else {
        	// --- JUEGO TERMINADO ---
            this.dibujarMensajeFinDeJuego(); // NUEVO
        }
    }
    
    private void dibujarTablero() {
        for (int i = 0; i < this.tablero.length; i++) {
            this.tablero[i].dibujarse(this.entorno);
        }
    }
    
    /**
     * Dibuja las cartas de la UI.
     */
    private void dibujarCartas() { // NUEVO MÉTODO
        for (int i = 0; i < this.cartas.length; i++) {
            if (this.cartas[i] != null) {
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
    
    private void dibujarObjetos(Regalo[] arrayDeRegalos) {
        for (int i = 0; i < arrayDeRegalos.length; i++) {
            if (arrayDeRegalos[i] != null) {
                arrayDeRegalos[i].dibujarse(this.entorno);
            }
        }
    }
    
    private void dibujarObjetos(RoseBlade[] arrayDeRosas) { // MODIFICADO
        for (int i = 0; i < arrayDeRosas.length; i++) {
            if (arrayDeRosas[i] != null) {
                // (Pasa el estado de selección a la planta)
                boolean esLaSeleccionada = (arrayDeRosas[i] == this.plantaSeleccionada);
                arrayDeRosas[i].dibujarse(this.entorno, esLaSeleccionada);
            }
        }
    }
    
    private void manejarEstadoJugador() { // MODIFICADO
        if (this.plantaParaPlantar != null) {
            manejarEstadoSosteniendoPlanta();
        } else {
            manejarClicsModoJugando();
        }
        // (Añade la llamada a la lógica de movimiento)
        if (this.plantaSeleccionada != null) {
            manejarMovimientoPlanta();
        }
    }
    
    private void manejarClicsModoJugando() { // MODIFICADO
        if (this.entorno.sePresionoBoton(entorno.BOTON_IZQUIERDO)) {
            // (Añade la lógica de prioridad de deselección)
            if (this.plantaSeleccionada != null) {
                manejarDeseleccion();
            } else {
                if (!intentarAgarrarCarta()) {
                    intentarSeleccionarPlanta();
                }
            }
        }
    }

    private boolean intentarAgarrarCarta() {
        for (int i = 0; i < this.cartas.length; i++) {
            if (this.cartas[i] != null && this.cartas[i].fueClickeado(this.entorno.mouseX(), this.entorno.mouseY())) {
                if (this.entorno.numeroDeTick() >= this.proximoTickParaPlantar) {
                    if (this.hayEspacioParaPlantar()) {
                        this.plantaParaPlantar = new RoseBlade(this.entorno.mouseX(), this.entorno.mouseY());
                    }
                }
                return true;
            }
        }
        return false;
    }
    
    private void intentarSeleccionarPlanta() { // NUEVO MÉTODO
        for (int i = 0; i < this.rosas.length; i++) {
            if (this.rosas[i] != null && hayColision(this.rosas[i].getX(), this.rosas[i].getY(), this.entorno.mouseX(), this.entorno.mouseY())) {
                this.plantaSeleccionada = this.rosas[i];
                break; 
            }
        }
    }
    
    private void manejarDeseleccion() { // NUEVO MÉTODO
        if (hayColision(this.plantaSeleccionada.getX(), this.plantaSeleccionada.getY(), this.entorno.mouseX(), this.entorno.mouseY())) {
            this.plantaSeleccionada = null;
        } else {
            this.plantaSeleccionada = null;
        }
    }
    private void manejarEstadoSosteniendoPlanta() {
        this.plantaParaPlantar.snapAPosicion(this.entorno.mouseX(), this.entorno.mouseY());
        this.plantaParaPlantar.dibujarse(this.entorno, false);
        
        if (this.entorno.sePresionoBoton(entorno.BOTON_IZQUIERDO)) {
            for (int i = 0; i < this.tablero.length; i++) {
                Casilla celdaClickeada = this.tablero[i];
                if (celdaClickeada.fueClickeada(this.entorno.mouseX(), this.entorno.mouseY())) {
                    if (celdaClickeada.esPlantable() && !estaCasillaOcupada(celdaClickeada.getCentroX(), celdaClickeada.getCentroY())) {
                        for (int k = 0; k < this.rosas.length; k++) {
                            if (this.rosas[k] == null) {
                                this.rosas[k] = this.plantaParaPlantar;
                                this.rosas[k].snapAPosicion(celdaClickeada.getCentroX(), celdaClickeada.getCentroY());
                                this.plantaParaPlantar = null;
                                this.proximoTickParaPlantar = this.entorno.numeroDeTick() + COOLDOWN_DURACION;
                                break; 
                            }
                        }
                    }
                    break; 
                }
            }
        }
    }
    
    private void dibujarZombies() {
        for (int z = 0; z < this.zombies.length; z++) {
            if (this.zombies[z] != null) { this.zombies[z].dibujarse(this.entorno); }
        }
    }
    
    private void dibujarDisparos() {
        for (int i = 0; i < this.rosas.length; i++) {
            if (this.rosas[i] != null && this.rosas[i].getBola() != null) {
                this.rosas[i].getBola().dibujarse(this.entorno);
            }
        }
    }

    private void actualizarZombies() {
        for (int z = 0; z < this.zombies.length; z++) {
            if (this.zombies[z] != null) { this.zombies[z].moverse(); }
        }
    }

    private void actualizarDisparos() {
        for (int i = 0; i < this.rosas.length; i++) {
            if (this.rosas[i] != null && this.rosas[i].getBola() != null) {
                this.rosas[i].getBola().moverse();
                if (this.rosas[i].getBola().getX() > this.entorno.ancho()) {
                    this.rosas[i].setBola(null);
                }
            }
        }
    }
    
    private void dibujarInformacionUI() { // NUEVO MÉTODO
        this.entorno.cambiarFont("Arial", 18, Color.WHITE);
        String textoEliminados = "Eliminados: " + this.cont_zombies + " / " + this.cant_zombies;
        this.entorno.escribirTexto(textoEliminados, 600, 50);
        
        int tiempoAMostrar;
        if (this.juegoTerminado) {
            tiempoAMostrar = this.tiempoFinal;
        } else {
            tiempoAMostrar = this.entorno.numeroDeTick() / 60;
        }
        this.entorno.escribirTexto("Tiempo: " + tiempoAMostrar, 600, 80);
    }
    
    private void actualizarPlantas() { // NUEVO MÉTODO
        for (int i = 0; i < this.rosas.length; i++) {
            if (this.rosas[i] != null) {
                this.rosas[i].actualizarMovimiento();
            }
        }
    }
    
    private void ejecutarLogicaJuego() { // MODIFICADO
        if (this.entorno.numeroDeTick() % 100 == 0) { spawnZombie(); }
        
        // (Añade la comprobación de 'plantaSeleccionada')
        if (this.entorno.numeroDeTick() % 150 == 0) {
            for (int i = 0; i < this.rosas.length; i++) {
                if (this.rosas[i] != null && this.rosas[i] != this.plantaSeleccionada) {
                    this.rosas[i].disparar();
                }
            }
        }
        chequearColisionesDisparos();
        chequearCondicionDerrota();
        chequearCondicionVictoria(); // NUEVO
        chequearColisionesPlantas();
    }

    private void spawnZombie() {
        for (int z = 0; z < this.zombies.length; z++) { 
            if (this.zombies[z] == null) { 
                int f = (int)(Math.random() * FILAS);
                int c = COLUMNAS - 1;
                int indiceCasilla = (f * COLUMNAS) + c;
                this.zombies[z] = new ZombieGrinch(this.entorno.ancho() + 50, this.tablero[indiceCasilla].getCentroY());
                break;
            } 
        } 
    }

    private void chequearColisionesDisparos() {
        for (int i = 0; i < this.rosas.length; i++) {
            for (int z = 0; z < this.zombies.length; z++) {
                if (this.rosas[i] != null && this.rosas[i].getBola() != null && this.zombies[z] != null) {
                    if (hayColision(this.rosas[i].getBola().getX(), this.rosas[i].getBola().getY(), this.zombies[z].getX(), this.zombies[z].getY())) {
                        this.rosas[i].setBola(null);
                        this.zombies[z].recibirDisparo();
                        if (this.zombies[z].getVida() == 0) {
                            this.zombies[z] = null;
                            this.cont_zombies++;
                        }
                    }
                }
            }
        }
    }

    private void chequearCondicionDerrota() {
        for (int z = 0; z < this.zombies.length; z++) {
            for (int i = 0; i < this.regalos.length; i++) {
                if (this.zombies[z] != null && this.regalos[i] != null) {
                    if (hayColision(this.zombies[z].getX(), this.zombies[z].getY(), this.regalos[i].getX(), this.regalos[i].getY())) {
                        if (!this.juegoTerminado) {
                            System.out.println("¡Juego Terminado!");
                            this.juegoTerminado = true;
                            this.tiempoFinal = this.entorno.numeroDeTick() / 60;
                        }
                    }
                }
            }
        }
    }
    
    private void chequearCondicionVictoria() { // NUEVO MÉTODO
        if (this.cont_zombies >= this.cant_zombies) {
            if (!this.juegoTerminado) {
                System.out.println("¡FELICIDADES! Has ganado el juego.");
                this.juegoTerminado = true;
                this.tiempoFinal = this.entorno.numeroDeTick() / 60;
            }
        }
    }
    
    private void chequearColisionesPlantas() { // MODIFICADO
        for (int z = 0; z < this.zombies.length; z++) {
            for (int i = 0; i < this.rosas.length; i++) {
                if (this.zombies[z] != null && this.rosas[i] != null) {
                    if (hayColision(this.zombies[z].getX(), this.zombies[z].getY(), this.rosas[i].getX(), this.rosas[i].getY())) {
                        RoseBlade plantaComida = this.rosas[i];
                        this.rosas[i] = null;
                        // (Comprueba si la planta comida era la seleccionada)
                        if (this.plantaSeleccionada == plantaComida) {
                            this.plantaSeleccionada = null;
                        }
                    }
                }
            }
        }
    }
    
    private void dibujarMensajeFinDeJuego() { // NUEVO MÉTODO
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
    
    private void manejarMovimientoPlanta() { // NUEVO MÉTODO
        if (!this.plantaSeleccionada.estaMoviendose()) {
            double saltoHorizontal = this.tablero[0].getAncho(); 
            double saltoVertical = this.tablero[0].getAlto(); 

            if (this.entorno.sePresiono('w') || this.entorno.sePresiono(this.entorno.TECLA_ARRIBA)) {
                double nuevaY = this.plantaSeleccionada.getY() - saltoVertical;
                double limiteSuperior = this.tablero[0].getCentroY();
                if (nuevaY >= limiteSuperior && !estaCasillaOcupada(this.plantaSeleccionada.getX(), nuevaY)) {
                    this.plantaSeleccionada.setTarget(this.plantaSeleccionada.getX(), nuevaY);
                }
            }
            if (this.entorno.sePresiono('s') || this.entorno.sePresiono(this.entorno.TECLA_ABAJO)) {
                double nuevaY = this.plantaSeleccionada.getY() + saltoVertical;
                int indiceLimite = ((FILAS - 1) * COLUMNAS);
                double limiteInferior = this.tablero[indiceLimite].getCentroY();
                if (nuevaY <= limiteInferior && !estaCasillaOcupada(this.plantaSeleccionada.getX(), nuevaY)) {
                    this.plantaSeleccionada.setTarget(this.plantaSeleccionada.getX(), nuevaY);
                }
            }
            if (this.entorno.sePresiono('a') || this.entorno.sePresiono(this.entorno.TECLA_IZQUIERDA)) {
                double nuevaX = this.plantaSeleccionada.getX() - saltoHorizontal;
                int indiceLimite = (0 * COLUMNAS) + 1;
                double limiteIzquierdo = this.tablero[indiceLimite].getCentroX();
                if (nuevaX >= limiteIzquierdo && !estaCasillaOcupada(nuevaX, this.plantaSeleccionada.getY())) {
                    this.plantaSeleccionada.setTarget(nuevaX, this.plantaSeleccionada.getY());
                }
            }
            if (this.entorno.sePresiono('d') || this.entorno.sePresiono(this.entorno.TECLA_DERECHA)) {
                double nuevaX = this.plantaSeleccionada.getX() + saltoHorizontal;
                int indiceLimite = (0 * COLUMNAS) + (COLUMNAS - 1);
                double limiteDerecho = this.tablero[indiceLimite].getCentroX();
                if (nuevaX <= limiteDerecho && !estaCasillaOcupada(nuevaX, this.plantaSeleccionada.getY())) {
                    this.plantaSeleccionada.setTarget(nuevaX, this.plantaSeleccionada.getY());
                }
            }
        }
    }
    
    // --- MÉTODOS DE UTILIDAD ---
    private boolean hayEspacioParaPlantar() {
        for (int i = 0; i < this.rosas.length; i++) {
            if (this.rosas[i] == null) { return true; }
        }
        return false;
    }

    private boolean estaCasillaOcupada(double x, double y) { // MODIFICADO
        for (int i = 0; i < this.rosas.length; i++) {
            // (Añade la comprobación de 'plantaSeleccionada')
            if (this.rosas[i] != null && this.rosas[i] != this.plantaSeleccionada) {
                if (Math.abs(this.rosas[i].getX() - x) < 1 && Math.abs(this.rosas[i].getY() - y) < 1) {
                    return true;
                }
            }
        }
        return false;
    }
    
    private boolean hayColision(double x1, double y1, double x2, double y2) {
        double diffX = x1 - x2;
        double diffY = y1 - y2;
        double distancia = Math.sqrt(diffX * diffX + diffY * diffY);
        return distancia < 30; // Umbral de colisión
    }

    @SuppressWarnings("unused")
    public static void main(String[] args) {
        Juego juego = new Juego();
    }
}