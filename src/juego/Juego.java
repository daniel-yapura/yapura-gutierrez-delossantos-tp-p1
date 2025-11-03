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
    
    // --- ESTADOS ---
    private int proximoTickParaPlantar; // NUEVO

    Juego() {
        this.entorno = new Entorno(this, "La invasión de los Zombies Grinch", 810, 600);
        
        this.tablero = new Casilla[FILAS * COLUMNAS];
        this.regalos = new Regalo[FILAS];
        this.cartas = new CartaPlanta[3]; // NUEVO
        
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

        this.entorno.iniciar();
    }

    @Override
    public void tick() {
        this.dibujarTablero();
        this.dibujarCartas(); // NUEVO
        this.dibujarObjetos(this.regalos);
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

    @SuppressWarnings("unused")
    public static void main(String[] args) {
        Juego juego = new Juego();
    }
}