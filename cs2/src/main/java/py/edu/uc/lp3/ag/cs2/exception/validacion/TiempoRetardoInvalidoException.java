package py.edu.uc.lp3.ag.cs2.exception.validacion;

/** El tiempo de retardo de una granada es negativo. → 400 */
public class TiempoRetardoInvalidoException extends DatoInvalidoException {
    private static final long serialVersionUID = 1L;

    public TiempoRetardoInvalidoException(float tiempoRetardo) {
        super("El tiempo de retardo no puede ser negativo: " + tiempoRetardo);
    }
}
