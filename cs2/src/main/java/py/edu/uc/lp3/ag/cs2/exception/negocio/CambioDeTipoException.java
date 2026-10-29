package py.edu.uc.lp3.ag.cs2.exception.negocio;

/** Se intentó cambiar el tipo de un arma ya guardada. → 400 */
public class CambioDeTipoException extends ReglaNegocioException {
    private static final long serialVersionUID = 1L;

    public CambioDeTipoException(String tipoActual, String tipoNuevo) {
        super("No se puede cambiar el tipo de un arma: es " + tipoActual + " y se recibió " + tipoNuevo);
    }
}
