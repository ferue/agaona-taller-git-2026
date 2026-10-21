package py.edu.uc.lp3.ag.cs2.domain;

/**
 * Arma que dispara munición desde un cargador.
 *
 * Invariantes: cargadorCapacidad > 0, danoBase > 0 y
 * 0 <= municionRestante <= cargadorCapacidad.
 */
public abstract class ArmaDeFuego extends Arma {
    private final int cargadorCapacidad;
    private final int danoBase;
    private int municionRestante;

    protected ArmaDeFuego(String nombre, int precio, String bando, int cargadorCapacidad, int danoBase) {
        super(nombre, precio, bando);
        if (cargadorCapacidad <= 0) {
            throw new IllegalArgumentException("La capacidad del cargador debe ser mayor a 0: " + cargadorCapacidad);
        }
        if (danoBase <= 0) {
            throw new IllegalArgumentException("El daño base debe ser mayor a 0: " + danoBase);
        }
        this.cargadorCapacidad = cargadorCapacidad;
        this.danoBase = danoBase;
        this.municionRestante = cargadorCapacidad;
    }

    public String disparar() {
        if (municionRestante > 0) {
            municionRestante--;
            return "¡PUM! Disparo efectuado con " + getNombre() + ". Munición restante: " + municionRestante;
        }
        return "¡CLIC! Cargador vacío en " + getNombre();
    }

    public String recargar() {
        if (municionRestante == cargadorCapacidad) {
            return getNombre() + " ya tiene el cargador lleno (" + cargadorCapacidad + ")";
        }
        municionRestante = cargadorCapacidad;
        return "Recargando " + getNombre() + "... Munición: " + municionRestante + "/" + cargadorCapacidad;
    }

    public int getCargadorCapacidad() { return cargadorCapacidad; }
    public int getMunicionRestante() { return municionRestante; }
    public int getDanoBase() { return danoBase; }
}
