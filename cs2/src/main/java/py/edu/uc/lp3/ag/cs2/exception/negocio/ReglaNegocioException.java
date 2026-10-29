package py.edu.uc.lp3.ag.cs2.exception.negocio;

import py.edu.uc.lp3.ag.cs2.exception.Cs2Exception;

/** Rama de los datos válidos que violan una regla del negocio o del juego. */
public abstract class ReglaNegocioException extends Cs2Exception {
    private static final long serialVersionUID = 1L;

    protected ReglaNegocioException(String mensaje) {
        super(mensaje);
    }
}
