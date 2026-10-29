package py.edu.uc.lp3.ag.cs2.rest.response;

/**
 * Cuerpo JSON de todos los errores. "tipo" es el nombre de la excepción que
 * lo produjo, así se ve qué parte de la jerarquía respondió.
 */
public record ErrorResponse(int status, String error, String tipo, String mensaje) {
}
