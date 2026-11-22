package py.edu.uc.lp3.ag.cs2.service;

import org.springframework.stereotype.Service;
import py.edu.uc.lp3.ag.cs2.domain.Arma;
import py.edu.uc.lp3.ag.cs2.domain.ResultadoCompra;
import py.edu.uc.lp3.ag.cs2.domain.Tienda;
import py.edu.uc.lp3.ag.cs2.exception.noencontrado.TiendaNoEncontradaException;
import py.edu.uc.lp3.ag.cs2.exception.validacion.SaldoInvalidoException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Tiendas de compra de POO-06. Viven en memoria (no se guardan en H2): este
 * service las guarda en un mapa por id ("tienda-0", "tienda-1"...).
 *
 * - Nombre obligatorio → CampoObligatorioException; saldo >= 0 → SaldoInvalidoException.
 * - Las armas que se agregan pasan por las invariantes de ArmaService.
 * - Id inexistente → TiendaNoEncontradaException.
 * - Regla de compra: la posición del catálogo existe y el saldo alcanza; si
 *   no, devuelve INDICE_INVALIDO o SALDO_INSUFICIENTE sin cambiar nada.
 */
@Service
public class TiendaService {

    private final Map<String, Tienda> tiendas = new ConcurrentHashMap<>();
    private final AtomicInteger contador = new AtomicInteger();
    private final ArmaService armaService;

    public TiendaService(ArmaService armaService) {
        this.armaService = armaService;
    }

    public TiendaRegistrada crear(String nombre, int saldo) {
        Validaciones.requerirTexto(nombre, "nombre");
        if (saldo < 0) {
            throw new SaldoInvalidoException(saldo);
        }
        Tienda tienda = new Tienda(nombre, saldo);
        String id = "tienda-" + contador.getAndIncrement();
        tiendas.put(id, tienda);
        return new TiendaRegistrada(id, tienda);
    }

    public Tienda buscar(String id) {
        Tienda tienda = tiendas.get(id);
        if (tienda == null) {
            throw new TiendaNoEncontradaException(id);
        }
        return tienda;
    }

    /** Crea el arma con FabricaArmas, controla sus invariantes y la suma al catálogo. */
    public Arma agregarArma(String id, String tipo, String nombre, int precio, String bando,
                            int cargadorCapacidad, int danoBase, boolean atributoEspecial) {
        Tienda tienda = buscar(id);
        Arma arma = FabricaArmas.crear(tipo, nombre, precio, bando, cargadorCapacidad, danoBase, atributoEspecial);
        armaService.validar(arma);
        tienda.agregarArma(arma);
        return arma;
    }

    /** Regla de compra: solo se cobra si el arma existe y el saldo alcanza. */
    public ResultadoCompra comprar(String id, int indice) {
        Tienda tienda = buscar(id);
        if (indice < 0 || indice >= tienda.getCantidadArmas()) {
            return ResultadoCompra.INDICE_INVALIDO;
        }
        Arma arma = tienda.getArmasDisponibles().get(indice);
        if (tienda.getSaldo() < arma.getPrecio()) {
            return ResultadoCompra.SALDO_INSUFICIENTE;
        }
        tienda.registrarCompra(indice);
        return ResultadoCompra.EXITOSA;
    }

    /** Una tienda recién creada junto con el id para buscarla. */
    public record TiendaRegistrada(String id, Tienda tienda) {
    }
}
