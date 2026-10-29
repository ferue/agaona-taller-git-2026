package py.edu.uc.lp3.ag.cs2.exception.validacion;

/** El saldo inicial de una tienda es negativo. → 400 */
public class SaldoInvalidoException extends DatoInvalidoException {
    private static final long serialVersionUID = 1L;

    public SaldoInvalidoException(int saldo) {
        super("El saldo inicial no puede ser negativo: " + saldo);
    }
}
