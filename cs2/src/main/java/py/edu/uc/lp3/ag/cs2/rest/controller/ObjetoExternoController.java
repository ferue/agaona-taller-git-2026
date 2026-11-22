package py.edu.uc.lp3.ag.cs2.rest.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import py.edu.uc.lp3.ag.cs2.rest.response.ErrorResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import py.edu.uc.lp3.ag.cs2.rest.response.ObjetoExternoResponse;
import py.edu.uc.lp3.ag.cs2.service.ObjetoExternoService;

import java.util.List;

/**
 * Expone lo que se consulta a https://api.restful-api.dev con WebClient.
 * Sin lógica: delega en ObjetoExternoService.
 */
@Tag(name = "Objetos externos (WebClient)", description = "Consulta la API pública https://api.restful-api.dev con WebClient.")
@RestController
@RequestMapping("/api/objetos-externos")
public class ObjetoExternoController {

    private final ObjetoExternoService service;

    public ObjetoExternoController(ObjetoExternoService service) {
        this.service = service;
    }

    /** GET /api/objetos-externos y GET /api/objetos-externos?nombre=apple */
    @Operation(summary = "Listar objetos de restful-api.dev",
            description = "Hace GET /objects en la API externa. Filtro opcional ?nombre= (contiene, sin distinguir mayúsculas).")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de objetos"),
        @ApiResponse(responseCode = "502", description = "ServicioExternoNoDisponibleException: la API externa no respondió o devolvió un error",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping
    public List<ObjetoExternoResponse> listar(@RequestParam(required = false) String nombre) {
        return service.listar(nombre).stream().map(ObjetoExternoResponse::de).toList();
    }

    @Operation(summary = "Obtener un objeto de restful-api.dev",
            description = "Hace GET /objects/{id} en la API externa.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "El objeto"),
        @ApiResponse(responseCode = "404", description = "ObjetoExternoNoEncontradoException",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "502", description = "ServicioExternoNoDisponibleException",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public ObjetoExternoResponse obtener(@PathVariable String id) {
        return ObjetoExternoResponse.de(service.obtener(id));
    }
}
