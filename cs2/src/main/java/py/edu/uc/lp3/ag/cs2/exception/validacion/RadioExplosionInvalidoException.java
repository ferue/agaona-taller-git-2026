package py.edu.uc.lp3.ag.cs2.exception.validacion;

/** El radio de explosión de una granada HE no es mayor a 0. → 400 */
public class RadioExplosionInvalidoException extends DatoInvalidoException {
    private static final long serialVersionUID = 1L;

    public RadioExplosionInvalidoException(float radioExplosion) {
        super("El radio de explosión debe ser mayor a 0: " + radioExplosion);
    }
}
