package py.edu.uc.lp3.ag.cs2.exception.validacion;

/** El precio de un arma es negativo. → 400 */
public class PrecioNegativoException extends DatoInvalidoException {
    private static final long serialVersionUID = 1L;

    public PrecioNegativoException(int precio) {
        super("El precio no puede ser negativo: " + precio);
    }
}
