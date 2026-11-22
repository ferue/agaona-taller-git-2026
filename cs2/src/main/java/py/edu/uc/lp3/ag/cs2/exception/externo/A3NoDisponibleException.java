package py.edu.uc.lp3.ag.cs2.exception.externo;

/** La API de A3 no respondió, tardó demasiado o tuvo un error propio (5xx). → 502 */
public class A3NoDisponibleException extends ServicioExternoNoDisponibleException {
    private static final long serialVersionUID = 1L;

    public A3NoDisponibleException(String companero, String motivo) {
        super("A3 (" + companero + ")", motivo);
    }
}
