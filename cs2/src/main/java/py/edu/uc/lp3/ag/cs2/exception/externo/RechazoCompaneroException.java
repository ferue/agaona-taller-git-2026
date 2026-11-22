package py.edu.uc.lp3.ag.cs2.exception.externo;

/**
 * Rama de los rechazos de los compañeros del POO-09: la API de A2 o de A3
 * respondió, pero dijo que la jugada no es legal (un 4xx según su OpenAPI).
 * Cada compañero tiene su propia excepción concreta, así el handler distingue
 * de quién fue el rechazo. → 409
 */
public abstract class RechazoCompaneroException extends ServicioExternoException {
    private static final long serialVersionUID = 1L;

    private final String rol;
    private final int statusRecibido;

    protected RechazoCompaneroException(String rol, String companero, int statusRecibido, String motivo) {
        super(rol + " (" + companero + ") rechazó la jugada con HTTP " + statusRecibido + ": " + motivo
                + ". No se guardó nada.");
        this.rol = rol;
        this.statusRecibido = statusRecibido;
    }

    public String getRol() {
        return rol;
    }

    public int getStatusRecibido() {
        return statusRecibido;
    }
}
