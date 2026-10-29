package py.edu.uc.lp3.ag.cs2.exception.noencontrado;

import py.edu.uc.lp3.ag.cs2.exception.Cs2Exception;

/** Rama de los recursos pedidos por la API que no existen. */
public abstract class RecursoNoEncontradoException extends Cs2Exception {
    private static final long serialVersionUID = 1L;

    protected RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
