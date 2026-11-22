package py.edu.uc.lp3.ag.cs2.exception.externo;

/** La API de A2 rechazó la jugada (respondió un 4xx). → 409 */
public class RechazoA2Exception extends RechazoCompaneroException {
    private static final long serialVersionUID = 1L;

    public RechazoA2Exception(String companero, int statusRecibido, String motivo) {
        super("A2", companero, statusRecibido, motivo);
    }
}
