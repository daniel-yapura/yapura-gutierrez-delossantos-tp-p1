![logo_ungs](https://huelladigital.com.ar/V6/campito/assets/images/logoungs-1.png)

# Trabajo Práctico: La invasión de los Zombies Grinch
## Programación I (COM-07-G4) - Turno mañana

## Encabezado

* **Alumno 1:** Daniel Alejandro Yapura
    * **Email:** ale18.yapura2003@gmail.com
* **Alumno 2:** Ignacio Daniel Gutiérrez
    * **Email:** ignaciodanielgutierrez@gmail.com
* **Alumno 3:** Agustin Ezequiel De Los Santos
    * **Email:** delossantosagustinezequiel@gmail.com

---

## Introducción

El presente trabajo práctico aborda el desarrollo de un videojuego 2D del género *Tower Defense*, titulado "La invasión de los Zombies Grinch", siguiendo las especificaciones provistas por la cátedra. El objetivo del juego es proteger una serie de regalos, ubicados en la primera columna de un tablero, del ataque de hordas de Zombies Grinch que avanzan desde la derecha. Para lograrlo, el jugador puede plantar estratégicamente la planta de defensa `RoseBlade`, que ataca a los enemigos.

El juego fue desarrollado en Java, utilizando la librería `entorno.jar` proporcionada. El desarrollo se centró en implementar la lógica del juego, la gestión de estados del jugador, la física de colisiones y el cumplimiento de todos los requisitos obligatorios del proyecto.

---

## Descripción

A continuación, se detallan las clases implementadas y las decisiones de diseño tomadas para resolver los problemas encontrados.

### Clases Implementadas

* **`Juego` (clase principal):**
    * **Variables de Instancia:**
        * `entorno`: Objeto `Entorno` que controla la ventana y el bucle del juego.
        * `tablero`: Un arreglo (`Casilla[]`) que simula una matriz de 5x9. Almacena todos los objetos `Casilla`.
        * `rosas`, `zombies`, `regalos`, `cartas`: Arreglos que almacenan los objetos activos del juego (plantas, enemigos, objetivos y UI).
        * `plantaParaPlantar`: Objeto `RoseBlade` que representa la planta "fantasma" que el jugador sostiene con el mouse.
        * `plantaSeleccionada`: Objeto `RoseBlade` que representa la planta activa que el jugador está moviendo con WASD.
        * `proximoTickParaPlantar`: Variable `int` que actúa como temporizador para el cooldown de plantado.
        * `cont_zombies`, `cant_zombies`: Contadores para gestionar la condición de victoria.
        * `juegoTerminado`: Variable `boolean` que "congela" la lógica del juego al ganar o perder.
        * `tiempoFinal`: Variable `int` que almacena el tiempo al finalizar la partida.
    * **Métodos Principales:**
        * `tick()`: Bucle principal que delega tareas. Separa la lógica de **Dibujado** (que siempre se ejecuta) de la lógica de **Actualización** (que se detiene si `juegoTerminado` es `true`).
        * `manejarEstadoJugador()`: Implementa una máquina de estados para controlar si el jugador está plantando, moviendo (WASD), o en reposo.
        * `ejecutarLogicaJuego()`: Contiene los disparadores de eventos (spawns, disparos) y las llamadas a los métodos de colisión.
        * `chequearCondicion...()`: Métodos que revisan las colisiones (Disparo-Zombie, Zombie-Regalo, Zombie-Planta) y las condiciones de victoria/derrota.

* **`Casilla`:**
    * **Descripción:** Clase refactorizada que encapsula toda la lógica de una celda del tablero.
    * **Variables:** `centroX`, `centroY`, `ancho`, `alto`, `esPlantable` (booleano, `false` para la columna 0), `color`.
    * **Métodos:** `dibujarse()`, `fueClickeada(x, y)`, y *getters* para sus propiedades.

* **`CartaPlanta`:**
    * **Descripción:** Representa un botón en la UI para seleccionar la `RoseBlade`.
    * **Variables:** `x`, `y`, `ancho`, `alto`, `tipoPlanta` (un `int` para saber que es una `RoseBlade` (0)).
    * **Métodos:** `dibujarse(entorno, enCooldown)` (cambia su apariencia si está en cooldown), `fueClickeada(x, y)`.

* **`RoseBlade`:**
    * **Descripción:** La planta de ataque principal (única planta obligatoria).
    * **Variables:** `x`, `y`, `bola` (referencia a la `BolaDeFuego` que disparó).
    * **Métodos:** `dibujarse()`, `disparar()` (crea una `BolaDeFuego` si `bola` es `null`), `actualizarMovimiento()`, `setTarget()`, `snapAPosicion()`, `estaMoviendose()`.

* **`BolaDeFuego`:**
    * **Descripción:** El proyectil disparado por `RoseBlade`.
    * **Variables:** `x`, `y`.
    * **Métodos:** `dibujarse()`, `moverse()` (incrementa su `x` para ir a la derecha).

* **`ZombieGrinch`:**
    * **Descripción:** El enemigo estándar.
    * **Variables:** `x`, `y`, `vida` (inicializada en 4, cumpliendo el requisito de 2 o más disparos).
    * **Métodos:** `dibujarse()`, `moverse()` (decrementa su `x` para ir a la izquierda), `recibirDisparo()` (decrementa `vida`).

* **`Regalo`:**
    * **Descripción:** El objeto estático que el jugador debe proteger, ubicado en la primera columna.
    * **Variables:** `x`, `y`.
    * **Métodos:** `dibujarse()`.

---

## Cumplimiento de Requerimientos

Se implementaron todos los requisitos obligatorios del `Proyecto.pdf`. A continuación, se detalla cómo se abordó cada uno:

1.  **Pantalla (Req. 1):**
    * **Implementación:** La clase `Juego` instancia el `Entorno`. Se crearon dos secciones:
        * **Sector Superior (UI):** Se implementó una clase `CartaPlanta` para las cartas de selección y se usa `entorno.escribirTexto()` para mostrar la información del juego (eliminados, restantes, tiempo).
        * **Sector de Juego (Tablero):** Se refactorizó la lógica del tablero en una clase `Casilla`. La clase `Juego` gestiona un arreglo (`Casilla[]`) de 5 filas y 9 columnas. Los `Regalos` se instancian en la primera columna.

2.  **Plantar una Planta (Req. 2):**
    * **Implementación:** Se implementó una máquina de estados. Al hacer clic en una `CartaPlanta` (`intentarAgarrarCarta()`), la variable `plantaParaPlantar` se instancia. Mientras no sea `null`, la planta sigue al mouse (`manejarEstadoSosteniendoPlanta()`). Al hacer clic en una casilla válida (plantable y no ocupada), la planta se asigna al arreglo `rosas` y se vuelve `null`.
    * **Cooldown:** Se implementó un temporizador `proximoTickParaPlantar` que se activa al plantar. La carta dibuja un velo gris progresivo (`dibujarse(..., progreso)`) para cumplir con el requisito de mostrar la carga.
    * **Casilla Ocupada:** La lógica `!estaCasillaOcupada()` previene plantar sobre una planta existente, cumpliendo el requisito.

3.  **Mover una Planta (Req. 3):**
    * **Implementación:** Se usa la variable de estado `plantaSeleccionada`.
    * **Selección:** Un clic sobre una `RoseBlade` existente (detectado con `hayColision()`) la asigna a `plantaSeleccionada`.
    * **Deselección:** Un clic sobre la misma planta, o sobre cualquier otro lugar (suelo, carta), vuelve `plantaSeleccionada` a `null`.
    * **Movimiento:** El método `manejarMovimientoPlanta()` comprueba `entorno.sePresiono()` para las teclas WASD y flechas. El movimiento es un "salto" discreto que inicia una animación suave (`setTarget()`).
    * **Límites:** Antes de mover, se comprueba que la nueva casilla esté dentro de los límites del tablero y no esté ocupada por otra planta (`estaCasillaOcupada()`), cumpliendo el requisito.

4.  **Tipo de Planta: Rose Blade (Req. 4):**
    * **Implementación:** Se creó la clase `RoseBlade`. Su método `disparar()` instancia una `BolaDeFuego` que se mueve a la derecha. La muerte por colisión con zombi se maneja en `chequearColisionesPlantas()`.

5.  **Zombies Grinch (Req. 5):**
    * **Implementación:** Se creó la clase `ZombieGrinch`. El método `spawnZombie()` en `Juego` usa un temporizador (`entorno.numeroDeTick() % 100 == 0`) para crear zombis. Aparecen fuera de pantalla (`entorno.ancho() + 50`) en una fila aleatoria (`f = (int)(Math.random() * FILAS)`), y su método `moverse()` decrementa su `x` para avanzar a la izquierda.

6.  **Eliminar Zombies (Req. 6):**
    * **Implementación:** Se añadió una variable `vida` (valor 4) a `ZombieGrinch`, cumpliendo el requisito de "al menos 2 disparos". En `chequearColisionesDisparos()`, cada colisión llama a `recibirDisparo()`. Cuando `vida == 0`, el objeto en el arreglo `zombies` se vuelve `null`. La `BolaDeFuego` también se vuelve `null` al colisionar.

7.  **Cantidad de Zombies (Req. 7):**
    * **Implementación:** El arreglo `zombies` se inicializó con un tamaño de 10 (`new ZombieGrinch[10]`), cumpliendo el requisito de estar entre 1 y 15.

8.  **Condición de Victoria (Req. 8):**
    * **Implementación:** Se inicializaron `cant_zombies = 50` y `cont_zombies = 0`. Al volverse un zombi `null` (Req. 6), se incrementa `cont_zombies++`. El método `chequearCondicionVictoria()` comprueba si `cont_zombies >= cant_zombies`.

9.  **Condición de Derrota (Req. 9):**
    * **Implementación:** El método `chequearCondicionDerrota()` usa bucles anidados para comprobar la colisión (`hayColision()`) entre cada zombi y cada regalo.
    * **Mejora:** En lugar de cerrar la ventana, se activa el estado `juegoTerminado = true`, que "congela" el juego y muestra un mensaje de "¡Has Perdido!".

10. **Buen Diseño (Req. 10):**
    * **Implementación:** Se aplicó el principio de Responsabilidad Única. La lógica del `tick()` se dividió en múltiples métodos de ayuda (ej. `ejecutarLogicaJuego`, `manejarEstadoJugador`). La lógica del tablero se abstrajo en la clase `Casilla` y la lógica de la UI en `CartaPlanta`.

11. **Aclaraciones (Arrays y Null) (Req. 11):**
    * **Implementación:** El proyecto solo utiliza "arrays (arreglos)". Para simular la matriz del tablero, se usó un arreglo (`Casilla[]`) y aritmética de índices, cumpliendo la restricción. Todos los bucles que iteran sobre arreglos de objetos (`rosas`, `zombies`, etc.) incluyen comprobaciones `if (objeto != null)` para manejar correctamente los espacios vacíos y los objetos eliminados, tal como se exige.

---

## Problemas Encontrados y Soluciones

Durante el desarrollo, surgieron varios desafíos de diseño que requirieron refactorización y la implementación de lógica avanzada:

1.  **Refactor del Tablero (El Mayor Desafío):**
    * **Problema:** Inicialmente, la lógica del tablero (cálculo de posiciones, colores, dibujado) estaba mezclada dentro de la clase `Juego`, volviéndola monolítica y difícil de mantener.
    * **Decisión:** Se decidió refactorizar toda la lógica del tablero a su propia clase (`Casilla`), siguiendo el principio de Responsabilidad Única.
    * **Restricción:** El proyecto solo permite el uso de "arrays (arreglos)".
    * **Solución:** Para simular una matriz 2D respetando la restricción, se implementó un **arreglo** (`Casilla[]`) de tamaño `FILAS * COLUMNAS`. El acceso a una celda `(f, c)` se resolvió matemáticamente usando la fórmula `indice = (f * COLUMNAS) + c`. Esto satisfizo la restricción y limpió enormemente la clase `Juego`.

2.  **Gestión de Estados del Jugador (Bugs de Clics):**
    * **Problema:** El jugador tiene múltiples estados (plantando, moviendo con WASD, o en reposo), y los clics del mouse causaban conflictos.
    * **Bug 1:** Hacer clic en una carta de la UI mientras se movía una planta (WASD) provocaba que la planta se "plantara" sola en el suelo.
    * **Bug 2:** Las plantas seleccionadas (WASD) seguían disparando automáticamente.
    * **Solución:** Se implementó una lógica de prioridades en `manejarEstadoJugador()`. El estado "moviendo planta" (`plantaSeleccionada != null`) ahora tiene prioridad. Si el jugador está moviendo una planta, cualquier clic (en el suelo, en otra planta, o en una carta) solo sirve para *deseleccionar* la planta actual. Solo si `plantaSeleccionada == null` se puede interactuar con las cartas o seleccionar una nueva planta. Para el Bug 2, se añadió una comprobación (`&& rosas[i] != this.plantaSeleccionada`) al bucle de disparo automático.

3.  **Movimiento por Casillas (WASD):**
    * **Problema:** El movimiento con `estaPresionada` (movimiento libre) no cumplía el requisito de moverse por casillas discretas y respetar límites.
    * **Solución:** Se cambió el disparador a `sePresiono`, que se ejecuta una sola vez. El movimiento se implementó como un "salto" que inicia una animación suave (`setTarget()`). Antes de cada salto, se realizan dos comprobaciones: 1) Que la nueva coordenada esté dentro de los límites del tablero y 2) Se llama al método `estaCasillaOcupada()` para asegurar que la casilla de destino esté libre, cumpliendo el requisito.
    * **Bug de Animación:** Se detectó que presionar WASD repetidamente "descentraba" la planta. Se solucionó añadiendo `estaMoviendose()` a `RoseBlade` y bloqueando nuevos movimientos hasta que la animación termine.

