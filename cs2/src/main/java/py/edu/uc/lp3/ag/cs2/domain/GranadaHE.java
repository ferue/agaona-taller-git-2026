package py.edu.uc.lp3.ag.cs2.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import py.edu.uc.lp3.ag.cs2.exception.negocio.GranadaYaDetonadaException;
import py.edu.uc.lp3.ag.cs2.exception.validacion.DanoInvalidoException;
import py.edu.uc.lp3.ag.cs2.exception.validacion.EmpujeInvalidoException;
import py.edu.uc.lp3.ag.cs2.exception.validacion.RadioExplosionInvalidoException;

import java.util.Optional;

/**
 * Granada explosiva (HE) de CS2. Participa en la simulación de explosiones.
 *
 * Invariantes: radioExplosion > 0, danoMaximo > 0 y empujeMaximo >= 0.
 * Se consume al explotar: detonar() la marca como detonada y no se puede
 * volver a usar.
 *
 * Las dos reglas de la simulación son mensajes de esta clase:
 * - Regla 1, danar(jugador, centro): daña a un jugador vivo dentro del radio
 *   (aliados incluidos); el daño baja con la distancia y el chaleco lo reduce a la mitad.
 * - Regla 2, empujar(arma, centro): un arma tirada en el mapa dentro del radio
 *   sale despedida en dirección contraria a la explosión, más lejos cuanto más cerca estaba.
 *
 * Las dos fórmulas bajan en línea recta con la distancia: en el centro valen
 * el máximo y en el borde del radio valen 0.
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
        if (radioExplosion <= 0) {
            throw new RadioExplosionInvalidoException(radioExplosion);
        }
        if (danoMaximo <= 0) {
            throw new DanoInvalidoException("El daño máximo debe ser mayor a 0", danoMaximo);
        }
        if (empujeMaximo < 0) {
            throw new EmpujeInvalidoException(empujeMaximo);
        }
        this.radioExplosion = radioExplosion;
        this.danoMaximo = danoMaximo;
        this.empujeMaximo = empujeMaximo;
        this.detonada = false;
    }

    /** true si un punto a esa distancia del centro está dentro del radio. */
    public boolean alcanza(double distancia) {
        return distancia <= radioExplosion;
    }

    /** Regla 1: daño = danoMaximo × (1 − distancia / radio), redondeado; 0 fuera del radio. */
    public int calcularDano(double distancia) {
        if (!alcanza(distancia)) {
            return 0;
        }
        return (int) Math.round(danoMaximo * (1 - distancia / radioExplosion));
    }

    /** Regla 2: metros que vuela un arma = empujeMaximo × (1 − distancia / radio); 0 fuera del radio. */
    public double calcularEmpuje(double distancia) {
        if (!alcanza(distancia)) {
            return 0;
        }
        return empujeMaximo * (1 - distancia / radioExplosion);
    }

    /**
     * Regla 1 – Daño. Si el jugador está vivo y dentro del radio, recibe el
     * daño que corresponde a su distancia (Jugador aplica el chaleco y no baja
     * de 0 de vida). Devuelve el impacto, o vacío si no lo alcanzó.
     */
    public Optional<Impacto> danar(Jugador jugador, Posicion centro) {
        if (!jugador.estaVivo()) {
            return Optional.empty();
        }
        double distancia = jugador.getPosicion().distanciaA(centro);
        if (!alcanza(distancia)) {
            return Optional.empty();
        }
        int danoCalculado = calcularDano(distancia);
        int danoRecibido = jugador.recibirDano(danoCalculado);
        return Optional.of(new Impacto(jugador, distancia, danoCalculado, danoRecibido));
    }

    /**
     * Regla 2 – Empuje. Si el arma está tirada en el mapa y dentro del radio,
     * la mueve alejándola del centro (Posicion calcula el punto de llegada).
     * La granada no se empuja a sí misma. Devuelve el desplazamiento, o vacío.
     */
    public Optional<Desplazamiento> empujar(Arma arma, Posicion centro) {
        if (arma == this || !arma.estaEnElMapa()) {
            return Optional.empty();
        }
        Posicion desde = arma.getPosicion();
        double distancia = desde.distanciaA(centro);
        if (!alcanza(distancia)) {
            return Optional.empty();
        }
        double metros = calcularEmpuje(distancia);
        Posicion hasta = desde.desplazarDesde(centro, metros);
        arma.ubicarEn(hasta);
        return Optional.of(new Desplazamiento(arma, desde, hasta, metros));
    }

    /** Resultado de la regla 1 sobre un jugador. */
    public record Impacto(Jugador jugador, double distancia, int danoCalculado, int danoRecibido) {
    }

    /** Resultado de la regla 2 sobre un arma. */
    public record Desplazamiento(Arma arma, Posicion desde, Posicion hasta, double metros) {
    }

    /** La granada se consume: explota una sola vez. */
    public void detonar() {
        if (detonada) {
            throw new GranadaYaDetonadaException(getNombre());
        }
        detonada = true;
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
