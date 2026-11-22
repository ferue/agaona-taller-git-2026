package py.edu.uc.lp3.ag.cs2.service;

import org.springframework.stereotype.Service;
import py.edu.uc.lp3.ag.cs2.domain.Arma;
import py.edu.uc.lp3.ag.cs2.domain.ArmaDeFuego;
import py.edu.uc.lp3.ag.cs2.domain.GranadaHumo;
import py.edu.uc.lp3.ag.cs2.domain.Pistola;
import py.edu.uc.lp3.ag.cs2.domain.Rifle;
import py.edu.uc.lp3.ag.cs2.exception.negocio.ArmaNoDisparaException;
import py.edu.uc.lp3.ag.cs2.exception.validacion.CantidadDisparosInvalidaException;

import java.util.ArrayList;
import java.util.List;

/**
 * Lógica de los endpoints de demostración de POO-06 (/api/armas/probar,
 * /rafaga y /polimorfismo). Las armas viven en memoria, no se guardan en H2,
 * pero igual pasan por las invariantes de ArmaService.
 *
 * - Ráfaga de 1 a 100 disparos → CantidadDisparosInvalidaException.
 * - Solo dispara un arma de fuego → ArmaNoDisparaException.
 */
@Service
public class DemostracionArmasService {

    public static final int MAX_DISPAROS = 100;

    private final ArmaService armaService;

    public DemostracionArmasService(ArmaService armaService) {
        this.armaService = armaService;
    }

    /**
     * Construye un arma con su tipo y ejecuta su acción principal. Si llega
     * "skin" se aplica con aplicarSkin(String); si además llega "statTrak" se
     * usa la sobrecarga aplicarSkin(String, boolean).
     */
    public ResultadoPrueba probar(String tipo, String nombre, int precio, String bando, String skin, Boolean statTrak) {
        Arma arma = FabricaArmas.crear(tipo, nombre, precio, bando);
        if (skin != null) {
            if (statTrak == null) {
                arma.aplicarSkin(skin);
            } else {
                arma.aplicarSkin(skin, statTrak);
            }
        }
        armaService.validar(arma);
        return new ResultadoPrueba(arma, arma.ejecutarAccionPrincipal());
    }

    /**
     * Dispara N veces y luego recarga: muestra cómo cambia el estado
     * (munición) y que nunca baja de 0.
     */
    public ResultadoRafaga rafaga(String tipo, int disparos) {
        if (disparos < 1 || disparos > MAX_DISPAROS) {
            throw new CantidadDisparosInvalidaException(disparos, MAX_DISPAROS);
        }
        Arma arma = FabricaArmas.crear(tipo, String.valueOf(tipo).toUpperCase(), 0, "T");
        armaService.validar(arma);
        if (!(arma instanceof ArmaDeFuego armaDeFuego)) {
            throw new ArmaNoDisparaException(tipo);
        }

        List<String> resultados = new ArrayList<>();
        for (int i = 0; i < disparos; i++) {
            resultados.add(armaDeFuego.disparar());
        }
        int municionAntesDeRecargar = armaDeFuego.getMunicionRestante();
        String recarga = armaDeFuego.recargar();
        return new ResultadoRafaga(armaDeFuego, resultados, municionAntesDeRecargar, recarga,
                armaDeFuego.getMunicionRestante());
    }

    /**
     * Recorre una List<Arma> y le manda los mismos mensajes a cada elemento:
     * cada clase hija responde con su propia sobrescritura.
     */
    public List<ResultadoPolimorfismo> polimorfismo() {
        List<Arma> arsenal = List.of(
                new Rifle("M4A4", 3100, "CT", 30, 33, true),
                new Pistola("Glock-18", 200, "T", 20, 30, false),
                new GranadaHumo("Smoke", 300, "CT/T"));
        List<ResultadoPolimorfismo> resultados = new ArrayList<>();
        for (Arma arma : arsenal) {
            armaService.validar(arma);
            resultados.add(new ResultadoPolimorfismo(arma.getClass().getSimpleName(), arma.obtenerDetalles(),
                    arma.ejecutarAccionPrincipal(), arma.emitirSonidoInspeccion()));
        }
        return resultados;
    }

    /** El arma construida y lo que respondió su acción principal. */
    public record ResultadoPrueba(Arma arma, String resultadoAccion) {
    }

    /** Lo que respondió cada clase hija a los mismos mensajes. */
    public record ResultadoPolimorfismo(String tipo, String detalles, String accionPrincipal,
                                        String sonidoInspeccion) {
    }

    /** Lo que pasó en una ráfaga: los disparos, la munición antes de recargar y después. */
    public record ResultadoRafaga(ArmaDeFuego arma, List<String> disparos, int municionAntesDeRecargar,
                                  String recarga, int municionFinal) {
    }
}