4.  **Gestión de Objetos (`NullPointerException`):**
    * **Problema:** Al usar arrays de tamaño fijo, muchos espacios son `null`. Iterar sobre ellos para llamar a métodos (ej. `rosas[i].dibujarse()`) causaba errores `NullPointerException`. El requisito del proyecto exige que los objetos eliminados se vuelvan `null` explícitamente.
    * **Solución:** Se implementaron comprobaciones de seguridad (`if (rosas[i] != null)`) en todos los bucles `for` antes de acceder a los métodos de un objeto, asegurando un manejo robusto de los arrays.

---

## Implementación

*A continuación, se presenta el código fuente final y comentado de las clases implementadas.*

### `Juego.java`

```java
package juego;

import entorno.Entorno;
import entorno.InterfaceJuego;
import java.awt.Color;

/**
 * Clase principal del juego "La invasión de los Zombies Grinch".
 * Gestiona el bucle principal (tick), todos los objetos del juego
 * (plantas, zombies, tablero, etc.) y los estados del jugador
 * (plantando, moviendo, jugando).
 * @author Tu Grupo (Daniel Yapura, Ignacio Gutiérrez, Agustin De Los Santos)
 * @version 1.5
 */
public class Juego extends InterfaceJuego {

    // --- CONSTANTES DEL JUEGO ---
    private static final int FILAS = 5;
    private static final int COLUMNAS = 9;
    private static final int COOLDOWN_DURACION = 100; // Duración del cooldown en ticks

    // --- VARIABLES DE INSTANCIA ---
    private Entorno entorno;
    private Casilla[] tablero; // Arreglo que simula la matriz del tablero
    private RoseBlade[] rosas;
    private ZombieGrinch[] zombies;
    private Regalo[] regalos;
    private CartaPlanta[] cartas;
    
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
        this.entorno = new Entorno(this, "La invasión de los Zombies Grinch", 810, 600);

        // 2. Inicializa los arrays (contenedores)
        this.tablero = new Casilla[FILAS * COLUMNAS];
        this.rosas = new RoseBlade[10];
        this.zombies = new ZombieGrinch[10];
        this.regalos = new Regalo[FILAS];
        this.cartas = new CartaPlanta[3];
        
        // 3. Crea las cartas de la UI
        this.cartas[0] = new CartaPlanta(20, 20, 90, 70, 0); // 0 = RoseBlade
        // this.cartas[1] y [2] quedan como null (espacio para futuras plantas)

        // 4. Calcula medidas y crea el tablero
        int anchoCasilla = this.entorno.ancho() / COLUMNAS;
        int altoCasilla = anchoCasilla;
        int altoTotalTablero = FILAS * altoCasilla;
        int inicioX = 0;
        int inicioY = this.entorno.alto() - altoTotalTablero;
        
        // Llenamos el arreglo 1D del tablero
        for (int f = 0; f < FILAS; f++) {
            for (int c = 0; c < COLUMNAS; c++) {
                double x = inicioX + c * anchoCasilla + anchoCasilla / 2;
                double y = inicioY + f * altoCasilla + altoCasilla / 2;
                boolean plantable = (c >= 1); // Columna 0 (regalos) no es plantable
                
                // Lógica de color para el patrón de ajedrez
                Color color;
                if ((f % 2 == 0 && c % 2 == 0) || (f % 2 != 0 && c % 2 != 0)) {
                    color = new Color(0, 102, 0); // Verde claro
                } else {
                    color = new Color(0, 50, 0); // Verde oscuro
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
        
        // --- 1. DIBUJADO (Siempre se ejecuta) ---
        this.dibujarTablero();
        this.dibujarCartas();
        this.dibujarObjetos(this.regalos);
        this.dibujarObjetos(this.rosas);
        this.dibujarZombies();
        this.dibujarDisparos();
        this.dibujarInformacionUI();

        // --- 2. LÓGICA DE JUEGO (Solo si el juego NO ha terminado) ---
        if (!this.juegoTerminado) {
            
            this.actualizarPlantas();
            this.actualizarZombies();
            this.actualizarDisparos();
            this.ejecutarLogicaJuego();
            this.manejarEstadoJugador();
        
        } else {
            // --- 3. JUEGO TERMINADO ---
            this.dibujarMensajeFinDeJuego();
        }
    }

    // --- MÉTODOS DE AYUDA (Dividen la lógica de tick()) ---
    
    // --- MÉTODOS DE DIBUJADO Y ACTUALIZACIÓN ---

    private void dibujarTablero() {
        for (int i = 0; i < this.tablero.length; i++) {
            this.tablero[i].dibujarse(this.entorno);
        }
    }

    private void dibujarCartas() {
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
    
    private void dibujarInformacionUI() {
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

    private void dibujarObjetos(Regalo[] arrayDeRegalos) {
        for (int i = 0; i < arrayDeRegalos.length; i++) {
            if (arrayDeRegalos[i] != null) {
                arrayDeRegalos[i].dibujarse(this.entorno);
            }
        }
    }

    private void dibujarObjetos(RoseBlade[] arrayDeRosas) {
        for (int i = 0; i < arrayDeRosas.length; i++) {
            if (arrayDeRosas[i] != null) {
                boolean esLaSeleccionada = (arrayDeRosas[i] == this.plantaSeleccionada);
                arrayDeRosas[i].dibujarse(this.entorno, esLaSeleccionada);
            }
        }
    }

    private void dibujarZombies() {
        for (int z = 0; z < this.zombies.length; z++) {
            if (this.zombies[z] != null) {
                this.zombies[z].dibujarse(this.entorno);
            }
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
            if (this.zombies[z] != null) {
                this.zombies[z].moverse();
            }
        }
    }
    
    private void actualizarPlantas() {
        for (int i = 0; i < this.rosas.length; i++) {
            if (this.rosas[i] != null) {
                this.rosas[i].actualizarMovimiento();
            }
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
    
    // --- MÉTODOS DE LÓGICA DE JUEGO ---

    private void ejecutarLogicaJuego() {
        if (this.entorno.numeroDeTick() % 100 == 0) { 
            spawnZombie();
        }
        
        if (this.entorno.numeroDeTick() % 150 == 0) {
            for (int i = 0; i < this.rosas.length; i++) {
                if (this.rosas[i] != null && this.rosas[i] != this.plantaSeleccionada) {
                    this.rosas[i].disparar();
                }
            }
        }
        chequearColisionesDisparos();
        chequearCondicionDerrota();
        chequearCondicionVictoria();
        chequearColisionesPlantas();
    }

    private void spawnZombie() {
        for (int z = 0; z < this.zombies.length; z++) { 
            if (this.zombies[z] == null) { 
                int f = (int)(Math.random() * FILAS);
                int c = COLUMNAS - 1;
                int indiceCasilla = (f * COLUMNAS) + c;
                Casilla casillaSpawn = this.tablero[indiceCasilla];
                this.zombies[z] = new ZombieGrinch(this.entorno.ancho() + 50, casillaSpawn.getCentroY());
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
                            System.out.println("¡Juego Terminado! Un zombi ha robado un regalo.");
                            this.juegoTerminado = true;
                            this.tiempoFinal = this.entorno.numeroDeTick() / 60;
                        }
                    }
                }
            }
        }
    }
    
    private void chequearCondicionVictoria() {
        if (this.cont_zombies >= this.cant_zombies) {
            if (!this.juegoTerminado) {
                System.out.println("¡FELICIDADES! Has ganado el juego.");
                this.juegoTerminado = true;
                this.tiempoFinal = this.entorno.numeroDeTick() / 60;
            }
        }
    }
    
    private void dibujarMensajeFinDeJuego() {
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
    
    private void chequearColisionesPlantas() {
        for (int z = 0; z < this.zombies.length; z++) {
            for (int i = 0; i < this.rosas.length; i++) {
                if (this.zombies[z] != null && this.rosas[i] != null) {
                    if (hayColision(this.zombies[z].getX(), this.zombies[z].getY(), this.rosas[i].getX(), this.rosas[i].getY())) {
                        RoseBlade plantaComida = this.rosas[i];
                        this.rosas[i] = null;
                        if (this.plantaSeleccionada == plantaComida) {
                            this.plantaSeleccionada = null;
                        }
                    }
                }
            }
        }
    }
    
    // --- MÉTODOS DE ESTADO Y MOVIMIENTO DEL JUGADOR ---

    private void manejarEstadoJugador() {
        if (this.plantaParaPlantar != null) {
            manejarEstadoSosteniendoPlanta();
        } else {
            manejarClicsModoJugando();
        }
        
        if (this.plantaSeleccionada != null) {
            manejarMovimientoPlanta();
        }
    }
    
    private void manejarClicsModoJugando() {
        if (this.entorno.sePresionoBoton(entorno.BOTON_IZQUIERDO)) {
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

    private void intentarSeleccionarPlanta() {
        for (int i = 0; i < this.rosas.length; i++) {
            if (this.rosas[i] != null && hayColision(this.rosas[i].getX(), this.rosas[i].getY(), this.entorno.mouseX(), this.entorno.mouseY())) {
                this.plantaSeleccionada = this.rosas[i];
                break; 
            }
        }
    }
    
    private void manejarDeseleccion() {
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
    
    private void manejarMovimientoPlanta() {
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
                if (nuevaX <= limiteDerecho && !estaCasillaOcupada(nuevaX, this.plantaSeleccionada.y)) {
                    this.plantaSeleccionada.setTarget(nuevaX, this.plantaSeleccionada.y);
                }
            }
        }
    }
    
    // --- MÉTODOS DE UTILIDAD ---
    
    private boolean hayEspacioParaPlantar() {
        for (int i = 0; i < this.rosas.length; i++) {
            if (this.rosas[i] == null) {
                return true;
            }
        }
        return false;
    }

    private boolean estaCasillaOcupada(double x, double y) {
        for (int i = 0; i < this.rosas.length; i++) {
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
```

