package py.edu.uc.lp3.ag.cs2.exception.negocio;

/** La granada HE ya explotó: se consume al detonar. → 409 */
public class GranadaYaDetonadaException extends ReglaNegocioException {
    private static final long serialVersionUID = 1L;

    public GranadaYaDetonadaException(String nombre) {
        super("La granada '" + nombre + "' ya fue detonada y no se puede volver a usar");
    }
}
