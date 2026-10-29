package py.edu.uc.lp3.ag.cs2.service;

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
    /** Regla 1: daño que recibió un jugador. */
    public record Impacto(Jugador jugador, double distancia, int danoCalculado, int danoRecibido) {
    }

    /** Regla 2: un arma del mapa que salió volando. */
    public record Desplazamiento(Long armaId, String nombre, String tipo,
                                 Posicion desde, Posicion hasta, double metros) {
    }
}
