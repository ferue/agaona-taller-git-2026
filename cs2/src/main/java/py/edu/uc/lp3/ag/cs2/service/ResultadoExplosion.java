package py.edu.uc.lp3.ag.cs2.service;

import py.edu.uc.lp3.ag.cs2.domain.Arma;
import py.edu.uc.lp3.ag.cs2.domain.GranadaHE;
import py.edu.uc.lp3.ag.cs2.domain.Jugador;
import py.edu.uc.lp3.ag.cs2.domain.Posicion;

import java.util.List;

/**
 * Lo que devuelve ExplosionService: qué granada explotó, dónde, a qué
 * jugadores dañó (regla 1) y qué armas empujó (regla 2).
 */
public record ResultadoExplosion(
        GranadaHE granada,
        Posicion centro,
        List<Impacto> jugadoresAfectados,
        List<Desplazamiento> armasDesplazadas
) {

    /** Resultado de la regla 1 sobre un jugador. */
    public record Impacto(Jugador jugador, double distancia, int danoCalculado, int danoRecibido) {
    }

    /** Resultado de la regla 2 sobre un arma. */
    public record Desplazamiento(Arma arma, Posicion desde, Posicion hasta, double metros) {
    }
}
