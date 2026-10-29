package py.edu.uc.lp3.ag.cs2.exception.validacion;

/** Una coordenada de una posición no es un número finito. → 400 */
public class CoordenadaInvalidaException extends DatoInvalidoException {
    private static final long serialVersionUID = 1L;

    public CoordenadaInvalidaException(String eje, double valor) {
        super("La coordenada " + eje + " debe ser un número finito: " + valor);
    }
}
