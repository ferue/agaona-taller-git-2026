package py.edu.uc.lp3.ag.cs2.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import py.edu.uc.lp3.ag.cs2.exception.validacion.CampoObligatorioException;
import py.edu.uc.lp3.ag.cs2.exception.validacion.CoordenadaInvalidaException;

import java.util.Objects;

/**
 * Punto del mapa en metros (x, y, z; z es la altura).
 *
 * Es un objeto valor: no tiene id propio y no cambia. Moverlo devuelve una
 * Posicion nueva. Se guarda dentro de la tabla de quien la usa (@Embeddable),
 * en las columnas pos_x, pos_y y pos_z.
 *
 * Invariante: las tres coordenadas son números finitos.
 */
@Embeddable
public class Posicion {

    @Column(name = "pos_x")
    private Double x;

    @Column(name = "pos_y")
    private Double y;

    @Column(name = "pos_z")
    private Double z;

    /** Solo para JPA. */
    protected Posicion() {
    }

    public Posicion(double x, double y, double z) {
        this.x = requerirFinita("x", x);
        this.y = requerirFinita("y", y);
        this.z = requerirFinita("z", z);
    }

    /** Distancia en línea recta (euclídea) hasta otro punto. */
    public double distanciaA(Posicion otra) {
        if (otra == null) {
            throw new CampoObligatorioException("posicion");
        }
        double dx = x - otra.x;
        double dy = y - otra.y;
        double dz = z - otra.z;
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    /**
     * Devuelve el punto al que llega este objeto si sale despedido "metros"
     * en línea recta, alejándose de "centro". Si está justo en el centro no
     * hay dirección horizontal, así que sale hacia arriba (eje z).
     */
    public Posicion desplazarDesde(Posicion centro, double metros) {
        double distancia = distanciaA(centro);
        if (distancia == 0) {
            return new Posicion(x, y, z + metros);
        }
        double factor = metros / distancia;
        return new Posicion(
                x + (x - centro.x) * factor,
                y + (y - centro.y) * factor,
                z + (z - centro.z) * factor);
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public double getZ() { return z; }

    private static double requerirFinita(String eje, double valor) {
        if (!Double.isFinite(valor)) {
            throw new CoordenadaInvalidaException(eje, valor);
        }
        return valor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Posicion p)) return false;
        return Objects.equals(x, p.x) && Objects.equals(y, p.y) && Objects.equals(z, p.z);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z);
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ", " + z + ")";
    }
}
