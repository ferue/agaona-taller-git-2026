package py.edu.uc.lp3.ag.cs2.model;

public abstract class ArmaDeFuego extends Arma {
    private int cargadorCapacidad;
    private int municionRestante;
    private int danoBase;

    public ArmaDeFuego(String nombre, int precio, String bando, int cargadorCapacidad, int danoBase) {
        super(nombre, precio, bando);
        this.cargadorCapacidad = cargadorCapacidad;
        this.municionRestante = cargadorCapacidad;
        this.danoBase = danoBase;
    }

    public String disparar() {
        if (municionRestante > 0) {
            municionRestante--;
            return "¡PUM! Disparo efectuado con " + getNombre() + ". Munición restante: " + municionRestante;
        }
        return "¡CLIC! Cargador vacío en " + getNombre();
    }

    public int getMunicionRestante() { return municionRestante; }
    public int getDanoBase() { return danoBase; }
}