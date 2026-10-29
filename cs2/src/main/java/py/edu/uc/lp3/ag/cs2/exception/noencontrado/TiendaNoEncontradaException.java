package py.edu.uc.lp3.ag.cs2.exception.noencontrado;

/** No hay una tienda con ese id. → 404 */
public class TiendaNoEncontradaException extends RecursoNoEncontradoException {
    private static final long serialVersionUID = 1L;

    public TiendaNoEncontradaException(String id) {
        super("Tienda no encontrada: " + id);
    }
}
