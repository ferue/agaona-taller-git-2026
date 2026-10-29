package py.edu.uc.lp3.ag.cs2.rest.response;

import py.edu.uc.lp3.ag.cs2.domain.Arma;

/**
 * Vista JSON de un arma guardada en la base (incluye el id).
 * Igual que ArmaResponse, se arma solo con métodos públicos de Arma.
 * posicion es null si el arma está en el catálogo y no tirada en el mapa.
 */
public record ArmaGuardadaResponse(
        Long id,
        String tipo,
        String nombre,
        int precio,
        String bando,
        String skin,
        String detalles,
        String iconoHud,
        PosicionResponse posicion
) {
    public static ArmaGuardadaResponse de(Arma arma) {
        return new ArmaGuardadaResponse(
                arma.getId(),
                arma.getClass().getSimpleName(),
                arma.getNombre(),
                arma.getPrecio(),
                arma.getBando(),
                arma.getSkin(),
                arma.obtenerDetalles(),
                arma.obtenerIconoHud(),
                PosicionResponse.de(arma.getPosicion()));
    }
}
