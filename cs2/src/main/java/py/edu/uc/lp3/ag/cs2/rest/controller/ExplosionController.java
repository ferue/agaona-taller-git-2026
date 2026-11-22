package py.edu.uc.lp3.ag.cs2.rest.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import py.edu.uc.lp3.ag.cs2.rest.response.ErrorResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import py.edu.uc.lp3.ag.cs2.rest.request.ExplosionRequest;
import py.edu.uc.lp3.ag.cs2.rest.response.ExplosionResponse;
import py.edu.uc.lp3.ag.cs2.service.ExplosionService;

/**
 * Servicio REST de la simulación: POST /api/explosiones hace explotar una
 * granada HE en un punto y aplica las dos reglas de negocio.
 */
@Tag(name = "Simulación de explosiones", description = "Simulación persistida: una granada HE explota en un punto y aplica las dos reglas de negocio.")
@RestController
@RequestMapping("/api/explosiones")
public class ExplosionController {

    private final ExplosionService explosionService;

    public ExplosionController(ExplosionService explosionService) {
        this.explosionService = explosionService;
    }

    @Operation(summary = "Explotar una granada HE",
            description = "Regla 1 (daño): daña a todos los jugadores vivos dentro del radio, aliados incluidos; daño = danoMaximo × (1 − distancia / radio), y el chaleco lo reduce a la mitad; la vida no baja de 0. Regla 2 (empuje): toda arma tirada en el mapa dentro del radio se aleja del centro empujeMaximo × (1 − distancia / radio) metros. La granada se consume. Todo queda guardado en H2.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Resultado: jugadores afectados y armas desplazadas"),
        @ApiResponse(responseCode = "400", description = "ArmaNoExplosivaException (el id no es una granada HE) o CampoObligatorioException (falta granadaId, x, y o z)",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "ArmaNoEncontradaException: no hay un arma con ese id",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "409", description = "GranadaYaDetonadaException: la granada ya explotó",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ExplosionResponse explotar(@RequestBody ExplosionRequest request) {
        return ExplosionResponse.de(
                explosionService.explotar(request.granadaId(), request.centro()));
    }

    @Operation(summary = "Validar una explosión sin detonar (POO-09)",
            description = "Aplica las mismas dos reglas que POST /api/explosiones, pero sin efectos: la granada no se detona, "
                    + "las vidas no bajan y las armas no se mueven. Responde qué pasaría (simulacion = true). "
                    + "Es la operación que consultan los compañeros: rechaza con 409 si la granada ya explotó, "
                    + "400 si el id no es una HE o falta un dato, y 404 si no existe.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "La explosión es válida: a quién dañaría y qué armas empujaría (no se guardó nada)"),
        @ApiResponse(responseCode = "400", description = "ArmaNoExplosivaException (el id no es una granada HE), CampoObligatorioException (falta granadaId, x, y o z) o CoordenadaInvalidaException",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "ArmaNoEncontradaException: no hay un arma con ese id",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "409", description = "GranadaYaDetonadaException: la granada ya explotó",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/validar")
    public ExplosionResponse validar(@RequestBody ExplosionRequest request) {
        return ExplosionResponse.de(
                explosionService.validar(request.granadaId(), request.centro()));
    }
}
