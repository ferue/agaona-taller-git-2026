package py.edu.uc.lp3.ag.cs2.service;

import py.edu.uc.lp3.ag.cs2.domain.GranadaHE;
import py.edu.uc.lp3.ag.cs2.domain.Posicion;

import java.util.List;

/**
 * Lo que devuelve ExplosionService: qué granada explotó, dónde, a qué
 * jugadores dañó (regla 1) y qué armas empujó (regla 2).
 */
public record ResultadoExplosion(
        GranadaHE granada,
        Posicion centro,
        List<GranadaHE.Impacto> jugadoresAfectados,
        List<GranadaHE.Desplazamiento> armasDesplazadas
) {
}
