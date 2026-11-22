package py.edu.uc.lp3.ag.cs2.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import py.edu.uc.lp3.ag.cs2.exception.externo.A3NoDisponibleException;
import py.edu.uc.lp3.ag.cs2.exception.externo.RechazoA3Exception;
import py.edu.uc.lp3.ag.cs2.exception.externo.RechazoCompaneroException;
import py.edu.uc.lp3.ag.cs2.exception.externo.ServicioExternoNoDisponibleException;

/**
 * Cliente de A3 (Vanessa Ricciardi), armado con su OpenAPI (/v3/api-docs):
 *
 * POST /api/armas/{id}/disparar?distancia=10
 *   "Aplica la regla 1: consume munición o usa la granada y persiste el resultado."
 *   200 ResultadoDisparo {arma, tipo, exito, danio, efecto, estado}
 *   400 la distancia no puede ser negativa · 404 no existe el arma · 409 el arma no puede dispararse en su estado actual
 *
 * URL, id del arma y distancia en application.properties (cs2.a3.*); también por variables de entorno.
 */
@Component
public class A3ArmasClient extends CompaneroClient {

    private final long armaPorDefecto;
    private final double distancia;

    public A3ArmasClient(WebClient.Builder builder,
                         @Value("${cs2.a3.nombre:Vanessa Ricciardi}") String companero,
                         @Value("${cs2.a3.url}") String urlBase,
                         @Value("${cs2.companeros.timeout-ms:5000}") long timeoutMs,
                         @Value("${cs2.a3.arma-id:4}") long armaPorDefecto,
                         @Value("${cs2.a3.distancia:10}") double distancia) {
        super(builder, "A3", companero, urlBase, timeoutMs);
        this.armaPorDefecto = armaPorDefecto;
        this.distancia = distancia;
    }

    public long getArmaPorDefecto() {
        return armaPorDefecto;
    }

    /**
     * Regla 1 de A3: dispara el arma {armaId}. Si responde 200 pero con
     * exito = false, también cuenta como rechazo.
     */
    public DisparoA3 disparar(long armaId) {
        String operacion = "POST /api/armas/" + armaId + "/disparar?distancia=" + distancia;
        DisparoA3 resultado = enviar(operacion,
                webClient().post().uri(u -> u.path("/api/armas/{id}/disparar").queryParam("distancia", distancia)
                        .build(armaId)),
                DisparoA3.class);
        if (resultado != null && Boolean.FALSE.equals(resultado.exito())) {
            log.debug("[A3] {} respondió exito=false: {}", operacion, resultado);
            throw rechazo(200, "exito = false (" + resultado.efecto() + ", estado " + resultado.estado() + ")");
        }
        return resultado;
    }

    @Override
    protected RechazoCompaneroException rechazo(int status, String motivo) {
        return new RechazoA3Exception(getCompanero(), status, motivo);
    }

    @Override
    protected ServicioExternoNoDisponibleException noDisponible(String motivo) {
        return new A3NoDisponibleException(getCompanero(), motivo);
    }

    /** ResultadoDisparo del OpenAPI de A3. */
    public record DisparoA3(String arma, String tipo, Boolean exito, Integer danio, String efecto, String estado) {

        public String resumen() {
            return arma + " disparó: " + danio + " de daño, " + efecto + " (estado " + estado + ")";
        }
    }
}
