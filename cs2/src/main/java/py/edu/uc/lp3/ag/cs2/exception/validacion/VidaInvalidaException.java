package py.edu.uc.lp3.ag.cs2.exception.validacion;

/** La vida de un jugador está fuera del rango 0..100. → 400 */
public class VidaInvalidaException extends DatoInvalidoException {
    private static final long serialVersionUID = 1L;

    public VidaInvalidaException(int vida, int vidaMaxima) {
        super("La vida debe estar entre 0 y " + vidaMaxima + ": " + vida);
    }
}
