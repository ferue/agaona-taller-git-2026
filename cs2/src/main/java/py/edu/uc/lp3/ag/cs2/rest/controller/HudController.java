package py.edu.uc.lp3.ag.cs2.rest.controller;

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
@RestController
public class HudController {

    private final HudService hudService;

    public HudController(HudService hudService) {
        this.hudService = hudService;
    }

    @GetMapping("/api/hud")
    public List<HudElementoResponse> hud() {
        List<UICapable> elementos = hudService.elementos();
        return elementos.stream().map(HudElementoResponse::de).toList();
    }
}
