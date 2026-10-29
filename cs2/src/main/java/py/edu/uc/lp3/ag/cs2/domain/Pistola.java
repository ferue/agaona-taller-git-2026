package py.edu.uc.lp3.ag.cs2.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "pistolas")
public class Pistola extends ArmaDeFuego {
    public static final int CARGADOR_POR_DEFECTO = 12;
    public static final int DANO_POR_DEFECTO = 30;

    @Column(nullable = false)
    private boolean tieneSilenciador;

    /** Solo para JPA. */
    protected Pistola() {
        super();
    }

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

    @Override
    public void actualizarCon(Arma datos) {
        super.actualizarCon(datos);
        this.tieneSilenciador = ((Pistola) datos).tieneSilenciador;
    }

    public boolean isTieneSilenciador() {
        return tieneSilenciador;
    }
}
