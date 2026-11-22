package py.edu.uc.lp3.ag.cs2.rest.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import py.edu.uc.lp3.ag.cs2.rest.response.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import py.edu.uc.lp3.ag.cs2.domain.Arma;
import py.edu.uc.lp3.ag.cs2.rest.request.ArmaRequest;
import py.edu.uc.lp3.ag.cs2.rest.response.ArmaGuardadaResponse;
import py.edu.uc.lp3.ag.cs2.service.ArmaService;

import java.net.URI;
import java.util.List;

/**
 * Capa de presentación del CRUD de armas (raíz abstracta Arma).
 *
 * El controlador no tiene lógica de negocio: convierte JSON ↔ objetos y
 * delega en ArmaService. Las excepciones se traducen a 400/404/409 en
 * ManejadorErrores.
 *
 * El {id} solo acepta números, así no se confunde con /api/armas/probar,
 * /rafaga y /polimorfismo de ArmaController.
 */
@Tag(name = "Armas - CRUD", description = "CRUD de la familia Arma (Rifle, Pistola, GranadaHumo, GranadaHE) guardada en H2. El campo tipo elige la clase al crear.")
@RestController
@RequestMapping("/api/armas")
public class ArmaCrudController {

    private final ArmaService armaService;

    public ArmaCrudController(ArmaService armaService) {
        this.armaService = armaService;
    }

    /** GET /api/armas y GET /api/armas?bando=CT */
    @Operation(summary = "Listar armas guardadas",
            description = "Devuelve las armas de H2. Es polimórfico: cada una vuelve con su clase en el campo tipo. Filtro opcional ?bando=T, CT o CT/T.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de armas"),
        @ApiResponse(responseCode = "400", description = "BandoInvalidoException: el bando del filtro no es T, CT ni CT/T",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping
    public List<ArmaGuardadaResponse> listar(@RequestParam(required = false) String bando) {
        List<Arma> armas = (bando == null) ? armaService.listar() : armaService.listarPorBando(bando);
        return armas.stream().map(ArmaGuardadaResponse::de).toList();
    }

    @Operation(summary = "Obtener un arma por id",
            description = "Devuelve el arma con ese id (lo asigna H2 al crear).")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "El arma"),
        @ApiResponse(responseCode = "404", description = "ArmaNoEncontradaException: no hay un arma con ese id",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id:\\d+}")
    public ArmaGuardadaResponse obtener(@PathVariable Long id) {
        return ArmaGuardadaResponse.de(armaService.obtener(id));
    }

    /** 201 Created con la cabecera Location apuntando al arma nueva. */
    @Operation(summary = "Crear un arma",
            description = "Cuerpo obligatorio: tipo (rifle, pistola, granada-humo, granada-he), nombre, precio y bando. Opcionales: skin, statTrak, cargadorCapacidad, danoBase, atributoEspecial, tiempoRetardo, duracionHumo, radioExplosion, danoMaximo, empujeMaximo; x, y, z dejan el arma tirada en el mapa. Responde 201 con el id nuevo y la cabecera Location.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Arma creada"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos o regla del catálogo rota: CampoObligatorioException, PrecioNegativoException, CargadorInvalidoException, DanoInvalidoException, TiempoRetardoInvalidoException, DuracionHumoInvalidaException, RadioExplosionInvalidoException, EmpujeInvalidoException, CoordenadaInvalidaException, TipoArmaNoSoportadoException, BandoInvalidoException, PrecioExcedidoException, SkinNoPermitidaException o JsonInvalido",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "409", description = "ArmaDuplicadaException: ya existe un arma con ese nombre",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<ArmaGuardadaResponse> crear(@RequestBody ArmaRequest request) {
        Arma creada = armaService.crear(request.aDominio());
        return ResponseEntity
                .created(URI.create("/api/armas/" + creada.getId()))
                .body(ArmaGuardadaResponse.de(creada));
    }

    @Operation(summary = "Reemplazar un arma",
            description = "Reemplaza el arma entera (mismo cuerpo que el POST). El tipo no puede cambiar. Los campos opcionales omitidos toman el valor estándar del tipo.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Arma actualizada"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos o regla del catálogo rota: CampoObligatorioException, PrecioNegativoException, CargadorInvalidoException, DanoInvalidoException, TiempoRetardoInvalidoException, DuracionHumoInvalidaException, RadioExplosionInvalidoException, EmpujeInvalidoException, CoordenadaInvalidaException, TipoArmaNoSoportadoException, BandoInvalidoException, PrecioExcedidoException, SkinNoPermitidaException o JsonInvalido o CambioDeTipoException",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "ArmaNoEncontradaException",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "409", description = "ArmaDuplicadaException: el nombre lo usa otra arma",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/{id:\\d+}")
    public ArmaGuardadaResponse actualizar(@PathVariable Long id, @RequestBody ArmaRequest request) {
        return ArmaGuardadaResponse.de(armaService.actualizar(id, request.aDominio()));
    }

    /** 204 No Content si se borró. */
    @Operation(summary = "Eliminar un arma",
            description = "Borra el arma de H2 (de la tabla armas y de su tabla hija).")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Arma eliminada, sin cuerpo"),
        @ApiResponse(responseCode = "404", description = "ArmaNoEncontradaException",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{id:\\d+}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        armaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
