package py.edu.uc.lp3.ag.cs2.exception.validacion;

/** Un valor de daño no es válido (daño base o daño máximo ≤ 0, o daño recibido negativo). → 400 */
public class DanoInvalidoException extends DatoInvalidoException {
    private static final long serialVersionUID = 1L;

    public DanoInvalidoException(String descripcion, int valor) {
        super(descripcion + ": " + valor);
    }
}
