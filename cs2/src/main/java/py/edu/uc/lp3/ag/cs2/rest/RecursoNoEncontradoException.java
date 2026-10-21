package py.edu.uc.lp3.ag.cs2.rest;

/**
 * Se lanza cuando un recurso pedido por la API no existe. El
 * ManejadorErrores la traduce a HTTP 404.
 */
public class RecursoNoEncontradoException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
