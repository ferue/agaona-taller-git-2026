package py.edu.uc.lp3.ag.cs2.rest;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import py.edu.uc.lp3.ag.cs2.exception.validacion.CampoObligatorioException;
import py.edu.uc.lp3.ag.cs2.exception.validacion.CantidadDisparosInvalidaException;
import py.edu.uc.lp3.ag.cs2.exception.validacion.CargadorInvalidoException;
import py.edu.uc.lp3.ag.cs2.exception.validacion.CoordenadaInvalidaException;
import py.edu.uc.lp3.ag.cs2.exception.validacion.DanoInvalidoException;
import py.edu.uc.lp3.ag.cs2.exception.validacion.DuracionHumoInvalidaException;
import py.edu.uc.lp3.ag.cs2.exception.validacion.EmpujeInvalidoException;
import py.edu.uc.lp3.ag.cs2.exception.validacion.PrecioNegativoException;
import py.edu.uc.lp3.ag.cs2.exception.validacion.RadioExplosionInvalidoException;
import py.edu.uc.lp3.ag.cs2.exception.validacion.SaldoInvalidoException;
import py.edu.uc.lp3.ag.cs2.exception.validacion.TiempoRetardoInvalidoException;
import py.edu.uc.lp3.ag.cs2.exception.validacion.TipoArmaNoSoportadoException;
import py.edu.uc.lp3.ag.cs2.exception.validacion.VidaInvalidaException;
import py.edu.uc.lp3.ag.cs2.exception.noencontrado.ArmaNoEncontradaException;
import py.edu.uc.lp3.ag.cs2.exception.noencontrado.ElementoUINoEncontradoException;
import py.edu.uc.lp3.ag.cs2.exception.noencontrado.JugadorNoEncontradoException;
import py.edu.uc.lp3.ag.cs2.exception.noencontrado.TiendaNoEncontradaException;
import py.edu.uc.lp3.ag.cs2.exception.negocio.ArmaDuplicadaException;
import py.edu.uc.lp3.ag.cs2.exception.negocio.ArmaNoDisparaException;
import py.edu.uc.lp3.ag.cs2.exception.negocio.ArmaNoExplosivaException;
import py.edu.uc.lp3.ag.cs2.exception.negocio.BandoInvalidoException;
import py.edu.uc.lp3.ag.cs2.exception.negocio.CambioDeTipoException;
import py.edu.uc.lp3.ag.cs2.exception.negocio.GranadaYaDetonadaException;
import py.edu.uc.lp3.ag.cs2.exception.negocio.JugadorDuplicadoException;
import py.edu.uc.lp3.ag.cs2.exception.negocio.PrecioExcedidoException;
import py.edu.uc.lp3.ag.cs2.exception.negocio.SkinNoPermitidaException;
import py.edu.uc.lp3.ag.cs2.rest.response.ErrorResponse;

/**
 * Traduce cada excepción a una respuesta HTTP con cuerpo JSON
 * {"status", "error", "tipo", "mensaje"}.
 *
 * Hay un @ExceptionHandler por cada tipo de excepción concreta de la jerarquía
 * (exception/), sin agrupar varias en un mismo método: así se ve qué código
 * HTTP corresponde a cada error. El test ManejadorErroresTest comprueba que
 * ninguna excepción de la jerarquía quede sin su handler.
 *
 * Al final están los handlers de las excepciones de Spring que puede
 * provocar un pedido mal armado (JSON inválido, parámetro faltante o de otro tipo).
 */
@RestControllerAdvice
public class ManejadorErrores {

    // ===== Datos que rompen una invariante del dominio → 400 =====

    @ExceptionHandler(CampoObligatorioException.class)
    public ResponseEntity<ErrorResponse> campoObligatorio(CampoObligatorioException e) {
        return responder(HttpStatus.BAD_REQUEST, e);
    }

    @ExceptionHandler(CantidadDisparosInvalidaException.class)
    public ResponseEntity<ErrorResponse> cantidadDisparosInvalida(CantidadDisparosInvalidaException e) {
        return responder(HttpStatus.BAD_REQUEST, e);
    }

    @ExceptionHandler(CargadorInvalidoException.class)
    public ResponseEntity<ErrorResponse> cargadorInvalido(CargadorInvalidoException e) {
        return responder(HttpStatus.BAD_REQUEST, e);
    }

    @ExceptionHandler(CoordenadaInvalidaException.class)
    public ResponseEntity<ErrorResponse> coordenadaInvalida(CoordenadaInvalidaException e) {
        return responder(HttpStatus.BAD_REQUEST, e);
    }

    @ExceptionHandler(DanoInvalidoException.class)
    public ResponseEntity<ErrorResponse> danoInvalido(DanoInvalidoException e) {
        return responder(HttpStatus.BAD_REQUEST, e);
    }

    @ExceptionHandler(DuracionHumoInvalidaException.class)
    public ResponseEntity<ErrorResponse> duracionHumoInvalida(DuracionHumoInvalidaException e) {
        return responder(HttpStatus.BAD_REQUEST, e);
    }

    @ExceptionHandler(EmpujeInvalidoException.class)
    public ResponseEntity<ErrorResponse> empujeInvalido(EmpujeInvalidoException e) {
        return responder(HttpStatus.BAD_REQUEST, e);
    }

    @ExceptionHandler(PrecioNegativoException.class)
    public ResponseEntity<ErrorResponse> precioNegativo(PrecioNegativoException e) {
        return responder(HttpStatus.BAD_REQUEST, e);
    }

    @ExceptionHandler(RadioExplosionInvalidoException.class)
    public ResponseEntity<ErrorResponse> radioExplosionInvalido(RadioExplosionInvalidoException e) {
        return responder(HttpStatus.BAD_REQUEST, e);
    }

