package juego;
import java.awt.Color;
import entorno.Entorno;

public class Casilla {
	private final double centroX, centroY, ancho, alto;
    private final boolean esPlantable;
    private final Color color;

    public Casilla(double x, double y, double ancho, double alto, boolean esPlantable, Color color) {
        this.centroX = x; this.centroY = y; this.ancho = ancho;
        this.alto = alto; this.esPlantable = esPlantable; this.color = color;
}

public void dibujarse(Entorno entorno) {
    entorno.dibujarRectangulo(this.centroX, this.centroY, this.ancho, this.alto, 0, this.color);
}

public boolean fueClickeada(int mouseX, int mouseY) {
    double xMin = this.centroX - this.ancho / 2; double xMax = this.centroX + this.ancho / 2;
    double yMin = this.centroY - this.alto / 2; double yMax = this.centroY + this.alto / 2;
    return (mouseX > xMin && mouseX < xMax && mouseY > yMin && mouseY < yMax);
}

public double getCentroX() {return this.centroX;}
public double getCentroY() { return this.centroY; }
public boolean esPlantable() { return this.esPlantable; }
public double getAncho() { return this.ancho; }
public double getAlto() { return this.alto; }
}