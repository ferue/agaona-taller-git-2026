package py.edu.uc.lp3.ag.cs2.rest.controller;

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
@RestController
@RequestMapping("/api/explosiones")
public class ExplosionController {

    private final ExplosionService explosionService;

    public ExplosionController(ExplosionService explosionService) {
        this.explosionService = explosionService;
    }

    @PostMapping
    public ExplosionResponse explotar(@RequestBody ExplosionRequest request) {
        return ExplosionResponse.de(
                explosionService.explotar(request.granadaIdObligatorio(), request.centro()));
    }
}
