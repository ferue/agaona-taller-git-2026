package py.edu.uc.lp3.ag.cs2.exception.validacion;

/** La capacidad del cargador no es mayor a 0. → 400 */
public class CargadorInvalidoException extends DatoInvalidoException {
    private static final long serialVersionUID = 1L;

    public CargadorInvalidoException(int cargadorCapacidad) {
        super("La capacidad del cargador debe ser mayor a 0: " + cargadorCapacidad);
    }
}
