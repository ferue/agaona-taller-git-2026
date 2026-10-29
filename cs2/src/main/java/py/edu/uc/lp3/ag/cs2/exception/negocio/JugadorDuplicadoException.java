package py.edu.uc.lp3.ag.cs2.exception.negocio;

/** Ya existe un jugador con ese nombre. → 409 */
public class JugadorDuplicadoException extends ReglaNegocioException {
    private static final long serialVersionUID = 1L;

    public JugadorDuplicadoException(String nombre) {
        super("Ya existe un jugador con el nombre '" + nombre + "'");
    }
}
