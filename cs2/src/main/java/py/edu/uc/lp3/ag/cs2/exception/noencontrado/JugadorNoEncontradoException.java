package py.edu.uc.lp3.ag.cs2.exception.noencontrado;

/** No hay un jugador con ese id en la base. → 404 */
public class JugadorNoEncontradoException extends RecursoNoEncontradoException {
    private static final long serialVersionUID = 1L;

    public JugadorNoEncontradoException(Long id) {
        super("Jugador no encontrado: " + id);
    }
}
