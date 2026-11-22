package py.edu.uc.lp3.ag.cs2.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Datos generales del documento OpenAPI (título, descripción y licencia).
 * Las operaciones las arma springdoc leyendo los controllers y sus
 * anotaciones @Tag, @Operation y @ApiResponse.
 *
 * Documento: /v3/api-docs · Swagger UI: /swagger-ui.html
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI apiCs2() {
        return new OpenAPI().info(new Info()
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

                        **Errores:** todos responden `{"status", "error", "tipo", "mensaje"}`; `tipo` es el nombre de la \
                        excepción (400 datos inválidos, 404 no existe, 409 conflicto, 502 API externa caída).
                        """)
                .license(new License().name("Apache 2.0").url("https://www.apache.org/licenses/LICENSE-2.0")));
    }
}
