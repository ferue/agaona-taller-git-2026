package py.edu.uc.lp3.ag.cs2.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.util.Objects;

/**
 * Punto del mapa en metros (x, y, z; z es la altura).
 *
 * Es un objeto valor: no tiene id propio y no cambia. Para moverla se crea
 * una Posicion nueva. Se guarda dentro de la tabla de quien la usa (@Embeddable),
 * en las columnas pos_x, pos_y y pos_z.
 *
 * Solo guarda las coordenadas. Que sean números finitos lo controlan los
 * services, y las distancias y desplazamientos de la explosión los calcula
 * ExplosionService.
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
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public double getZ() { return z; }

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
