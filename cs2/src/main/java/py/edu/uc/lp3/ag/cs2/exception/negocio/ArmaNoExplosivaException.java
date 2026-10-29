package py.edu.uc.lp3.ag.cs2.exception.negocio;

/** Se pidió hacer explotar un arma que no es una granada HE. → 400 */
public class ArmaNoExplosivaException extends ReglaNegocioException {
    private static final long serialVersionUID = 1L;

    public ArmaNoExplosivaException(String nombre, String tipo) {
        super("El arma '" + nombre + "' es de tipo " + tipo + " y no puede explotar: solo explotan las granadas HE");
    }
}
