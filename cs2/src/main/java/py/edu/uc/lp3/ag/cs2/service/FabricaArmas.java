package py.edu.uc.lp3.ag.cs2.service;

import py.edu.uc.lp3.ag.cs2.domain.Arma;
import py.edu.uc.lp3.ag.cs2.domain.GranadaHumo;
import py.edu.uc.lp3.ag.cs2.domain.Pistola;
import py.edu.uc.lp3.ag.cs2.domain.Rifle;

import java.util.Locale;

/**
 * Crea armas a partir del "tipo" recibido en la API. Centraliza la
 * traducción texto → clase concreta para que los controladores no repitan
 * cadenas de if/else.
 */
public final class FabricaArmas {

    public static final String TIPOS_VALIDOS = "rifle, pistola, granada-humo";

    private FabricaArmas() {
    }

    /** Sobrecarga 1: arma con los valores estándar de su tipo. */
    public static Arma crear(String tipo, String nombre, int precio, String bando) {
        return switch (normalizar(tipo)) {
            case "rifle" -> new Rifle(nombre, precio, bando);
            case "pistola" -> new Pistola(nombre, precio, bando);
            case "granada-humo" -> new GranadaHumo(nombre, precio, bando);
            default -> throw tipoNoSoportado(tipo);
        };
    }

    /**
     * Sobrecarga 2: arma de fuego con cargador, daño y atributo especial
     * (mira para rifles, silenciador para pistolas). Las granadas ignoran
     * cargador, daño y atributo especial y usan sus valores estándar.
     */
    public static Arma crear(String tipo, String nombre, int precio, String bando,
                             int cargadorCapacidad, int danoBase, boolean atributoEspecial) {
        return switch (normalizar(tipo)) {
            case "rifle" -> new Rifle(nombre, precio, bando, cargadorCapacidad, danoBase, atributoEspecial);
            case "pistola" -> new Pistola(nombre, precio, bando, cargadorCapacidad, danoBase, atributoEspecial);
            case "granada-humo" -> new GranadaHumo(nombre, precio, bando);
            default -> throw tipoNoSoportado(tipo);
        };
    }

    private static String normalizar(String tipo) {
        if (tipo == null) {
            throw tipoNoSoportado(null);
        }
        String t = tipo.trim().toLowerCase(Locale.ROOT);
        return switch (t) {
            case "humo", "granada", "smoke" -> "granada-humo";
            default -> t;
        };
    }

    private static IllegalArgumentException tipoNoSoportado(String tipo) {
        return new IllegalArgumentException("Tipo de arma no soportado: '" + tipo + "'. Valores válidos: " + TIPOS_VALIDOS);
    }
}
