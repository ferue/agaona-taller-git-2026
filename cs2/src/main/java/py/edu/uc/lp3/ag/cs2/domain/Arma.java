package py.edu.uc.lp3.ag.cs2.domain;

/**
 * Raíz de la jerarquía de armas de CS2.
 *
 * Invariantes que garantiza la clase:
 * - nombre y bando nunca son nulos ni vacíos.
 * - precio nunca es negativo.
 * - skin nunca es nula ni vacía (por defecto "Vanilla").
 *
 * nombre, precio y bando son inmutables; la skin solo cambia con aplicarSkin(...).
 */
public abstract class Arma implements UICapable {

    public static final String SKIN_POR_DEFECTO = "Vanilla";

    private final String nombre;
    private final int precio;
    private final String bando;
    private String skin;

    // 1. Constructor simple (por defecto)
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
            throw new IllegalArgumentException("El precio no puede ser negativo: " + precio);
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

    // Icono genérico; las clases hijas lo redefinen si tienen uno propio.
    // emitirSonidoInspeccion() se hereda tal cual del método default de UICapable.
    @Override
    public String obtenerIconoHud() {
        return "ui/hud/icons/default_weapon.png";
    }

    // Solo getters: el estado no se puede modificar desde afuera sin pasar por las reglas.
    public String getNombre() { return nombre; }
    public int getPrecio() { return precio; }
    public String getBando() { return bando; }
    public String getSkin() { return skin; }

    // Métodos abstractos que implementan las clases hijas (sobrescritura)
    public abstract String ejecutarAccionPrincipal();
    public abstract String obtenerDetalles();

    protected static String requerirTexto(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException("El campo '" + campo + "' es obligatorio");
        }
        return valor.trim();
    }
}