### `Casilla.java`
```java
package juego;

import java.awt.Color;
import entorno.Entorno;

/**
 * Representa una única casilla en el tablero.
 * Es responsable de saber su posición, tamaño,
 * color, y si el jugador puede plantar en ella.
 * @author Tu Grupo (Daniel Yapura, Ignacio Gutiérrez, Agustin De Los Santos)
 * @version 1.1
 */
public class Casilla {
    
    private final double centroX, centroY, ancho, alto;
    private final boolean esPlantable;
    private final Color color;

    /**
     * Constructor de la Casilla.
     * @param x El centro en X
     * @param y El centro en Y
     * @param ancho El ancho total
     * @param alto El alto total
     * @param esPlantable true si se puede plantar en esta casilla
     * @param color El color de fondo de la casilla
     */
    public Casilla(double x, double y, double ancho, double alto, boolean esPlantable, Color color) {
        this.centroX = x;
        this.centroY = y;
        this.ancho = ancho;
        this.alto = alto;
        this.esPlantable = esPlantable;
        this.color = color;
    }

    /**
     * Dibuja esta casilla en la pantalla.
     * @param entorno El contexto gráfico donde se dibujará.
     */
    public void dibujarse(Entorno entorno) {
        entorno.dibujarRectangulo(this.centroX, this.centroY, this.ancho, this.alto, 0, this.color);
    }

    /**
     * Comprueba si un clic (mouseX, mouseY) ocurrió dentro de los límites de esta casilla.
     * @param mouseX La coordenada X del clic del mouse.
     * @param mouseY La coordenada Y del clic del mouse.
     * @return true si el clic fue dentro de los bordes, false si no.
     */
    public boolean fueClickeada(int mouseX, int mouseY) {
        double xMin = this.centroX - this.ancho / 2;
        double xMax = this.centroX + this.ancho / 2;
        double yMin = this.centroY - this.alto / 2;
        double yMax = this.centroY + this.alto / 2;
        
        return (mouseX > xMin && mouseX < xMax &&
                mouseY > yMin && mouseY < yMax);
    }

    // --- Getters (Métodos para consultar los atributos privados) ---
    
    public double getCentroX() { return this.centroX; }
    public double getCentroY() { return this.centroY; }
    public boolean esPlantable() { return this.esPlantable; }
    public double getAncho() { return this.ancho; }
    public double getAlto() { return this.alto; }
}
```

