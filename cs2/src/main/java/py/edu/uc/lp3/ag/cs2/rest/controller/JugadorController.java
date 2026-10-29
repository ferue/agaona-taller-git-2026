package py.edu.uc.lp3.ag.cs2.rest.controller;

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
@RestController
@RequestMapping("/api/jugadores")
public class JugadorController {

    private final JugadorService jugadorService;

    public JugadorController(JugadorService jugadorService) {
        this.jugadorService = jugadorService;
    }

    @GetMapping
    public List<JugadorResponse> listar() {
        return jugadorService.listar().stream().map(JugadorResponse::de).toList();
    }

    @GetMapping("/{id:\\d+}")
    public JugadorResponse obtener(@PathVariable Long id) {
        return JugadorResponse.de(jugadorService.obtener(id));
    }

    @PostMapping
    public ResponseEntity<JugadorResponse> crear(@RequestBody JugadorRequest request) {
        Jugador creado = jugadorService.crear(request.aDominio());
        return ResponseEntity
                .created(URI.create("/api/jugadores/" + creado.getId()))
                .body(JugadorResponse.de(creado));
    }
}
