package py.edu.uc.lp3.ag.cs2.rest.response;

import py.edu.uc.lp3.ag.cs2.domain.Jugador;

/** Vista JSON de un jugador. */
public record JugadorResponse(
        Long id,
        String nombre,
        String bando,
        int vida,
        boolean tieneChaleco,
        String estado,
        PosicionResponse posicion
) {
    public static JugadorResponse de(Jugador j) {
        return new JugadorResponse(
                j.getId(),
                j.getNombre(),
                j.getBando(),
                j.getVida(),
                j.isTieneChaleco(),
                j.estaVivo() ? "VIVO" : "ELIMINADO",
                PosicionResponse.de(j.getPosicion()));
    }
}
