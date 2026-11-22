package py.edu.uc.lp3.ag.cs2.rest.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "Inicio", description = "Estado del servicio y lista de endpoints.")
@RestController
public class IndexController {

    @Operation(summary = "Servicio vivo",
            description = "Devuelve estado OK y la lista de endpoints.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Servicio en línea")
    })
    @GetMapping("/")
    public Map<String, Object> index() {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("servicio", "Servicio API REST - CS2 Modelado (CYT646)");
        response.put("estado", "OK");
        response.put("endpoints", List.of(
                "GET  /api/armas/probar?tipo=rifle&nombre=AK-47&precio=2700&bando=T&skin=Redline&statTrak=true",
                "GET  /api/armas/rafaga?tipo=pistola&disparos=5",
                "GET  /api/armas/polimorfismo",
                "GET    /api/armas            (CRUD: listar, opcional ?bando=CT)",
                "GET    /api/armas/{id}       (CRUD: obtener)",
                "POST   /api/armas            (CRUD: crear, cuerpo JSON)",
                "PUT    /api/armas/{id}       (CRUD: actualizar, cuerpo JSON)",
                "DELETE /api/armas/{id}       (CRUD: eliminar)",
                "GET    /api/jugadores        (listar jugadores)",
                "GET    /api/jugadores/{id}   (obtener jugador)",
                "POST   /api/jugadores        (crear jugador, cuerpo JSON)",
                "POST   /api/explosiones      (explota una granada HE: {granadaId, x, y, z})",
                "POST   /api/explosiones/validar (POO-09: aplica las dos reglas sin detonar ni guardar nada)",
                "GET    /api/hud              (protocolo UICapable: armas y jugadores de la base)",
                "GET    /api/objetos-externos      (consulta https://api.restful-api.dev con WebClient, opcional ?nombre=)",
                "GET    /api/objetos-externos/{id} (un objeto de restful-api.dev)",
                "POST /tienda?nombre=CT&saldo=5000",
                "POST /tienda/{id}/agregar-arma?tipo=rifle&nombre=M4A1-S&precio=2900&bando=CT",
                "GET  /tienda/{id}/catalogo",
                "POST /tienda/{id}/comprar?indiceArma=0",
                "GET  /ui/{nombre}   (ak47, usp, smoke)"));
        return response;
    }
}
