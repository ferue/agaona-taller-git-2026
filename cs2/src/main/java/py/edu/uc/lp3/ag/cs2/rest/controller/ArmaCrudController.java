package py.edu.uc.lp3.ag.cs2.rest.controller;

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
@RestController
@RequestMapping("/api/armas")
public class ArmaCrudController {

    private final ArmaService armaService;

    public ArmaCrudController(ArmaService armaService) {
        this.armaService = armaService;
    }

    /** GET /api/armas y GET /api/armas?bando=CT */
    @GetMapping
    public List<ArmaGuardadaResponse> listar(@RequestParam(required = false) String bando) {
        List<Arma> armas = (bando == null) ? armaService.listar() : armaService.listarPorBando(bando);
        return armas.stream().map(ArmaGuardadaResponse::de).toList();
    }

    @GetMapping("/{id:\\d+}")
    public ArmaGuardadaResponse obtener(@PathVariable Long id) {
        return ArmaGuardadaResponse.de(armaService.obtener(id));
    }

    /** 201 Created con la cabecera Location apuntando al arma nueva. */
    @PostMapping
    public ResponseEntity<ArmaGuardadaResponse> crear(@RequestBody ArmaRequest request) {
        Arma creada = armaService.crear(request.aDominio());
        return ResponseEntity
                .created(URI.create("/api/armas/" + creada.getId()))
                .body(ArmaGuardadaResponse.de(creada));
    }

    @PutMapping("/{id:\\d+}")
    public ArmaGuardadaResponse actualizar(@PathVariable Long id, @RequestBody ArmaRequest request) {
        return ArmaGuardadaResponse.de(armaService.actualizar(id, request.aDominio()));
    }

    /** 204 No Content si se borró. */
    @DeleteMapping("/{id:\\d+}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        armaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
