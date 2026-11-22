package py.edu.uc.lp3.ag.cs2.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import py.edu.uc.lp3.ag.cs2.exception.Cs2Exception;
import py.edu.uc.lp3.ag.cs2.exception.externo.ServicioExternoNoDisponibleException;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

/**
 * Cliente HTTP de la API pública https://restful-api.dev, hecho con WebClient.
 *
 * Solo traduce HTTP ↔ Java: la URL base y el tiempo de espera vienen de
 * application.properties (cs2.objetos-api.*), así se pueden cambiar sin tocar
 * el código. Las decisiones (qué hacer si no existe, filtrar) las toma
 * ObjetoExternoService.
 *
 * WebClient es reactivo (devuelve Mono/Flux). Como esta aplicación es Spring
 * MVC, cada pedido espera la respuesta con block(), con un límite de tiempo.
 */
@Component
public class ObjetosApiClient {

    static final String NOMBRE_API = "restful-api.dev";

    private final WebClient webClient;
    private final Duration timeout;

    // Spring Boot ya trae configurado un WebClient.Builder (con Jackson); acá solo se le pone la URL base.
    public ObjetosApiClient(WebClient.Builder builder,
                            @Value("${cs2.objetos-api.url}") String urlBase,
                            @Value("${cs2.objetos-api.timeout-ms:5000}") long timeoutMs) {
        this.webClient = builder.baseUrl(urlBase).build();
        this.timeout = Duration.ofMillis(timeoutMs);
    }

    /** GET /objects: todos los objetos. */
    public List<ObjetoExternoDto> listar() {
        Mono<List<ObjetoExternoDto>> pedido = webClient.get()
                .uri("/objects")
                .retrieve()
                .bodyToFlux(ObjetoExternoDto.class)
                .collectList();
        return esperar(pedido, "GET /objects");
    }

    /** GET /objects/{id}: vacío si la API responde 404. */
    public Optional<ObjetoExternoDto> buscar(String id) {
        Mono<ObjetoExternoDto> pedido = webClient.get()
                .uri("/objects/{id}", id)
                .exchangeToMono(respuesta -> {
                    if (respuesta.statusCode().value() == 404) {
                        return respuesta.releaseBody().then(Mono.<ObjetoExternoDto>empty());
                    }
                    if (respuesta.statusCode().is2xxSuccessful()) {
                        return respuesta.bodyToMono(ObjetoExternoDto.class);
                    }
                    return respuesta.createError();
                });
        return Optional.ofNullable(esperar(pedido, "GET /objects/" + id));
    }

    /**
     * Espera la respuesta con un tiempo máximo. Cualquier falla de red, tiempo
     * agotado o respuesta 4xx/5xx inesperada se convierte en una excepción de
     * nuestra jerarquía, que ManejadorErrores responde con 502.
     */
    private <T> T esperar(Mono<T> pedido, String operacion) {
        return pedido
                .timeout(timeout)
                .onErrorMap(error -> !(error instanceof Cs2Exception),
                        error -> new ServicioExternoNoDisponibleException(NOMBRE_API,
                                operacion + " falló (" + error.getClass().getSimpleName() + ": " + error.getMessage() + ")"))
                .block();
    }
}