    @ExceptionHandler(SaldoInvalidoException.class)
    public ResponseEntity<ErrorResponse> saldoInvalido(SaldoInvalidoException e) {
        return responder(HttpStatus.BAD_REQUEST, e);
    }

    @ExceptionHandler(TiempoRetardoInvalidoException.class)
    public ResponseEntity<ErrorResponse> tiempoRetardoInvalido(TiempoRetardoInvalidoException e) {
        return responder(HttpStatus.BAD_REQUEST, e);
    }

    @ExceptionHandler(TipoArmaNoSoportadoException.class)
    public ResponseEntity<ErrorResponse> tipoArmaNoSoportado(TipoArmaNoSoportadoException e) {
        return responder(HttpStatus.BAD_REQUEST, e);
    }

    @ExceptionHandler(VidaInvalidaException.class)
    public ResponseEntity<ErrorResponse> vidaInvalida(VidaInvalidaException e) {
        return responder(HttpStatus.BAD_REQUEST, e);
    }

    // ===== Recursos inexistentes → 404 =====

    @ExceptionHandler(ArmaNoEncontradaException.class)
    public ResponseEntity<ErrorResponse> armaNoEncontrada(ArmaNoEncontradaException e) {
        return responder(HttpStatus.NOT_FOUND, e);
    }

    @ExceptionHandler(ElementoUINoEncontradoException.class)
    public ResponseEntity<ErrorResponse> elementoUINoEncontrado(ElementoUINoEncontradoException e) {
        return responder(HttpStatus.NOT_FOUND, e);
    }

    @ExceptionHandler(JugadorNoEncontradoException.class)
    public ResponseEntity<ErrorResponse> jugadorNoEncontrado(JugadorNoEncontradoException e) {
        return responder(HttpStatus.NOT_FOUND, e);
    }

    @ExceptionHandler(TiendaNoEncontradaException.class)
    public ResponseEntity<ErrorResponse> tiendaNoEncontrada(TiendaNoEncontradaException e) {
        return responder(HttpStatus.NOT_FOUND, e);
    }

    // ===== Reglas de negocio → 400 o 409 =====

    @ExceptionHandler(ArmaDuplicadaException.class)
    public ResponseEntity<ErrorResponse> armaDuplicada(ArmaDuplicadaException e) {
        return responder(HttpStatus.CONFLICT, e);
    }

    @ExceptionHandler(ArmaNoDisparaException.class)
    public ResponseEntity<ErrorResponse> armaNoDispara(ArmaNoDisparaException e) {
        return responder(HttpStatus.BAD_REQUEST, e);
    }

    @ExceptionHandler(ArmaNoExplosivaException.class)
    public ResponseEntity<ErrorResponse> armaNoExplosiva(ArmaNoExplosivaException e) {
        return responder(HttpStatus.BAD_REQUEST, e);
    }

    @ExceptionHandler(BandoInvalidoException.class)
    public ResponseEntity<ErrorResponse> bandoInvalido(BandoInvalidoException e) {
        return responder(HttpStatus.BAD_REQUEST, e);
    }

    @ExceptionHandler(CambioDeTipoException.class)
    public ResponseEntity<ErrorResponse> cambioDeTipo(CambioDeTipoException e) {
        return responder(HttpStatus.BAD_REQUEST, e);
    }

    @ExceptionHandler(GranadaYaDetonadaException.class)
    public ResponseEntity<ErrorResponse> granadaYaDetonada(GranadaYaDetonadaException e) {
        return responder(HttpStatus.CONFLICT, e);
    }

    @ExceptionHandler(JugadorDuplicadoException.class)
    public ResponseEntity<ErrorResponse> jugadorDuplicado(JugadorDuplicadoException e) {
        return responder(HttpStatus.CONFLICT, e);
    }

    @ExceptionHandler(PrecioExcedidoException.class)
    public ResponseEntity<ErrorResponse> precioExcedido(PrecioExcedidoException e) {
        return responder(HttpStatus.BAD_REQUEST, e);
    }

    @ExceptionHandler(SkinNoPermitidaException.class)
    public ResponseEntity<ErrorResponse> skinNoPermitida(SkinNoPermitidaException e) {
        return responder(HttpStatus.BAD_REQUEST, e);
    }

    // ===== Excepciones de Spring por pedidos mal armados =====

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> jsonInvalido(HttpMessageNotReadableException e) {
        return responder(HttpStatus.BAD_REQUEST, "JsonInvalido", "El cuerpo de la petición no es un JSON válido");
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> parametroFaltante(MissingServletRequestParameterException e) {
        return responder(HttpStatus.BAD_REQUEST, "ParametroFaltante",
                "Falta el parámetro obligatorio '" + e.getParameterName() + "'");
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> parametroDeOtroTipo(MethodArgumentTypeMismatchException e) {
        return responder(HttpStatus.BAD_REQUEST, "ParametroDeOtroTipo",
                "El parámetro '" + e.getName() + "' tiene un valor inválido: " + e.getValue());
    }

    /** Respaldo por si dos peticiones guardan el mismo nombre a la vez (restricción UNIQUE). */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> integridad(DataIntegrityViolationException e) {
        return responder(HttpStatus.CONFLICT, "IntegridadDeDatos",
                "La operación viola una restricción de la base de datos");
    }

    private ResponseEntity<ErrorResponse> responder(HttpStatus status, RuntimeException e) {
        return responder(status, e.getClass().getSimpleName(), e.getMessage());
    }

    private ResponseEntity<ErrorResponse> responder(HttpStatus status, String tipo, String mensaje) {
        return ResponseEntity.status(status)
                .body(new ErrorResponse(status.value(), status.getReasonPhrase(), tipo, mensaje));
    }
}
