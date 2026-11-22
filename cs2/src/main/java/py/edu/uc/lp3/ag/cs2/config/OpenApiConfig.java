package py.edu.uc.lp3.ag.cs2.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Datos generales del documento OpenAPI (título, descripción y licencia).
 * Las operaciones las arma springdoc leyendo los controllers y sus
 * anotaciones @Tag, @Operation y @ApiResponse.
 *
 * Documento: /v3/api-docs · Swagger UI: /swagger-ui.html
 *
 * Servidores: se declaran a mano. Detrás de `tailscale funnel` springdoc
 * generaba "http://..." (el funnel le pasa el pedido por http), y desde la
 * página https del funnel el navegador bloqueaba el "Try it out" ("Failed to
 * fetch"). El primero es "/" (el mismo servidor desde donde se abre Swagger:
 * https por el funnel, http en localhost); el segundo es el link público.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI apiCs2(@Value("${cs2.openapi.url-publica:https://ariel.tail26e794.ts.net}") String urlPublica) {
        return new OpenAPI()
                .servers(List.of(
                        new Server().url("/").description("Este mismo servidor (localhost o el link del funnel)"),
                        new Server().url(urlPublica).description("Link público (tailscale funnel)")))
                .info(new Info()
                .title("API Counter-Strike 2 — LP3 CYT646 (Ariel Gaona)")
                .version("POO-09")
                .description("""
                        Servicio REST del modelado de Counter-Strike 2, con persistencia en H2.

                        **CRUD** de la familia `Arma` (Rifle, Pistola, GranadaHumo, GranadaHE) en `/api/armas`, \
                        jugadores en `/api/jugadores` y **simulación** de explosiones en `POST /api/explosiones`.

                        **Las dos reglas de la simulación:**
                        1. **Daño:** una granada HE daña a todos los jugadores vivos dentro de su radio, aliados incluidos; \
                        el daño baja con la distancia y el chaleco lo reduce a la mitad.
                        2. **Empuje:** toda arma tirada en el mapa dentro del radio sale despedida en dirección contraria \
                        a la explosión, más lejos cuanto más cerca estaba.

                        **POO-09:** `POST /api/explosiones/validar` aplica las dos reglas **sin efectos** (para los                         compañeros) y `POST /api/jugadas` es el escenario «ataque coordinado»: consulta a A2 y a A3 y                         guarda la jugada solo si los dos aceptan.

                        **Errores:** todos responden `{"status", "error", "tipo", "mensaje"}`; `tipo` es el nombre de la \
                        excepción (400 datos inválidos, 404 no existe, 409 conflicto, 502 API externa caída).
                        """)
                .license(new License().name("Apache 2.0").url("https://www.apache.org/licenses/LICENSE-2.0")));
    }
}
