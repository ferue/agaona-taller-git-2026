package py.edu.uc.lp3.ag.cs2.rest.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import py.edu.uc.lp3.ag.cs2.domain.UICapable;
import py.edu.uc.lp3.ag.cs2.rest.response.HudElementoResponse;
import py.edu.uc.lp3.ag.cs2.service.HudService;

import java.util.List;

/**
 * Endpoint del protocolo de dominio: GET /api/hud.
 * Recibe una lista de UICapable con armas y jugadores de la base y les manda
 * los mismos mensajes (obtenerIconoHud, etiquetaHud, emitirSonidoInspeccion).
 * Cada clase responde a su manera: polimorfismo a través de la interfaz.
 */
@Tag(name = "HUD - interfaz UICapable", description = "Protocolo de dominio UICapable, implementado por Arma y por Jugador.")
@RestController
public class HudController {

    private final HudService hudService;

    public HudController(HudService hudService) {
        this.hudService = hudService;
    }

    @Operation(summary = "Ver el HUD",
            description = "Lista armas y jugadores de H2 tratados como UICapable: icono, etiqueta y sonido de cada uno.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Elementos del HUD")
    })
    @GetMapping("/api/hud")
    public List<HudElementoResponse> hud() {
        List<UICapable> elementos = hudService.elementos();
        return elementos.stream().map(HudElementoResponse::de).toList();
    }
}
