package py.edu.uc.lp3.ag.cs2.model;

public abstract class Granada extends Arma {
    private float tiempoRetardo;

    public Granada(String nombre, int precio, String bando, float tiempoRetardo) {
        super(nombre, precio, bando);
        this.tiempoRetardo = tiempoRetardo;
    }

    public float getTiempoRetardo() {
        return tiempoRetardo;
    }
}