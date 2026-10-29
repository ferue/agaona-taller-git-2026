package py.edu.uc.lp3.ag.cs2.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "rifles")
public class Rifle extends ArmaDeFuego {
    public static final int CARGADOR_POR_DEFECTO = 30;
    public static final int DANO_POR_DEFECTO = 36;

    @Column(nullable = false)
    private boolean tieneMira;

    /** Solo para JPA. */
    protected Rifle() {
        super();
    }

    // Constructor sobrecargado: rifle de asalto estándar sin mira
    public Rifle(String nombre, int precio, String bando) {
        this(nombre, precio, bando, CARGADOR_POR_DEFECTO, DANO_POR_DEFECTO, false);
    }

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
        return "Rifle " + getNombre() + " | Daño: " + getDanoBase() + " | Cargador: " + getCargadorCapacidad()
                + (tieneMira ? " | Con mira" : "");
    }

    @Override
    public String obtenerIconoHud() {
        return "ui/hud/icons/rifle.png";
    }

    // Redefine el método default de UICapable
    @Override
    public String emitirSonidoInspeccion() {
        return "clack-clack_metalico_rifle.wav";
    }

    @Override
    public void actualizarCon(Arma datos) {
        super.actualizarCon(datos);
        this.tieneMira = ((Rifle) datos).tieneMira;
    }

    public boolean isTieneMira() {
        return tieneMira;
    }
}
