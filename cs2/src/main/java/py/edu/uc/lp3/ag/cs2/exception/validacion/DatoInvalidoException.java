package py.edu.uc.lp3.ag.cs2.exception.validacion;

import py.edu.uc.lp3.ag.cs2.exception.Cs2Exception;

/** Rama de los datos que rompen una invariante (los services los validan antes de usar el objeto). */
public abstract class DatoInvalidoException extends Cs2Exception {
    private static final long serialVersionUID = 1L;

    protected DatoInvalidoException(String mensaje) {
        super(mensaje);
    }
}
