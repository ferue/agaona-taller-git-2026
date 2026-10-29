package py.edu.uc.lp3.ag.cs2.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import py.edu.uc.lp3.ag.cs2.exception.negocio.CambioDeTipoException;
import py.edu.uc.lp3.ag.cs2.exception.validacion.CampoObligatorioException;
import py.edu.uc.lp3.ag.cs2.exception.validacion.PrecioNegativoException;

/**
 * Raíz de la jerarquía de armas de CS2 y entidad JPA raíz.
 *
 * Invariantes que garantiza la clase:
 * - nombre y bando nunca son nulos ni vacíos.
 * - precio nunca es negativo.
 * - skin nunca es nula ni vacía (por defecto "Vanilla").
 * Si se rompe una invariante se lanza una DatoInvalidoException (jerarquía en exception/).
 *
 * Posición: opcional. Si es null el arma está en el catálogo; si tiene una,
 * el arma está tirada en el mapa y una explosión la puede empujar.
 *
 * Persistencia: herencia JPA con estrategia JOINED. La tabla "armas" guarda
 * los datos comunes y cada subclase tiene su propia tabla unida por el id.
 *
 * Los atributos ya no son final porque JPA los completa al leer de la base,
 * pero siguen sin setters: el estado solo cambia por los constructores,
 * aplicarSkin(...), ubicarEn(...) y actualizarCon(...), que respetan las invariantes.
 */
@Entity
@Table(name = "armas")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Arma implements UICapable {

    public static final String SKIN_POR_DEFECTO = "Vanilla";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 60)
    private String nombre;

    @Column(nullable = false)
    private int precio;

    @Column(nullable = false, length = 10)
    private String bando;

    @Column(nullable = false, length = 80)
    private String skin;

    @Embedded
    private Posicion posicion;

    // 1. Constructor simple (por defecto). También lo usa JPA al leer de la base.
    protected Arma() {
        this("Arma Estándar", 0, "Universal");
    }

    // 2. Constructor sobrecargado básico: skin por defecto
    protected Arma(String nombre, int precio, String bando) {
        this(nombre, precio, bando, SKIN_POR_DEFECTO);
    }

    // 3. Constructor sobrecargado completo: único lugar donde se validan las invariantes
    protected Arma(String nombre, int precio, String bando, String skin) {
        this.nombre = requerirTexto(nombre, "nombre");
        if (precio < 0) {
            throw new PrecioNegativoException(precio);
        }
        this.precio = precio;
        this.bando = requerirTexto(bando, "bando");
        this.skin = requerirTexto(skin, "skin");
    }

    // Sobrecarga de métodos: aplicar una skin simple o con contador StatTrak™
    public void aplicarSkin(String nombreSkin) {
        aplicarSkin(nombreSkin, false);
    }

    public void aplicarSkin(String nombreSkin, boolean esStatTrak) {
        String base = requerirTexto(nombreSkin, "skin");
        this.skin = esStatTrak ? "StatTrak™ " + base : base;
    }

    public boolean tieneSkinPersonalizada() {
        return !SKIN_POR_DEFECTO.equals(skin);
    }

    /** Deja el arma tirada en ese punto del mapa (null = vuelve al catálogo). */
    public void ubicarEn(Posicion nuevaPosicion) {
        this.posicion = nuevaPosicion;
    }

    public boolean estaEnElMapa() {
        return posicion != null;
    }

    /** Por defecto un arma admite skins; Granada lo redefine. */
    public boolean admiteSkin() {
        return true;
    }

    /**
     * Copia los datos de otra arma del mismo tipo (se usa en el PUT del CRUD).
     * "datos" ya pasó por un constructor, así que sus valores cumplen las
     * invariantes. Las subclases lo sobrescriben, llaman a super y copian sus
     * propios atributos.
     */
    public void actualizarCon(Arma datos) {
        if (datos == null) {
            throw new CampoObligatorioException("datos del arma");
        }
        if (getClass() != datos.getClass()) {
            throw new CambioDeTipoException(getClass().getSimpleName(), datos.getClass().getSimpleName());
        }
        this.nombre = datos.nombre;
        this.precio = datos.precio;
        this.bando = datos.bando;
        this.skin = datos.skin;
        this.posicion = datos.posicion;
    }

    // Icono genérico; las clases hijas lo redefinen si tienen uno propio.
    // emitirSonidoInspeccion() se hereda tal cual del método default de UICapable.
    @Override
    public String obtenerIconoHud() {
        return "ui/hud/icons/default_weapon.png";
    }

    // Solo getters: el estado no se puede modificar desde afuera sin pasar por las reglas.
    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public int getPrecio() { return precio; }
    public String getBando() { return bando; }
    public String getSkin() { return skin; }
    public Posicion getPosicion() { return posicion; }

    // Métodos abstractos que implementan las clases hijas (sobrescritura)
    public abstract String ejecutarAccionPrincipal();
    public abstract String obtenerDetalles();

    protected static String requerirTexto(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new CampoObligatorioException(campo);
        }
        return valor.trim();
    }
}