### `CartaPlanta.java`
```java
package juego;

import java.awt.Color;
import entorno.Entorno;

/**
 * Representa una "carta" seleccionable en la UI superior.
 * Sabe cómo dibujarse (con su ícono) y si está en cooldown.
 * @author Tu Grupo (Daniel Yapura, Ignacio Gutiérrez, Agustin De Los Santos)
 * @version 1.2
 */
public class CartaPlanta {
    
    private final int x; // Coordenada X (esquina superior izquierda)
    private final int y; // Coordenada Y (esquina superior izquierda)
    private final int alto;
    private final int ancho;
    private final int tipoPlanta; // 0 = RoseBlade
    
    /**
     * Constructor de la Carta.
     * @param x Coordenada X (esquina superior izquierda)
     * @param y Coordenada Y (esquina superior izquierda)
     * @param alto Alto de la carta
     * @param ancho Ancho de la carta
     * @param tipoPlanta El tipo de planta que representa (0, 1, 2...)
     */
    CartaPlanta(int x, int y, int alto, int ancho, int tipoPlanta){
        this.x = x;
        this.y = y;
        this.alto = alto;
        this.ancho = ancho;
        this.tipoPlanta = tipoPlanta;
    }
    
    /**
     * Dibuja la carta, su ícono, y el velo de cooldown progresivo.
     * @param entorno El entorno de dibujado.
     * @param progresoCooldown El progreso de la recarga (un valor de 0.0 a 1.0).
     */
    public void dibujarse(Entorno entorno, double progresoCooldown) {
        double centroX = this.x + this.ancho / 2;
        double centroY = this.y + this.alto / 2;
            
        // 1. Dibujar el fondo blanco
        entorno.dibujarRectangulo(centroX, centroY, this.ancho, this.alto, 0, Color.WHITE);
        
        // 2. Dibujar el ícono de la planta (RoseBlade)
        if (this.tipoPlanta == 0) { // 0 = RoseBlade
            entorno.dibujarCirculo(centroX, centroY, this.ancho / 2, Color.RED);
        }
        
        // 3. Dibujar el velo progresivo
        if (progresoCooldown < 1.0) {
            double alturaCargada = this.alto * progresoCooldown;
            double alturaVacia = this.alto - alturaCargada;
            double centroYVeloVacio = this.y + (alturaVacia / 2);
            Color veloGris = new Color(50, 50, 50, 150); // Semitransparente
            entorno.dibujarRectangulo(centroX, centroYVeloVacio, this.ancho, alturaVacia, 0, veloGris);
        }
    }
    
    /**
     * Comprueba si un clic fue dentro de los bordes de esta carta.
     * @param mouseX La coordenada X del clic del mouse.
     * @param mouseY La coordenada Y del clic del mouse.
     * @return true si el clic fue dentro de los bordes, false si no.
     */
    public boolean fueClickeado(int mouseX, int mouseY ) {
        return (mouseX > this.x && mouseX < this.x + this.ancho &&
                mouseY > this.y && mouseY < this.y + this.alto);
    }
}
```

