package py.edu.uc.lp3.ag.cs2.exception.externo;

/** La API de A3 rechazó la jugada (respondió un 4xx). → 409 */
public class RechazoA3Exception extends RechazoCompaneroException {
    private static final long serialVersionUID = 1L;

    public RechazoA3Exception(String companero, int statusRecibido, String motivo) {
        super("A3", companero, statusRecibido, motivo);
    }
}
