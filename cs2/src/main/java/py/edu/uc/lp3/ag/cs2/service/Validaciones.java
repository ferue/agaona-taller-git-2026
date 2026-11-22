package py.edu.uc.lp3.ag.cs2.service;

import py.edu.uc.lp3.ag.cs2.domain.Posicion;
import py.edu.uc.lp3.ag.cs2.exception.validacion.CampoObligatorioException;
import py.edu.uc.lp3.ag.cs2.exception.validacion.CoordenadaInvalidaException;

/**
 * Validaciones comunes que reusan varios services (ArmaService,
 * JugadorService, ExplosionService y TiendaService).
 */
final class Validaciones {

    private Validaciones() {
    }

    /** El texto es obligatorio: no puede ser null ni quedar vacío. */
    static void requerirTexto(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new CampoObligatorioException(campo);
        }
    }

    /** La posición es obligatoria y sus tres coordenadas son números finitos. */
    static void requerirPosicion(Posicion posicion, String campo) {
        if (posicion == null) {
            throw new CampoObligatorioException(campo);
        }
        requerirFinita("x", posicion.getX());
        requerirFinita("y", posicion.getY());
        requerirFinita("z", posicion.getZ());
    }

    private static void requerirFinita(String eje, double valor) {
        if (!Double.isFinite(valor)) {
            throw new CoordenadaInvalidaException(eje, valor);
        }
    }
}