### `RoseBlade.java`
```java
package juego;

import java.awt.Color;
import entorno.Entorno;

/**
 * Representa la planta de ataque "RoseBlade".
 * Es responsable de gestionar su propia posición (actual y objetivo),
 * su dibujado (normal o seleccionada), y sus acciones (disparar).
 * @author Tu Grupo (Daniel Yapura, Ignacio Gutiérrez, Agustin De Los Santos)
 * @version 1.4
 */
public class RoseBlade {

    // --- CONSTANTES ---
    private static final double VELOCIDAD_MOVIMIENTO = 4;
    private static final double DIAMETRO_PLANTA = 45;
    private static final double DIAMETRO_BORDE = 50;
    private static final double MARGEN_MOVIMIENTO = 0.1; // Margen de error para 'estaMoviendose'

    // --- ATRIBUTOS ---
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
     * Permite a 'Juego' poner la bola en 'null' tras una colisión.
     * @param bola La nueva bola (generalmente 'null')
     */
    public void setBola(BolaDeFuego bola) {
        this.bola = bola;
    }
}
```

### `BolaDeFuego.java`
```java
package juego;

import java.awt.Color;
import entorno.Entorno;

/**
 * Representa el proyectil "BolaDeFuego" disparado por la RoseBlade.
 * Es responsable de gestionar su propia posición, dibujado y movimiento.
 * @author Tu Grupo (Daniel Yapura, Ignacio Gutiérrez, Agustin De Los Santos)
 * @version 1.3
 */
public class BolaDeFuego {
    
    // --- CONSTANTES ---
    private static final double VELOCIDAD = 6;
    private static final double DIAMETRO = 15;

    // --- ATRIBUTOS ---
    private double x; // Coordenada X (centro)
    private double y; // Coordenada Y (centro)

    /**
     * Constructor de la bola de fuego.
     * @param x Posición inicial en x (centro)
     * @param y Posición inicial en y (centro)
     */
    public BolaDeFuego(double x, double y) {
        this.x = x;
        this.y = y;
    }

    // --- MÉTODOS PÚBLICOS ---

    /**
     * Dibuja la bola de fuego (un círculo negro).
     * @param entorno El contexto gráfico donde se dibujará.
     */
    public void dibujarse(Entorno entorno) {
        entorno.dibujarCirculo(this.x, this.y, DIAMETRO, Color.BLACK);
    }

    /**
     * Mueve la bola de fuego un paso hacia la derecha.
     */
    public void moverse() {
        this.x += VELOCIDAD;
    }
    
    // --- GETTERS ---
    
    /**
     * @return La coordenada X actual del centro de la bola.
     */
    public double getX() {
        return this.x;
    }
    
    /**
     * @return La coordenada Y actual del centro de la bola.
     */
    public double getY() {
        return this.y;
    }
}
```

