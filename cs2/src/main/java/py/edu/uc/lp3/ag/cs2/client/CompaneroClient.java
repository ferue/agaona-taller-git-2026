package py.edu.uc.lp3.ag.cs2.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import py.edu.uc.lp3.ag.cs2.exception.Cs2Exception;
import py.edu.uc.lp3.ag.cs2.exception.externo.RechazoCompaneroException;
import py.edu.uc.lp3.ag.cs2.exception.externo.ServicioExternoNoDisponibleException;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Map;

/**
 * Base de los clientes HTTP hacia los compañeros del POO-09 (A2 y A3).
 *
 * Solo traduce HTTP ↔ Java, con lo que dice el OpenAPI de cada compañero:
 * - 2xx: la regla ajena aceptó → se devuelve el cuerpo.
 * - 4xx: la regla ajena rechazó → excepción de rechazo de ese compañero (409).
 * - 5xx, sin conexión o tiempo agotado → excepción "no disponible" de ese compañero (502).
 *
 * Cada paso queda en el log con nivel DEBUG (logging.level.py.edu.uc.lp3.ag.cs2.client=DEBUG).
 */
public abstract class CompaneroClient {

    private static final ParameterizedTypeReference<Map<String, Object>> MAPA = new ParameterizedTypeReference<>() {
    };

    protected final Logger log = LoggerFactory.getLogger(getClass());

    private final WebClient webClient;
    private final Duration timeout;
    private final String rol;
    private final String companero;
    private final String urlBase;

    protected CompaneroClient(WebClient.Builder builder, String rol, String companero, String urlBase, long timeoutMs) {
        this.webClient = builder.baseUrl(urlBase).build();
        this.timeout = Duration.ofMillis(timeoutMs);
        this.rol = rol;
        this.companero = companero;
        this.urlBase = urlBase;
    }

    protected WebClient webClient() {
        return webClient;
    }

    public String getRol() {
        return rol;
    }

    public String getCompanero() {
        return companero;
    }

    public String getUrlBase() {
        return urlBase;
    }

    /** Excepción de rechazo propia de este compañero. */
    protected abstract RechazoCompaneroException rechazo(int status, String motivo);

    /** Excepción "no disponible" propia de este compañero. */
    protected abstract ServicioExternoNoDisponibleException noDisponible(String motivo);

    /**
     * POST a la API del compañero y espera la respuesta.
     *
     * @param operacion texto para el log y los mensajes, por ejemplo "POST /api/armas/4/disparar"
     * @param pedido    el pedido ya armado (ruta, query y cuerpo)
     * @param tipo      clase del cuerpo de una respuesta 2xx
     */
    protected <T> T enviar(String operacion, WebClient.RequestHeadersSpec<?> pedido, Class<T> tipo) {
        long inicio = System.currentTimeMillis();
        log.debug("[{}] {} → {} (base {})", rol, companero, operacion, urlBase);
        Mono<T> respuesta = pedido.exchangeToMono(r -> leer(r, operacion, tipo));
        try {
            T cuerpo = respuesta
                    .timeout(timeout)
                    .onErrorMap(e -> !(e instanceof Cs2Exception),
                            e -> noDisponible(operacion + " falló (" + e.getClass().getSimpleName() + ": "
                                    + e.getMessage() + ")"))
                    .block();
            log.debug("[{}] {} aceptó en {} ms: {}", rol, operacion, System.currentTimeMillis() - inicio, cuerpo);
            return cuerpo;
        } catch (RechazoCompaneroException e) {
            log.debug("[{}] {} rechazó en {} ms: {}", rol, operacion, System.currentTimeMillis() - inicio,
                    e.getMessage());
            throw e;
        } catch (ServicioExternoNoDisponibleException e) {
            log.debug("[{}] {} no disponible en {} ms: {}", rol, operacion, System.currentTimeMillis() - inicio,
                    e.getMessage());
            throw e;
        }
    }

    private <T> Mono<T> leer(ClientResponse respuesta, String operacion, Class<T> tipo) {
        int status = respuesta.statusCode().value();
        log.debug("[{}] {} respondió HTTP {}", rol, operacion, status);
        if (respuesta.statusCode().is2xxSuccessful()) {
            return respuesta.bodyToMono(tipo);
        }
        if (respuesta.statusCode().is4xxClientError()) {
            return respuesta.bodyToMono(MAPA)
                    .defaultIfEmpty(Map.of())
                    .onErrorResume(e -> Mono.just(Map.of()))
                    .flatMap(cuerpo -> Mono.error(rechazo(status, motivo(cuerpo))));
        }
        return respuesta.releaseBody()
                .then(Mono.error(noDisponible(operacion + " respondió HTTP " + status)));
    }

    /** Arma el motivo con el cuerpo de error del compañero, sea cual sea su formato. */
    static String motivo(Map<String, Object> cuerpo) {
        Object tipo = cuerpo.get("tipo");
        Object detalle = primero(cuerpo, "error", "mensaje", "message", "rechazo");
        Object rechazo = cuerpo.get("rechazo");
        StringBuilder texto = new StringBuilder();
        if (tipo != null) {
            texto.append(tipo).append(": ");
        }
        texto.append(detalle != null ? detalle : "sin detalle");
        if (rechazo != null && rechazo != detalle) {
            texto.append(" (").append(rechazo).append(")");
        }
        return texto.toString();
    }

    private static Object primero(Map<String, Object> cuerpo, String... claves) {
        for (String clave : claves) {
            if (cuerpo.get(clave) != null) {
                return cuerpo.get(clave);
            }
        }
        return null;
    }
}
