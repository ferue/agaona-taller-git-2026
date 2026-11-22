package py.edu.uc.lp3.ag.cs2.client;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import org.springframework.web.reactive.function.client.WebClient;
import py.edu.uc.lp3.ag.cs2.exception.externo.ServicioExternoNoDisponibleException;
import py.edu.uc.lp3.ag.cs2.exception.noencontrado.ObjetoExternoNoEncontradoException;
import py.edu.uc.lp3.ag.cs2.service.ObjetoExternoService;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Prueba el cliente WebClient y su service sin salir a internet: un
 * ExchangeFunction falso responde como lo haría https://api.restful-api.dev.
 */
class ObjetosApiClientTest {

    private static final String LISTA = """
            [
              {"id":"1","name":"Google Pixel 6 Pro","data":{"color":"Cloudy White","capacity":"128 GB"}},
              {"id":"2","name":"Apple iPhone 12 Mini, 256GB, Blue","data":null},
              {"id":"3","name":"Apple iPhone 12 Pro Max","data":{"color":"Cloudy White","capacity GB":512}}
            ]""";

    private static final String UNO = """
            {"id":"7","name":"Apple MacBook Pro 16","data":{"year":2019,"price":1849.99,"CPU model":"Intel Core i9"}}""";

    private final AtomicReference<String> ultimaRuta = new AtomicReference<>();

    private ObjetosApiClient clienteQueResponde(HttpStatus status, String cuerpo) {
        ExchangeFunction falsa = pedido -> {
            ultimaRuta.set(pedido.url().getPath());
            return Mono.just(ClientResponse.create(status)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .body(cuerpo)
                    .build());
        };
        return new ObjetosApiClient(WebClient.builder().exchangeFunction(falsa), "https://api.restful-api.dev", 1000);
    }

    @Test
    @DisplayName("listar hace GET /objects y lee la lista de objetos")
    void listar() {
        List<ObjetoExternoDto> objetos = clienteQueResponde(HttpStatus.OK, LISTA).listar();

        assertEquals("/objects", ultimaRuta.get());
        assertEquals(3, objetos.size());
        assertEquals("Google Pixel 6 Pro", objetos.get(0).name());
        assertEquals("Cloudy White", objetos.get(0).data().get("color"));
        assertEquals(null, objetos.get(1).data());
    }

    @Test
    @DisplayName("buscar hace GET /objects/{id}; un 404 de la API da vacío")
    void buscar() {
        ObjetoExternoDto macbook = clienteQueResponde(HttpStatus.OK, UNO).buscar("7").orElseThrow();
        assertEquals("/objects/7", ultimaRuta.get());
        assertEquals("Apple MacBook Pro 16", macbook.name());

        String error404 = "{\"error\":\"Oject with id=999 was not found.\"}";
        assertTrue(clienteQueResponde(HttpStatus.NOT_FOUND, error404).buscar("999").isEmpty());
    }

    @Test
    @DisplayName("Si la API falla o no responde → ServicioExternoNoDisponibleException")
    void apiCaida() {
        ObjetosApiClient conError = clienteQueResponde(HttpStatus.INTERNAL_SERVER_ERROR, "{}");
        assertThrows(ServicioExternoNoDisponibleException.class, conError::listar);
        assertThrows(ServicioExternoNoDisponibleException.class, () -> conError.buscar("1"));

        ObjetosApiClient queNoResponde = new ObjetosApiClient(
                WebClient.builder().exchangeFunction(pedido -> Mono.never()), "https://api.restful-api.dev", 200);
        assertThrows(ServicioExternoNoDisponibleException.class, queNoResponde::listar);
    }

    @Test
    @DisplayName("El service filtra por nombre y lanza 404 si el objeto no existe")
    void service() {
        ObjetoExternoService service = new ObjetoExternoService(clienteQueResponde(HttpStatus.OK, LISTA));
        assertEquals(3, service.listar(null).size());
        assertEquals(2, service.listar("apple").size());

        ObjetoExternoService sinObjeto = new ObjetoExternoService(
                clienteQueResponde(HttpStatus.NOT_FOUND, "{\"error\":\"not found\"}"));
        assertThrows(ObjetoExternoNoEncontradoException.class, () -> sinObjeto.obtener("999"));
    }
}
