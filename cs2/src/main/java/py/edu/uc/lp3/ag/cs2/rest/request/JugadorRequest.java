package py.edu.uc.lp3.ag.cs2.rest.request;

import py.edu.uc.lp3.ag.cs2.domain.Jugador;

/**
 * Cuerpo JSON de POST /api/jugadores.
 * Obligatorios: nombre, bando, x, y, z. Opcionales: vida (100) y tieneChaleco (false).
 */
public record JugadorRequest(
        String nombre,
        String bando,
        Integer vida,
        Boolean tieneChaleco,
        Double x,
        Double y,
        Double z
) {
    public Jugador aDominio() {
        return new Jugador(
                nombre,
                bando,
                vida != null ? vida : Jugador.VIDA_MAXIMA,
                Boolean.TRUE.equals(tieneChaleco),
                new PosicionRequest(x, y, z).aDominio());
    }
}
