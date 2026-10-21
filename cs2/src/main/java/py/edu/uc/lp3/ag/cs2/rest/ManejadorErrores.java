package py.edu.uc.lp3.ag.cs2.rest;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import py.edu.uc.lp3.ag.cs2.rest.response.ErrorResponse;

/**
 * Traduce las excepciones del dominio a respuestas HTTP con cuerpo JSON:
 * - IllegalArgumentException (invariante violada / dato inválido) → 400
 * - RecursoNoEncontradoException → 404
 */
@RestControllerAdvice
public class ManejadorErrores {

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse datoInvalido(IllegalArgumentException e) {
        return new ErrorResponse(400, "Bad Request", e.getMessage());
    }

    @ExceptionHandler(RecursoNoEncontradoException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse noEncontrado(RecursoNoEncontradoException e) {
        return new ErrorResponse(404, "Not Found", e.getMessage());
    }
}
