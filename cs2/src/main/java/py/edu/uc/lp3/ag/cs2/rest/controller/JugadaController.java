package py.edu.uc.lp3.ag.cs2.rest.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import py.edu.uc.lp3.ag.cs2.domain.JugadaAtaque;
import py.edu.uc.lp3.ag.cs2.rest.request.JugadaRequest;
import py.edu.uc.lp3.ag.cs2.rest.response.ErrorResponse;
import py.edu.uc.lp3.ag.cs2.rest.response.JugadaResponse;
import py.edu.uc.lp3.ag.cs2.service.JugadaService;

import java.net.URI;
import java.util.List;

/** Escenario del POO-09: el cliente le habla solo a este servicio (A1), que consulta a A2 y a A3. */
@Tag(name = "Escenario POO-09: ataque coordinado",
        description = "Una jugada que necesita las reglas de A2 (disparo de Mauricio) y de A3 (disparo de Vanessa) "
                + "antes de explotar la HE propia. Se guarda en H2 solo si los dos aceptan.")
@RestController
@RequestMapping("/api/jugadas")
public class JugadaController {

    private final JugadaService jugadaService;

    public JugadaController(JugadaService jugadaService) {
        this.jugadaService = jugadaService;
    }

    @Operation(summary = "Ataque coordinado",
            description = "1) Valida las reglas propias sin efectos (la granada existe, es HE y no explotó). "
                    + "2) A2: POST /api/armas/{id}/disparar en la API de Mauricio. "
                    + "3) A3: POST /api/armas/{id}/disparar en la API de Vanessa. "
                    + "4) Si los dos aceptan, explota la HE (daño y empuje) y guarda la jugada. "
                    + "Si uno rechaza o no responde, la base queda igual y el error dice de quién fue.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "A2 y A3 aceptaron: la HE explotó y la jugada quedó guardada"),
        @ApiResponse(responseCode = "400", description = "Reglas propias: ArmaNoExplosivaException, CampoObligatorioException o CoordenadaInvalidaException",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "ArmaNoEncontradaException: la granada propia no existe",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "409", description = "RechazoA2Exception o RechazoA3Exception (un compañero rechazó; incluye su código y su motivo) o GranadaYaDetonadaException",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "502", description = "A2NoDisponibleException o A3NoDisponibleException: el compañero no respondió o tuvo un error propio",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<JugadaResponse> atacar(@RequestBody JugadaRequest request) {
        JugadaAtaque jugada = jugadaService.atacar(request.granadaId(), request.centro(),
                request.armaA2Id(), request.objetivoA2Id(), request.armaA3Id());
        return ResponseEntity.created(URI.create("/api/jugadas/" + jugada.getId())).body(JugadaResponse.de(jugada));
    }

    @Operation(summary = "Listar jugadas guardadas", description = "Las jugadas que pasaron por A2 y A3.")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Lista de jugadas")})
    @GetMapping
    public List<JugadaResponse> listar() {
        return jugadaService.listar().stream().map(JugadaResponse::de).toList();
    }

    @Operation(summary = "Obtener una jugada", description = "La jugada guardada con ese id.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "La jugada"),
        @ApiResponse(responseCode = "404", description = "JugadaNoEncontradaException",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id:\\d+}")
    public JugadaResponse obtener(@PathVariable Long id) {
        return JugadaResponse.de(jugadaService.obtener(id));
    }
}
