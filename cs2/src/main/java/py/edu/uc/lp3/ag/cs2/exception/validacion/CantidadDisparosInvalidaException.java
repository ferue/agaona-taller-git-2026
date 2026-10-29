package py.edu.uc.lp3.ag.cs2.exception.validacion;

/** La cantidad de disparos de una ráfaga está fuera de rango. → 400 */
public class CantidadDisparosInvalidaException extends DatoInvalidoException {
    private static final long serialVersionUID = 1L;

    public CantidadDisparosInvalidaException(int disparos, int maximo) {
        super("disparos debe estar entre 1 y " + maximo + ": " + disparos);
    }
}
