package py.edu.uc.lp3.ag.cs2.client;

import java.util.Map;

/**
 * Un objeto tal como lo devuelve https://api.restful-api.dev/objects.
 * "data" es un mapa porque sus claves cambian de un objeto a otro
 * (color, capacity, CPU model...) y a veces viene null.
 */
public record ObjetoExternoDto(String id, String name, Map<String, Object> data) {
}
