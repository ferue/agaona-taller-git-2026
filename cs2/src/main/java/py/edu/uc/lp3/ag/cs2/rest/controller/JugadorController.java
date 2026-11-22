package py.edu.uc.lp3.ag.cs2.rest.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import py.edu.uc.lp3.ag.cs2.rest.response.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import py.edu.uc.lp3.ag.cs2.domain.Jugador;
import py.edu.uc.lp3.ag.cs2.rest.request.JugadorRequest;
import py.edu.uc.lp3.ag.cs2.rest.response.JugadorResponse;
import py.edu.uc.lp3.ag.cs2.service.JugadorService;

import java.net.URI;
import java.util.List;

/** Alta y consulta de los jugadores que participan en las explosiones. */
@Tag(name = "Jugadores", description = "Jugadores guardados en H2, con vida, chaleco y posición en el mapa. Son los que daña la explosión.")
@RestController
@RequestMapping("/api/jugadores")
public class JugadorController {

    private final JugadorService jugadorService;

    public JugadorController(JugadorService jugadorService) {
        this.jugadorService = jugadorService;
    }

    @Operation(summary = "Listar jugadores",
            description = "Devuelve los jugadores con su vida, estado (VIVO o ELIMINADO) y posición.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de jugadores")
    })
    @GetMapping
    public List<JugadorResponse> listar() {
        return jugadorService.listar().stream().map(JugadorResponse::de).toList();
    }

    @Operation(summary = "Obtener un jugador por id",
            description = "Devuelve el jugador con ese id.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "El jugador"),
        @ApiResponse(responseCode = "404", description = "JugadorNoEncontradoException",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id:\\d+}")
    public JugadorResponse obtener(@PathVariable Long id) {
        return JugadorResponse.de(jugadorService.obtener(id));
    }

    @Operation(summary = "Crear un jugador",
            description = "Cuerpo: nombre, bando (T o CT), x, y, z; opcionales vida (0 a 100, por defecto 100) y tieneChaleco.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Jugador creado"),
        @ApiResponse(responseCode = "400", description = "VidaInvalidaException, BandoInvalidoException, CampoObligatorioException o CoordenadaInvalidaException",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "409", description = "JugadorDuplicadoException: ya existe un jugador con ese nombre",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<JugadorResponse> crear(@RequestBody JugadorRequest request) {
        Jugador creado = jugadorService.crear(request.aDominio());
        return ResponseEntity
                .created(URI.create("/api/jugadores/" + creado.getId()))
                .body(JugadorResponse.de(creado));
    }
}
