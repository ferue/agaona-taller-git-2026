package py.edu.uc.lp3.ag.cs2.exception.noencontrado;

/** No hay una jugada con ese id en la base. → 404 */
public class JugadaNoEncontradaException extends RecursoNoEncontradoException {
    private static final long serialVersionUID = 1L;

    public JugadaNoEncontradaException(Long id) {
        super("Jugada no encontrada: " + id);
    }
}
