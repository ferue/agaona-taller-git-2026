package py.edu.uc.lp3.ag.cs2.exception.noencontrado;

import java.util.Collection;

/** No hay un elemento de HUD con ese nombre. → 404 */
public class ElementoUINoEncontradoException extends RecursoNoEncontradoException {
    private static final long serialVersionUID = 1L;

    public ElementoUINoEncontradoException(String nombre, Collection<String> valoresValidos) {
        super("No hay elemento de UI para '" + nombre + "'. Valores válidos: " + valoresValidos);
    }
}
