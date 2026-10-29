package py.edu.uc.lp3.ag.cs2.exception.negocio;

/** El precio supera el dinero máximo de una partida de CS2. → 400 */
public class PrecioExcedidoException extends ReglaNegocioException {
    private static final long serialVersionUID = 1L;

    public PrecioExcedidoException(int precio, int maximo) {
        super("El precio no puede superar $" + maximo + " (dinero máximo en una partida de CS2): " + precio);
    }
}
