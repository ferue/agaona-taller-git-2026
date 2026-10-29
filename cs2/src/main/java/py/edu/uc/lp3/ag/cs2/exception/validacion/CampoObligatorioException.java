package py.edu.uc.lp3.ag.cs2.exception.validacion;

/** Un campo obligatorio llegó nulo o vacío. → 400 */
public class CampoObligatorioException extends DatoInvalidoException {
    private static final long serialVersionUID = 1L;

    public CampoObligatorioException(String campo) {
        super("El campo '" + campo + "' es obligatorio");
    }
}
