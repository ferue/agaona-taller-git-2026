package py.edu.uc.lp3.ag.cs2.exception.externo;

import py.edu.uc.lp3.ag.cs2.exception.Cs2Exception;

/**
 * Rama de los errores de las APIs ajenas que consume este servicio con WebClient:
 * por ahora restful-api.dev; en POO-09, las APIs de los compañeros (A2 y A3).
 */
public abstract class ServicioExternoException extends Cs2Exception {
    private static final long serialVersionUID = 1L;

    protected ServicioExternoException(String mensaje) {
        super(mensaje);
    }
}
