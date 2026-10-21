package py.edu.uc.lp3.ag.cs2.domain;

/**
 * Resultado de intentar comprar un arma en la Tienda.
 */
public enum ResultadoCompra {
    EXITOSA("Compra realizada"),
    INDICE_INVALIDO("No existe un arma en esa posición del catálogo"),
    SALDO_INSUFICIENTE("Saldo insuficiente para comprar el arma");

    private final String mensaje;

    ResultadoCompra(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getMensaje() {
        return mensaje;
    }
}
