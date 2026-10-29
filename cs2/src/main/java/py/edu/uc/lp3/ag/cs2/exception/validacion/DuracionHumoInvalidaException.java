package py.edu.uc.lp3.ag.cs2.exception.validacion;

/** La duración del humo no es mayor a 0. → 400 */
public class DuracionHumoInvalidaException extends DatoInvalidoException {
    private static final long serialVersionUID = 1L;

    public DuracionHumoInvalidaException(float duracionHumo) {
        super("La duración del humo debe ser mayor a 0: " + duracionHumo);
    }
}
