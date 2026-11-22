package py.edu.uc.lp3.ag.cs2.rest.response;

import py.edu.uc.lp3.ag.cs2.domain.JugadaAtaque;

import java.time.LocalDateTime;

/** Vista JSON de una jugada guardada del escenario del POO-09. */
public record JugadaResponse(
        Long id,
        LocalDateTime fecha,
        Long granadaId,
        String granada,
        PosicionResponse centro,
        String resultadoA2,
        String resultadoA3,
        int jugadoresDanados,
        int jugadoresEliminados,
        int armasDesplazadas,
        String resumen) {

    public static JugadaResponse de(JugadaAtaque j) {
        return new JugadaResponse(j.getId(), j.getFecha(), j.getGranadaId(), j.getGranada(),
                PosicionResponse.de(j.getCentro()), j.getResultadoA2(), j.getResultadoA3(),
                j.getJugadoresDanados(), j.getJugadoresEliminados(), j.getArmasDesplazadas(), j.getResumen());
    }
}
