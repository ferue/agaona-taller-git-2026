package py.edu.uc.lp3.ag.cs2.rest.response;

import py.edu.uc.lp3.ag.cs2.service.ResultadoExplosion;

import java.util.List;

/** Vista JSON del resultado de POST /api/explosiones. */
public record ExplosionResponse(
        Long granadaId,
        String granada,
        PosicionResponse centro,
        float radio,
        String resumen,
        List<JugadorAfectado> jugadoresAfectados,
        List<ArmaDesplazada> armasDesplazadas
) {
    public record JugadorAfectado(Long id, String nombre, String bando, double distancia,
                                  int danoCalculado, int danoRecibido, int vidaRestante, String estado) {
    }

    public record ArmaDesplazada(Long id, String nombre, String tipo, PosicionResponse desde,
                                 PosicionResponse hasta, double metrosVolados) {
    }

    public static ExplosionResponse de(ResultadoExplosion r) {
        List<JugadorAfectado> jugadores = r.jugadoresAfectados().stream()
                .map(i -> new JugadorAfectado(
                        i.jugador().getId(),
                        i.jugador().getNombre(),
                        i.jugador().getBando(),
                        PosicionResponse.redondear(i.distancia()),
                        i.danoCalculado(),
                        i.danoRecibido(),
                        i.jugador().getVida(),
                        i.jugador().estaVivo() ? "VIVO" : "ELIMINADO"))
                .toList();
        List<ArmaDesplazada> armas = r.armasDesplazadas().stream()
                .map(d -> new ArmaDesplazada(d.armaId(), d.nombre(), d.tipo(),
                        PosicionResponse.de(d.desde()), PosicionResponse.de(d.hasta()),
                        PosicionResponse.redondear(d.metros())))
                .toList();
        long eliminados = jugadores.stream().filter(j -> j.estado().equals("ELIMINADO")).count();
        String resumen = "¡BOOM! " + r.granada().getNombre() + " dañó a " + jugadores.size() + " jugador(es) ("
                + eliminados + " eliminado(s)) y desplazó " + armas.size() + " arma(s).";
        return new ExplosionResponse(
                r.granada().getId(),
                r.granada().getNombre(),
                PosicionResponse.de(r.centro()),
                r.granada().getRadioExplosion(),
                resumen,
                jugadores,
                armas);
    }
}
