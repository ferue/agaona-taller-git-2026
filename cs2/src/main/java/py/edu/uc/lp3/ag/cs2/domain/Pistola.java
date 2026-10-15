package py.edu.uc.lp3.ag.cs2.model;

public class Pistola extends ArmaDeFuego {
    private boolean tieneSilenciador;

    public Pistola(String nombre, int precio, String bando, int cargadorCapacidad, int danoBase, boolean tieneSilenciador) {
        super(nombre, precio, bando, cargadorCapacidad, danoBase);
        this.tieneSilenciador = tieneSilenciador;
    }

    @Override
    public String ejecutarAccionPrincipal() {
        return disparar() + (tieneSilenciador ? " (Silenciado)" : " (Ruidoso)");
    }

    @Override
    public String obtenerDetalles() {
        return "Pistola " + getNombre() + " | Daño: " + getDanoBase();
    }
}