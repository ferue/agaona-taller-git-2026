package py.edu.uc.lp3.ag.cs2.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import py.edu.uc.lp3.ag.cs2.exception.validacion.CampoObligatorioException;
import py.edu.uc.lp3.ag.cs2.exception.validacion.DanoInvalidoException;
import py.edu.uc.lp3.ag.cs2.exception.validacion.VidaInvalidaException;

/**
 * Jugador en el mapa. Participa en la simulación de explosiones.
 *
 * Invariantes: nombre y bando obligatorios, 0 <= vida <= 100 y siempre
 * tiene una posición. La vida solo baja con recibirDano(...), que nunca la
 * deja por debajo de 0.
 */
@Entity
@Table(name = "jugadores")
public class Jugador {
    public static final int VIDA_MAXIMA = 100;
    /** El chaleco (kevlar) deja pasar la mitad del daño de una explosión. */
    public static final double FACTOR_CHALECO = 0.5;

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
        this.nombre = requerirTexto(nombre, "nombre");
        this.bando = requerirTexto(bando, "bando");
        if (vida < 0 || vida > VIDA_MAXIMA) {
            throw new VidaInvalidaException(vida, VIDA_MAXIMA);
        }
        if (posicion == null) {
            throw new CampoObligatorioException("posicion");
        }
        this.vida = vida;
        this.tieneChaleco = tieneChaleco;
        this.posicion = posicion;
    }

    /**
     * Aplica el daño de una explosión: el chaleco lo reduce a la mitad y la
     * vida no baja de 0. Devuelve el daño que realmente se descontó.
     */
    public int recibirDano(int dano) {
        if (dano < 0) {
            throw new DanoInvalidoException("El daño recibido no puede ser negativo", dano);
        }
        int danoReal = tieneChaleco ? (int) Math.round(dano * FACTOR_CHALECO) : dano;
        danoReal = Math.min(danoReal, vida);
        vida -= danoReal;
        return danoReal;
    }

    public boolean estaVivo() {
        return vida > 0;
    }

    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public String getBando() { return bando; }
    public int getVida() { return vida; }
    public boolean isTieneChaleco() { return tieneChaleco; }
    public Posicion getPosicion() { return posicion; }

    private static String requerirTexto(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new CampoObligatorioException(campo);
        }
        return valor.trim();
    }
}
