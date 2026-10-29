package py.edu.uc.lp3.ag.cs2.rest.request;

import py.edu.uc.lp3.ag.cs2.domain.Arma;
import py.edu.uc.lp3.ag.cs2.domain.ArmaDeFuego;
import py.edu.uc.lp3.ag.cs2.domain.GranadaHE;
import py.edu.uc.lp3.ag.cs2.domain.GranadaHumo;
import py.edu.uc.lp3.ag.cs2.exception.validacion.CampoObligatorioException;
import py.edu.uc.lp3.ag.cs2.service.FabricaArmas;

/**
 * Cuerpo JSON de POST y PUT /api/armas.
 *
 * Obligatorios: tipo, nombre, precio, bando. El resto es opcional y, si falta,
 * se usa el valor estándar del tipo:
 * - skin / statTrak: se aplican con aplicarSkin(...).
 * - cargadorCapacidad / danoBase / atributoEspecial: armas de fuego
 *   (atributoEspecial = mira en rifles, silenciador en pistolas).
 * - tiempoRetardo: granadas. duracionHumo: granada de humo.
 *   radioExplosion / danoMaximo / empujeMaximo: granada HE.
 * - x / y / z: si llegan, el arma queda tirada en ese punto del mapa (van las tres juntas).
 */
public record ArmaRequest(
        String tipo,
        String nombre,
        Integer precio,
        String bando,
        String skin,
        Boolean statTrak,
        Integer cargadorCapacidad,
        Integer danoBase,
        Boolean atributoEspecial,
        Float tiempoRetardo,
        Float duracionHumo,
        Float radioExplosion,
        Integer danoMaximo,
        Float empujeMaximo,
        Double x,
        Double y,
        Double z
) {

    /** Convierte el JSON en un objeto del dominio; los constructores validan las invariantes. */
    public Arma aDominio() {
        if (precio == null) {
            throw new CampoObligatorioException("precio");
        }
        Arma estandar = FabricaArmas.crear(tipo, nombre, precio, bando);
        Arma arma = conAtributosPropios(estandar);

        if (skin != null) {
            if (statTrak == null) {
                arma.aplicarSkin(skin);
            } else {
                arma.aplicarSkin(skin, statTrak);
            }
        }
        PosicionRequest posicion = new PosicionRequest(x, y, z);
        if (posicion.algunaCoordenada()) {
            arma.ubicarEn(posicion.aDominio());
        }
        return arma;
    }

    /** Si llegó algún atributo propio del tipo, se vuelve a construir el arma con esos valores. */
    private Arma conAtributosPropios(Arma estandar) {
        return switch (estandar) {
            case ArmaDeFuego f when cargadorCapacidad != null || danoBase != null || atributoEspecial != null ->
                    FabricaArmas.crear(tipo, nombre, precio, bando,
                            valorO(cargadorCapacidad, f.getCargadorCapacidad()),
                            valorO(danoBase, f.getDanoBase()),
                            Boolean.TRUE.equals(atributoEspecial));
            case GranadaHumo h when tiempoRetardo != null || duracionHumo != null ->
                    new GranadaHumo(nombre, precio, bando,
                            valorO(tiempoRetardo, h.getTiempoRetardo()),
                            valorO(duracionHumo, h.getDuracionHumo()));
            case GranadaHE he when tiempoRetardo != null || radioExplosion != null
                    || danoMaximo != null || empujeMaximo != null ->
                    new GranadaHE(nombre, precio, bando,
                            valorO(tiempoRetardo, he.getTiempoRetardo()),
                            valorO(radioExplosion, he.getRadioExplosion()),
                            valorO(danoMaximo, he.getDanoMaximo()),
                            valorO(empujeMaximo, he.getEmpujeMaximo()));
            default -> estandar;
        };
    }

    private static int valorO(Integer valor, int estandar) {
        return valor != null ? valor : estandar;
    }

    private static float valorO(Float valor, float estandar) {
        return valor != null ? valor : estandar;
    }
}
