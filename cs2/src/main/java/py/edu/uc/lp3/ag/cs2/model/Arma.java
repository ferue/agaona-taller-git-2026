package py.edu.uc.lp3.ag.cs2.model;

public abstract class Arma {
    private String nombre;
    private int precio;
    private String bando;
    private String skin; // Atributo para las skins de CS2

    // 1. Constructor simple (por defecto)
    public Arma() {
        this.nombre = "Arma Estándar";
        this.precio = 0;
        this.bando = "Universal";
        this.skin = "Vanilla";
    }

    // 2. Constructor sobrecargado básico
    public Arma(String nombre, int precio, String bando) {
        this.nombre = nombre;
        this.precio = precio;
        this.bando = bando;
        this.skin = "Vanilla";
    }

    // 3. Constructor sobrecargado completo (con Skin inicial)
    public Arma(String nombre, int precio, String bando, String skin) {
        this.nombre = nombre;
        this.precio = precio;
        this.bando = bando;
        this.skin = skin;
    }

    // Sobrecarga de métodos: aplicarSkin de forma simple o con StatTrak
    public void aplicarSkin(String nombreSkin) {
        this.skin = nombreSkin;
    }

    public void aplicarSkin(String nombreSkin, boolean esStatTrak) {
        this.skin = nombreSkin + (esStatTrak ? " (StatTrak™)" : "");
    }

    // Getters y Setters
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public int getPrecio() { return precio; }
    public void setPrecio(int precio) { this.precio = precio; }

    public String getBando() { return bando; }
    public void setBando(String bando) { this.bando = bando; }

    public String getSkin() { return skin; }
    public void setSkin(String skin) { this.skin = skin; }

    // Métodos abstractos que implementarán las clases hijas (Sobrescritura)
    public abstract String ejecutarAccionPrincipal();
    public abstract String obtenerDetalles();
}