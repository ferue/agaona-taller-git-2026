package py.edu.uc.lp3.ag.cs2.domain;

/**
 * Granada de humo. Invariante: duracionHumo > 0 segundos.
 */
public class GranadaHumo extends Granada {
    public static final float RETARDO_POR_DEFECTO = 1.5f;
    public static final float DURACION_POR_DEFECTO = 18.0f;

    private final float duracionHumo;

    // Constructor sobrecargado: valores estándar de CS2
    public GranadaHumo(String nombre, int precio, String bando) {
        this(nombre, precio, bando, RETARDO_POR_DEFECTO, DURACION_POR_DEFECTO);
    }

    public GranadaHumo(String nombre, int precio, String bando, float tiempoRetardo, float duracionHumo) {
        super(nombre, precio, bando, tiempoRetardo);
        if (duracionHumo <= 0) {
            throw new IllegalArgumentException("La duración del humo debe ser mayor a 0: " + duracionHumo);
        }
        this.duracionHumo = duracionHumo;
    }

    @Override
    public String ejecutarAccionPrincipal() {
        return "Lanzando " + getNombre() + ". Se despliega una cortina de humo por " + duracionHumo + " segundos.";
    }

    @Override
    public String obtenerDetalles() {
        return "Granada de Humo " + getNombre() + " | Retardo: " + getTiempoRetardo() + "s | Duración: " + duracionHumo + "s";
    }

    @Override
    public String obtenerIconoHud() {
        return "ui/hud/icons/smoke_grenade.png";
    }

    public float getDuracionHumo() {
        return duracionHumo;
    }
}
