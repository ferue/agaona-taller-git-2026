package py.edu.uc.lp3.ag.cs2.domain;

public class Rifle extends ArmaDeFuego implements UICapable {
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

    @Override
    public String obtenerIconoHud() {
        return "ui/hud/icons/rifle_ak47.png";
    }

    // Opcional: Reemplazar el método default de la interfaz
    @Override
    public String emitirSonidoInspeccion() {
        return "clack-clack_metalico_rifle.wav";
    }
}