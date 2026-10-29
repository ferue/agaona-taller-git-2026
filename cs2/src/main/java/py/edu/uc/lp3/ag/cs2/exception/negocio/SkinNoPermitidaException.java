package py.edu.uc.lp3.ag.cs2.exception.negocio;

/** El arma no admite skins (en CS2, las granadas). → 400 */
public class SkinNoPermitidaException extends ReglaNegocioException {
    private static final long serialVersionUID = 1L;

    public SkinNoPermitidaException(String tipo) {
        super("En CS2 las armas de tipo " + tipo + " no admiten skins");
    }
}
