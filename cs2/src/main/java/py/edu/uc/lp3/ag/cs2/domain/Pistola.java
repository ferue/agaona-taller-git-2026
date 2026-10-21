package py.edu.uc.lp3.ag.cs2.domain;

public class Pistola extends ArmaDeFuego {
    public static final int CARGADOR_POR_DEFECTO = 12;
    public static final int DANO_POR_DEFECTO = 30;

    private final boolean tieneSilenciador;

    // Constructor sobrecargado: pistola estándar sin silenciador
    public Pistola(String nombre, int precio, String bando) {
        this(nombre, precio, bando, CARGADOR_POR_DEFECTO, DANO_POR_DEFECTO, false);
    }

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
        return "Pistola " + getNombre() + " | Daño: " + getDanoBase() + " | Cargador: " + getCargadorCapacidad()
                + (tieneSilenciador ? " | Silenciador" : "");
    }

    public boolean isTieneSilenciador() {
        return tieneSilenciador;
    }
}
