package py.edu.uc.lp3.ag.cs2.exception.negocio;

import java.util.Collection;

/** El bando no es uno de los permitidos. → 400 */
public class BandoInvalidoException extends ReglaNegocioException {
    private static final long serialVersionUID = 1L;

    public BandoInvalidoException(String bando, Collection<String> valoresValidos) {
        super("Bando inválido: '" + bando + "'. Valores válidos: " + String.join(", ", valoresValidos));
    }
}