### `ZombieGrinch.java`
```java
package juego;

import java.awt.Color;
import entorno.Entorno;

/**
 * Representa al enemigo "ZombieGrinch".
 * Es responsable de moverse, dibujarse y gestionar su vida.
 * @author Tu Grupo (Daniel Yapura, Ignacio Gutiérrez, Agustin De Los Santos)
 * @version 1.4
 */
public class ZombieGrinch {
    
    // --- CONSTANTES ---
    private static final double VELOCIDAD = 0.5;
    private static final int VIDA_INICIAL = 4;
    private static final double DIAMETRO_ZOMBIE = 45;
    private static final double DIAMETRO_AURA = 50;

    // --- ATRIBUTOS ---
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
        this.vida = VIDA_INICIAL;
    }
    
    // --- MÉTODOS PÚBLICOS ---

    /**
     * Dibuja el zombi (círculo verde) y un "aura" o "capa"
     * que indica su vida restante (basado en 4 de vida total).
     * @param entorno El contexto gráfico donde se dibujará.
     */
    public void dibujarse(Entorno entorno) {
        
        // 1. DIBUJAR LAS "CAPAS" (El Aura)
        if (this.vida == VIDA_INICIAL) {
            // Vida Completa (4 golpes): Aura Blanca
            entorno.dibujarCirculo(this.x, this.y, DIAMETRO_AURA, Color.WHITE);
        } else if (this.vida > 1) {
            // Vida Media (3 o 2 golpes): Aura Amarilla
            entorno.dibujarCirculo(this.x, this.y, DIAMETRO_AURA, Color.YELLOW);
        } else {
            // Vida Baja (1 golpe): Aura Roja
            entorno.dibujarCirculo(this.x, this.y, DIAMETRO_AURA, Color.RED);
        }
        
        // 2. DIBUJAR EL ZOMBI
        entorno.dibujarCirculo(this.x, this.y, DIAMETRO_ZOMBIE, Color.GREEN);
    }

    /**
     * Mueve el zombi un paso hacia la izquierda.
     */
    public void moverse() {
        this.x -= VELOCIDAD;
    }

    /**
     * Reduce la vida del zombi en 1.
     */
    public void recibirDisparo() {
        this.vida -= 1;
    }
    
    // --- GETTERS ---
    
    public double getX() {
        return this.x;
    }
    
    public double getY() {
        return this.y;
    }
    
    public int getVida() {
        return this.vida;
    }
}
```

