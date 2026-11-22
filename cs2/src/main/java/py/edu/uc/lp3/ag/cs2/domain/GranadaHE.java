package py.edu.uc.lp3.ag.cs2.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 * Granada explosiva (HE) de CS2. Participa en la simulación de explosiones.
 *
 * Guarda la configuración de la explosión (radio, daño máximo y empuje
 * máximo) y si ya fue detonada. Las dos reglas de la simulación (daño a los
 * jugadores y empuje de las armas) y el control de que solo explote una vez
 * están en ExplosionService; las invariantes (radio > 0, daño máximo > 0,
 * empuje >= 0) las controla ArmaService.
 */
@Entity
@Table(name = "granadas_he")
public class GranadaHE extends Granada {
    public static final float RETARDO_POR_DEFECTO = 1.6f;
    public static final float RADIO_POR_DEFECTO = 8.0f;
    public static final int DANO_MAXIMO_POR_DEFECTO = 98;
    public static final float EMPUJE_POR_DEFECTO = 6.0f;

    @Column(nullable = false)
    private float radioExplosion;

    @Column(nullable = false)
    private int danoMaximo;

    @Column(nullable = false)
    private float empujeMaximo;

    @Column(nullable = false)
    private boolean detonada;

    /** Solo para JPA. */
    protected GranadaHE() {
        super();
    }

    // Constructor sobrecargado: valores estándar
    public GranadaHE(String nombre, int precio, String bando) {
        this(nombre, precio, bando, RETARDO_POR_DEFECTO, RADIO_POR_DEFECTO, DANO_MAXIMO_POR_DEFECTO, EMPUJE_POR_DEFECTO);
    }

    public GranadaHE(String nombre, int precio, String bando, float tiempoRetardo,
                     float radioExplosion, int danoMaximo, float empujeMaximo) {
        super(nombre, precio, bando, tiempoRetardo);
        this.radioExplosion = radioExplosion;
        this.danoMaximo = danoMaximo;
        this.empujeMaximo = empujeMaximo;
        this.detonada = false;
    }

    /** Marca la granada como usada: ExplosionService ya comprobó que no estaba detonada. */
    public void marcarDetonada() {
        this.detonada = true;
    }

    @Override
    public String ejecutarAccionPrincipal() {
        return "Lanzando " + getNombre() + ". ¡BOOM! Explota en " + getTiempoRetardo()
                + "s con un radio de " + radioExplosion + " m y hasta " + danoMaximo + " de daño.";
    }

    @Override
    public String obtenerDetalles() {
        return "Granada HE " + getNombre() + " | Retardo: " + getTiempoRetardo() + "s | Radio: " + radioExplosion
                + " m | Daño máx: " + danoMaximo + " | Empuje máx: " + empujeMaximo + " m"
                + (detonada ? " | DETONADA" : "");
    }

    @Override
    public String obtenerIconoHud() {
        return "ui/hud/icons/he_grenade.png";
    }

    /** Copia la configuración; el estado detonada no se puede deshacer con un PUT. */
    @Override
    public void actualizarCon(Arma datos) {
        super.actualizarCon(datos);
        GranadaHE otra = (GranadaHE) datos;
        this.radioExplosion = otra.radioExplosion;
        this.danoMaximo = otra.danoMaximo;
        this.empujeMaximo = otra.empujeMaximo;
    }

    public float getRadioExplosion() { return radioExplosion; }
    public int getDanoMaximo() { return danoMaximo; }
    public float getEmpujeMaximo() { return empujeMaximo; }
    public boolean isDetonada() { return detonada; }
}
