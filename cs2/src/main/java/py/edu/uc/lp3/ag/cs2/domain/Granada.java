package py.edu.uc.lp3.ag.cs2.domain;

/**
 * Arma arrojadiza. Invariante: tiempoRetardo >= 0 segundos.
 */
public abstract class Granada extends Arma {
    private final float tiempoRetardo;

    protected Granada(String nombre, int precio, String bando, float tiempoRetardo) {
        super(nombre, precio, bando);
        if (tiempoRetardo < 0) {
            throw new IllegalArgumentException("El tiempo de retardo no puede ser negativo: " + tiempoRetardo);
        }
        this.tiempoRetardo = tiempoRetardo;
    }

    public float getTiempoRetardo() {
        return tiempoRetardo;
    }
}
