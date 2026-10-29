package py.edu.uc.lp3.ag.cs2.exception;

/**
 * Raíz de la jerarquía de excepciones del proyecto.
 *
 * Todas las excepciones propias heredan de esta clase y se agrupan en tres ramas:
 * - DatoInvalidoException: un objeto del dominio recibió datos que rompen sus invariantes (400).
 * - RecursoNoEncontradoException: lo pedido no existe (404).
 * - ReglaNegocioException: los datos son válidos pero violan una regla del negocio (400 / 409).
 *
 * El dominio y los services no conocen HTTP: el código de respuesta de cada
 * tipo lo decide ManejadorErrores, que tiene un handler por cada excepción.
 */
public abstract class Cs2Exception extends RuntimeException {
    private static final long serialVersionUID = 1L;

    protected Cs2Exception(String mensaje) {
        super(mensaje);
    }
}
