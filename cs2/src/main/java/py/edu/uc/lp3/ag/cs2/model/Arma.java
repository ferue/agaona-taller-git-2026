package py.edu.uc.lp3.ag.cs2.model;

public abstract class Arma {
    private String nombre;
    private int precio;
    private String bando;

    public Arma(String nombre, int precio, String bando) {
        this.nombre = nombre;
        this.precio = precio;
        this.bando = bando;
    }

    public String getNombre() { return nombre; }
    public int getPrecio() { return precio; }
    public String getBando() { return bando; }

    public abstract String ejecutarAccionPrincipal();
    public abstract String obtenerDetalles();
}