package py.edu.uc.lp3.ag.cs2.exception.validacion;

/** El tipo de arma pedido no existe. → 400 */
public class TipoArmaNoSoportadoException extends DatoInvalidoException {
    private static final long serialVersionUID = 1L;

    public TipoArmaNoSoportadoException(String tipo, String tiposValidos) {
        super("Tipo de arma no soportado: '" + tipo + "'. Valores válidos: " + tiposValidos);
    }
}
