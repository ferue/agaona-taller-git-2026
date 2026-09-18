package py.edu.uc.lp3.ag.cs2.model;

public class GranadaHumo extends Granada {
    private float duracionHumo;

    public GranadaHumo(String nombre, int precio, String bando, float tiempoRetardo, float duracionHumo) {
        super(nombre, precio, bando, tiempoRetardo);
        this.duracionHumo = duracionHumo;
    }

    @Override
    public String ejecutarAccionPrincipal() {
        return "Lanzando " + getNombre() + ". Se despliega una cortina de humo por " + duracionHumo + " segundos.";
    }

    @Override
    public String obtenerDetalles() {
        return "Granada de Humo " + getNombre() + " | Duración: " + duracionHumo + "s | Precio: $" + getPrecio();
    }

    public float getDuracionHumo() {
        return duracionHumo;
    }
}