package py.edu.uc.lp3.ag.cs2.exception.noencontrado;

/** La API externa respondió que no tiene un objeto con ese id. → 404 */
public class ObjetoExternoNoEncontradoException extends RecursoNoEncontradoException {
    private static final long serialVersionUID = 1L;

    public ObjetoExternoNoEncontradoException(String id) {
        super("Objeto externo no encontrado en restful-api.dev: " + id);
    }
}
