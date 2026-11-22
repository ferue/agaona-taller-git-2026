package py.edu.uc.lp3.ag.cs2.exception.externo;

/** La API de A2 no respondió, tardó demasiado o tuvo un error propio (5xx). → 502 */
public class A2NoDisponibleException extends ServicioExternoNoDisponibleException {
    private static final long serialVersionUID = 1L;

    public A2NoDisponibleException(String companero, String motivo) {
        super("A2 (" + companero + ")", motivo);
    }
}
