package py.edu.uc.lp3.ag.cs2.exception.negocio;

/** Se pidió disparar con un arma que no es de fuego. → 400 */
public class ArmaNoDisparaException extends ReglaNegocioException {
    private static final long serialVersionUID = 1L;

    public ArmaNoDisparaException(String tipo) {
        super("Solo las armas de fuego disparan; tipo recibido: " + tipo);
    }
}
