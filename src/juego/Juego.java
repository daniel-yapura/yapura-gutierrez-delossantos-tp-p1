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
    
    // --- ESTADOS ---
    private RoseBlade plantaParaPlantar; // NUEVO
    private int proximoTickParaPlantar; // NUEVO

    Juego() {
        this.entorno = new Entorno(this, "La invasión de los Zombies Grinch", 810, 600);
        
        this.tablero = new Casilla[FILAS * COLUMNAS];
        this.regalos = new Regalo[FILAS];
        this.cartas = new CartaPlanta[3]; // NUEVO
        this.rosas = new RoseBlade[10]; // NUEVO (límite de 10)
        
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

        this.entorno.iniciar();
    }

    @Override
    public void tick() {
    	// --- DIBUJADO ---
        this.dibujarTablero();
        this.dibujarCartas(); // NUEVO
        this.dibujarObjetos(this.regalos);
        this.dibujarObjetos(this.rosas); // NUEVO
        
        // --- LÓGICA ---
        this.manejarEstadoJugador(); // NUEVO
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
    
// --- NUEVOS MÉTODOS ---
    
    private void dibujarObjetos(RoseBlade[] arrayDeRosas) {
        for (int i = 0; i < arrayDeRosas.length; i++) {
            if (arrayDeRosas[i] != null) {
                arrayDeRosas[i].dibujarse(this.entorno, false);
            }
        }
    }
    
    private void manejarEstadoJugador() {
        if (this.plantaParaPlantar != null) {
            manejarEstadoSosteniendoPlanta();
        } else {
            manejarClicsModoJugando();
        }
    }
    
    private void manejarClicsModoJugando() {
        if (this.entorno.sePresionoBoton(entorno.BOTON_IZQUIERDO)) {
            intentarAgarrarCarta();
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
    
    private boolean hayEspacioParaPlantar() {
        for (int i = 0; i < this.rosas.length; i++) {
            if (this.rosas[i] == null) { return true; }
        }
        return false;
    }

    private boolean estaCasillaOcupada(double x, double y) {
        for (int i = 0; i < this.rosas.length; i++) {
            if (this.rosas[i] != null) {
                if (Math.abs(this.rosas[i].getX() - x) < 1 && Math.abs(this.rosas[i].getY() - y) < 1) {
                    return true;
                }
            }
        }
        return false;
    }

    @SuppressWarnings("unused")
    public static void main(String[] args) {
        Juego juego = new Juego();
    }
}