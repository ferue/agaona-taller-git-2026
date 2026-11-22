package py.edu.uc.lp3.ag.cs2.exception.externo;

/** La API externa no respondió, tardó demasiado o devolvió un error. → 502 */
public class ServicioExternoNoDisponibleException extends ServicioExternoException {
    private static final long serialVersionUID = 1L;

    public ServicioExternoNoDisponibleException(String api, String motivo) {
        super("El servicio externo '" + api + "' no está disponible: " + motivo);
    }
}
