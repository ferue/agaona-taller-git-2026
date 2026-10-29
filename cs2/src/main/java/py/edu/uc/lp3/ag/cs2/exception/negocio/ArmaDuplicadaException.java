package py.edu.uc.lp3.ag.cs2.exception.negocio;

/** Ya existe un arma con ese nombre. → 409 */
public class ArmaDuplicadaException extends ReglaNegocioException {
    private static final long serialVersionUID = 1L;

    public ArmaDuplicadaException(String nombre) {
        super("Ya existe un arma con el nombre '" + nombre + "'");
    }
}
