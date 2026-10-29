package py.edu.uc.lp3.ag.cs2.exception.validacion;

/** El empuje máximo de una granada HE es negativo. → 400 */
public class EmpujeInvalidoException extends DatoInvalidoException {
    private static final long serialVersionUID = 1L;

    public EmpujeInvalidoException(float empujeMaximo) {
        super("El empuje máximo no puede ser negativo: " + empujeMaximo);
    }
}
