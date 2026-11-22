package py.edu.uc.lp3.ag.cs2.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.Locale;

/**
 * Jugador en el mapa. Participa en la simulación de explosiones.
 *
 * Guarda nombre, bando, vida, chaleco y posición. Las invariantes (nombre y
 * bando obligatorios, 0 <= vida <= 100, posición obligatoria) las controla
 * JugadorService, y cuánto daño recibe en una explosión (chaleco incluido) lo
 * calcula ExplosionService. La vida solo cambia con perderVida(...).
 *
 * Implementa UICapable, igual que Arma: dos familias distintas con el mismo
 * protocolo de HUD (lo usa GET /api/hud).
 */
@Entity
@Table(name = "jugadores")
public class Jugador implements UICapable {
    public static final int VIDA_MAXIMA = 100;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 40)
    private String nombre;

    @Column(nullable = false, length = 10)
    private String bando;

    @Column(nullable = false)
    private int vida;

    @Column(nullable = false)
    private boolean tieneChaleco;

    @Embedded
    private Posicion posicion;

    /** Solo para JPA. */
    protected Jugador() {
    }

    // Constructor sobrecargado: vida completa y sin chaleco
    public Jugador(String nombre, String bando, Posicion posicion) {
        this(nombre, bando, VIDA_MAXIMA, false, posicion);
    }

    public Jugador(String nombre, String bando, int vida, boolean tieneChaleco, Posicion posicion) {
        this.nombre = limpiar(nombre);
        this.bando = limpiar(bando);
        this.vida = vida;
        this.tieneChaleco = tieneChaleco;
        this.posicion = posicion;
    }

    /** Descuenta vida. ExplosionService ya calculó cuánto (nunca más de la vida que queda). */
    public void perderVida(int puntos) {
        this.vida -= puntos;
    }

    public boolean estaVivo() {
        return vida > 0;
    }

    // ===== Protocolo UICapable =====

    /** El icono depende del bando y de si sigue vivo. */
    @Override
    public String obtenerIconoHud() {
        if (!estaVivo()) {
            return "ui/hud/icons/player_dead.png";
        }
        return "CT".equals(bando) ? "ui/hud/icons/player_ct.png" : "ui/hud/icons/player_t.png";
    }

    @Override
    public String etiquetaHud() {
        return nombre + " (" + bando + ") " + (estaVivo() ? vida + " HP" : "ELIMINADO");
    }

    /** Redefine el método default de UICapable: el jugador responde por radio. */
    @Override
    public String emitirSonidoInspeccion() {
        return estaVivo() ? "radio_" + bando.toLowerCase(Locale.ROOT) + "_listo.wav" : "silencio.wav";
    }

    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public String getBando() { return bando; }
    public int getVida() { return vida; }
    public boolean isTieneChaleco() { return tieneChaleco; }
    public Posicion getPosicion() { return posicion; }

    /** Quita los espacios de los extremos; no valida (eso lo hace JugadorService). */
    private static String limpiar(String texto) {
        return texto == null ? null : texto.trim();
    }
}
