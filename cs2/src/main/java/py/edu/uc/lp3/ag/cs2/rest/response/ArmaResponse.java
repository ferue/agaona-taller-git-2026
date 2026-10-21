package py.edu.uc.lp3.ag.cs2.rest.response;

import py.edu.uc.lp3.ag.cs2.domain.Arma;

/**
 * Vista JSON de un arma. Se arma solo con métodos públicos del dominio:
 * el controlador no conoce la clase concreta (polimorfismo).
 */
public record ArmaResponse(
        String tipo,
        String nombre,
        int precio,
        String bando,
        String skin,
        String detalles,
        String iconoHud
) {
    public static ArmaResponse de(Arma arma) {
        return new ArmaResponse(
                arma.getClass().getSimpleName(),
                arma.getNombre(),
                arma.getPrecio(),
                arma.getBando(),
                arma.getSkin(),
                arma.obtenerDetalles(),
                arma.obtenerIconoHud());
    }
}
