package py.edu.uc.lp3.ag.cs2.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import py.edu.uc.lp3.ag.cs2.exception.externo.A2NoDisponibleException;
import py.edu.uc.lp3.ag.cs2.exception.externo.RechazoA2Exception;
import py.edu.uc.lp3.ag.cs2.exception.externo.RechazoCompaneroException;
import py.edu.uc.lp3.ag.cs2.exception.externo.ServicioExternoNoDisponibleException;

/**
 * Cliente de A2 (Mauricio Pecci), armado con su OpenAPI (/v3/api-docs):
 *
 * POST /api/armas/{id}/disparar  cuerpo DisparoRequest {objetivoId, distancia, critico}
 *   "Un arma de fuego dispara a un objetivo, aplica daño, consume munición y puede destruir el objetivo."
 *   200 DisparoResponse · 400 la solicitud o el estado del arma no son válidos · 404 no existe el atacante u objetivo
 *
 * URL e ids en application.properties (cs2.a2.*); también por variables de entorno (CS2_A2_URL...).
 */
@Component
public class A2ArmasClient extends CompaneroClient {

    private final long armaPorDefecto;
    private final long objetivoPorDefecto;

    public A2ArmasClient(WebClient.Builder builder,
                         @Value("${cs2.a2.nombre:Mauricio Pecci}") String companero,
                         @Value("${cs2.a2.url}") String urlBase,
                         @Value("${cs2.companeros.timeout-ms:5000}") long timeoutMs,
                         @Value("${cs2.a2.arma-id:1}") long armaPorDefecto,
                         @Value("${cs2.a2.objetivo-id:2}") long objetivoPorDefecto) {
        super(builder, "A2", companero, urlBase, timeoutMs);
        this.armaPorDefecto = armaPorDefecto;
        this.objetivoPorDefecto = objetivoPorDefecto;
    }

    public long getArmaPorDefecto() {
        return armaPorDefecto;
    }

    public long getObjetivoPorDefecto() {
        return objetivoPorDefecto;
    }

    /** Regla de disparo de A2: el arma {armaId} le dispara al arma {objetivoId}. */
    public DisparoA2 disparar(long armaId, long objetivoId) {
        DisparoA2Request cuerpo = new DisparoA2Request(objetivoId, 0, false);
        return enviar("POST /api/armas/" + armaId + "/disparar",
                webClient().post().uri("/api/armas/{id}/disparar", armaId).bodyValue(cuerpo),
                DisparoA2.class);
    }

    @Override
    protected RechazoCompaneroException rechazo(int status, String motivo) {
        return new RechazoA2Exception(getCompanero(), status, motivo);
    }

    @Override
    protected ServicioExternoNoDisponibleException noDisponible(String motivo) {
        return new A2NoDisponibleException(getCompanero(), motivo);
    }

    /** DisparoRequest del OpenAPI de A2. */
    public record DisparoA2Request(long objetivoId, int distancia, boolean critico) {
    }

    /** Campos de DisparoResponse de A2 que usa este servicio (el resto se ignora). */
    public record DisparoA2(String atacanteNombre, Integer atacanteMunicionDespues, String objetivoNombre,
                            Integer objetivoDurabilidadDespues, Integer danoAplicado, Boolean objetivoDestruido) {

        public String resumen() {
            return atacanteNombre + " le disparó a " + objetivoNombre + ": " + danoAplicado + " de daño"
                    + (Boolean.TRUE.equals(objetivoDestruido) ? " (objetivo destruido)" : "")
                    + ", quedan " + atacanteMunicionDespues + " balas";
        }
    }
}
