package py.edu.uc.lp3.ag.cs2.rest.response;

import py.edu.uc.lp3.ag.cs2.client.ObjetoExternoDto;

import java.util.Map;

/** Vista JSON propia de un objeto de restful-api.dev (campos en castellano). */
public record ObjetoExternoResponse(String id, String nombre, Map<String, Object> datos) {

    public static ObjetoExternoResponse de(ObjetoExternoDto dto) {
        return new ObjetoExternoResponse(dto.id(), dto.name(), dto.data());
    }
}
