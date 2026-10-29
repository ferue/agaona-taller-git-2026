package py.edu.uc.lp3.ag.cs2.exception.noencontrado;

/** No hay un arma con ese id en la base. → 404 */
public class ArmaNoEncontradaException extends RecursoNoEncontradoException {
    private static final long serialVersionUID = 1L;

    public ArmaNoEncontradaException(Long id) {
        super("Arma no encontrada: " + id);
    }
}