### `Regalo.java`
```java
package juego;

import java.awt.Color;
import entorno.Entorno;

/**
 * Representa el objetivo "Regalo" que debe ser protegido.
 * Es un objeto estático (no se mueve) y es inmutable.
 * @author Tu Grupo (Daniel Yapura, Ignacio Gutiérrez, Agustin De Los Santos)
 * @version 1.3
 */
public class Regalo {
    
    // --- CONSTANTES ---
    private static final double ANCHO = 30;
    private static final double ALTO = 30;

    // --- ATRIBUTOS ---
    private final double x; // Coordenada X (centro)
    private final double y; // Coordenada Y (centro)

    /**
     * Constructor del Regalo.
     * @param x Posición inicial en x (centro)
     * @param y Posición inicial en y (centro)
     */
    public Regalo(double x, double y) {
        this.x = x;
        this.y = y;
    }

    // --- MÉTODOS PÚBLICOS ---

    /**
     * Dibuja el regalo (un rectángulo azul).
     * @param entorno El contexto gráfico donde se dibujará.
     */
    public void dibujarse(Entorno entorno) {
        entorno.dibujarRectangulo(this.x, this.y, ANCHO, ALTO, 0, Color.BLUE);
    }
    
    // --- GETTERS ---
    
    public double getX() {
        return this.x;
    }
    
    public double getY() {
        return this.y;
    }
}
```

---

## Conclusiones

Este trabajo práctico nos permitió aplicar los conceptos fundamentales de la Programación Orientada a Objetos en un proyecto complejo y funcional. El mayor desafío fue la gestión de la lógica del tablero, que se resolvió exitosamente al refactorizar el código en una clase `Casilla` dedicada, respetando la restricción de usar únicamente la estructura de Arreglo (array).

La implementación de una máquina de estados (para gestionar los modos "plantando" y "moviendo") fue crucial para resolver los conflictos de interacción del usuario. El resultado es un juego que cumple con todos los requisitos obligatorios, incluyendo la detección de colisiones, múltiples estados del jugador, y condiciones claras de victoria y derrota.

Como lecciones aprendidas, destacamos la importancia de un diseño de clases limpio (Separación de Responsabilidades) y el manejo cuidadoso de los estados y las comprobaciones de `null` para evitar bugs comunes en la gestión de arrays.
