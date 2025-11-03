package juego;

import entorno.Entorno;
import entorno.InterfaceJuego;
import java.awt.Color;

public class Juego extends InterfaceJuego {

    // --- CONSTANTES DEL JUEGO ---
    private static final int FILAS = 5;
    private static final int COLUMNAS = 9;

    // --- VARIABLES DE INSTANCIA ---
    private Entorno entorno;
    private Casilla[] tablero; // NUEVO
    private Regalo[] regalos; // NUEVO

    Juego() {
        this.entorno = new Entorno(this, "La invasión de los Zombies Grinch", 810, 600);
        
        // --- NUEVO: INICIALIZAR ARRAYS ---
        this.tablero = new Casilla[FILAS * COLUMNAS];
        this.regalos = new Regalo[FILAS];
        
        // --- NUEVO: CREAR TABLERO ---
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
        
        // --- NUEVO: CREAR REGALOS ---
        for (int f = 0; f < FILAS; f++) {
            int indiceRegalo = (f * COLUMNAS) + 0;
            this.regalos[f] = new Regalo(this.tablero[indiceRegalo].getCentroX(), this.tablero[indiceRegalo].getCentroY());
        }

        this.entorno.iniciar();
    }

    @Override
    public void tick() {
        // --- NUEVO: DIBUJADO ---
        this.dibujarTablero();
        this.dibujarObjetos(this.regalos);
    }
    
    // --- NUEVOS MÉTODOS ---
    
    private void dibujarTablero() {
        for (int i = 0; i < this.tablero.length; i++) {
            this.tablero[i].dibujarse(this.entorno);
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