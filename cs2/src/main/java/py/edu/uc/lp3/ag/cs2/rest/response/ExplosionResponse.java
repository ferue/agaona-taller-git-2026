package py.edu.uc.lp3.ag.cs2.rest.response;

import py.edu.uc.lp3.ag.cs2.service.ResultadoExplosion;

import java.util.List;

/**
 * Vista JSON del resultado de POST /api/explosiones y de
 * POST /api/explosiones/validar (en ese caso simulacion = true y no se guardó nada).
 */
public record ExplosionResponse(
        Long granadaId,
        String granada,
        PosicionResponse centro,
        float radio,
        boolean simulacion,
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
                        i.vidaRestante(),
                        i.vidaRestante() > 0 ? "VIVO" : "ELIMINADO"))
                .toList();
        List<ArmaDesplazada> armas = r.armasDesplazadas().stream()
                .map(d -> new ArmaDesplazada(d.arma().getId(), d.arma().getNombre(),
                        d.arma().getClass().getSimpleName(),
                        PosicionResponse.de(d.desde()), PosicionResponse.de(d.hasta()),
                        PosicionResponse.redondear(d.metros())))
                .toList();
        long eliminados = jugadores.stream().filter(j -> j.estado().equals("ELIMINADO")).count();
        String resumen = r.simulacion()
                ? "Validación sin detonar: " + r.granada().getNombre() + " dañaría a " + jugadores.size()
                        + " jugador(es) (" + eliminados + " eliminado(s)) y desplazaría " + armas.size()
                        + " arma(s). No se guardó nada."
                : "¡BOOM! " + r.granada().getNombre() + " dañó a " + jugadores.size() + " jugador(es) ("
                        + eliminados + " eliminado(s)) y desplazó " + armas.size() + " arma(s).";
        return new ExplosionResponse(
                r.granada().getId(),
                r.granada().getNombre(),
                PosicionResponse.de(r.centro()),
                r.granada().getRadioExplosion(),
                r.simulacion(),
                resumen,
                jugadores,
                armas);
    }
}
