package py.edu.uc.lp3.ag.cs2.model;

public class Rifle extends ArmaDeFuego {
    private boolean tieneMira;

    public Rifle(String nombre, int precio, String bando, int cargadorCapacidad, int danoBase, boolean tieneMira) {
        super(nombre, precio, bando, cargadorCapacidad, danoBase);
        this.tieneMira = tieneMira;
    }

    @Override
    public String ejecutarAccionPrincipal() {
        return disparar() + (tieneMira ? " [Mira activa]" : "");
    }

    @Override
    public String obtenerDetalles() {
        return "Rifle " + getNombre() + " | Daño: " + getDanoBase();
    }
}