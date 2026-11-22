package py.edu.uc.lp3.ag.cs2.client;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import org.springframework.web.reactive.function.client.WebClient;
import py.edu.uc.lp3.ag.cs2.client.A2ArmasClient.DisparoA2;
import py.edu.uc.lp3.ag.cs2.client.A3ArmasClient.DisparoA3;
import py.edu.uc.lp3.ag.cs2.exception.externo.A2NoDisponibleException;
import py.edu.uc.lp3.ag.cs2.exception.externo.A3NoDisponibleException;
import py.edu.uc.lp3.ag.cs2.exception.externo.RechazoA2Exception;
import py.edu.uc.lp3.ag.cs2.exception.externo.RechazoA3Exception;
import reactor.core.publisher.Mono;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Clientes de A2 y A3 sin salir a la red: un ExchangeFunction falso responde
 * con los cuerpos y códigos que describen sus OpenAPI.
 */
class CompanerosClientTest {

    private final AtomicReference<String> ultimaUrl = new AtomicReference<>();

    private WebClient.Builder queResponde(HttpStatus status, String cuerpo) {
        ExchangeFunction falsa = pedido -> {
            ultimaUrl.set(pedido.method() + " " + pedido.url());
            return Mono.just(ClientResponse.create(status)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .body(cuerpo).build());
        };
        return WebClient.builder().exchangeFunction(falsa);
    }

    private A2ArmasClient a2(HttpStatus status, String cuerpo) {
        return new A2ArmasClient(queResponde(status, cuerpo), "Mauricio Pecci", "https://a2.test", 1000, 1, 2);
    }

    private A3ArmasClient a3(HttpStatus status, String cuerpo) {
        return new A3ArmasClient(queResponde(status, cuerpo), "Vanessa Ricciardi", "http://a3.test:8081", 1000, 4, 10);
    }

    @Test
    @DisplayName("A2 acepta: POST /api/armas/{id}/disparar y lee DisparoResponse")
    void a2Acepta() {
        DisparoA2 r = a2(HttpStatus.OK, """
                {"atacanteId":1,"atacanteNombre":"AK-47","atacanteMunicionAntes":30,"atacanteMunicionDespues":29,
                 "objetivoId":2,"objetivoNombre":"Glock-18","objetivoDurabilidadAntes":100,
                 "objetivoDurabilidadDespues":64,"danoAplicado":36,"objetivoDestruido":false}""").disparar(1, 2);
        assertEquals("POST https://a2.test/api/armas/1/disparar", ultimaUrl.get());
        assertEquals(36, r.danoAplicado());
        assertTrue(r.resumen().contains("AK-47 le disparó a Glock-18"));
    }

    @Test
    @DisplayName("A2 rechaza (400 o 404) → RechazoA2Exception con su código y su motivo")
    void a2Rechaza() {
        RechazoA2Exception e = assertThrows(RechazoA2Exception.class, () -> a2(HttpStatus.BAD_REQUEST,
                "{\"tipo\":\"ARMA_SIN_MUNICION\",\"error\":\"El arma no tiene munición\",\"rechazo\":\"regla de disparo\"}")
                .disparar(1, 2));
        assertEquals(400, e.getStatusRecibido());
        assertTrue(e.getMessage().startsWith("A2 (Mauricio Pecci) rechazó la jugada con HTTP 400"));
        assertTrue(e.getMessage().contains("ARMA_SIN_MUNICION: El arma no tiene munición"));

        assertThrows(RechazoA2Exception.class, () -> a2(HttpStatus.NOT_FOUND, "{}").disparar(9, 2));
    }

    @Test
    @DisplayName("A2 con error propio (500) → A2NoDisponibleException")
    void a2Caido() {
        assertThrows(A2NoDisponibleException.class, () -> a2(HttpStatus.INTERNAL_SERVER_ERROR,
                "{\"status\":500,\"error\":\"Internal Server Error\"}").disparar(1, 2));
    }

    @Test
    @DisplayName("A3 acepta: POST /api/armas/{id}/disparar?distancia=10 y lee ResultadoDisparo")
    void a3Acepta() {
        DisparoA3 r = a3(HttpStatus.OK, """
                {"arma":"AK-47 lleno","tipo":"rifle","exito":true,"danio":36,"efecto":"impacto","estado":"29/90"}""")
                .disparar(4);
        assertEquals("POST http://a3.test:8081/api/armas/4/disparar?distancia=10.0", ultimaUrl.get());
        assertEquals("29/90", r.estado());
    }

    @Test
    @DisplayName("A3 rechaza con 409, o con 200 y exito = false → RechazoA3Exception")
    void a3Rechaza() {
        RechazoA3Exception e = assertThrows(RechazoA3Exception.class, () -> a3(HttpStatus.CONFLICT,
                "{\"tipo\":\"ArmaSinMunicionException\",\"codigo\":409,\"error\":\"AK-47 vacio no tiene balas\"}")
                .disparar(3));
        assertEquals(409, e.getStatusRecibido());
        assertTrue(e.getMessage().contains("ArmaSinMunicionException: AK-47 vacio no tiene balas"));

        assertThrows(RechazoA3Exception.class, () -> a3(HttpStatus.OK,
                "{\"arma\":\"AK-47 vacio\",\"exito\":false,\"efecto\":\"clic\",\"estado\":\"0/0\"}").disparar(3));
    }

    @Test
    @DisplayName("A3 sin respuesta a tiempo → A3NoDisponibleException")
    void a3SinRespuesta() {
        ExchangeFunction colgada = pedido -> Mono.never();
        A3ArmasClient lento = new A3ArmasClient(WebClient.builder().exchangeFunction(colgada),
                "Vanessa Ricciardi", "http://a3.test:8081", 200, 4, 10);
        assertThrows(A3NoDisponibleException.class, () -> lento.disparar(4));
    }
}
