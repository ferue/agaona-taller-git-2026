package py.edu.uc.lp3.ag.cs2.service;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import py.edu.uc.lp3.ag.cs2.domain.Arma;
import py.edu.uc.lp3.ag.cs2.exception.negocio.ArmaDuplicadaException;
import py.edu.uc.lp3.ag.cs2.exception.negocio.BandoInvalidoException;
import py.edu.uc.lp3.ag.cs2.exception.negocio.CambioDeTipoException;
import py.edu.uc.lp3.ag.cs2.exception.negocio.PrecioExcedidoException;
import py.edu.uc.lp3.ag.cs2.exception.negocio.SkinNoPermitidaException;
import py.edu.uc.lp3.ag.cs2.exception.noencontrado.ArmaNoEncontradaException;
import py.edu.uc.lp3.ag.cs2.repository.ArmaRepository;

import java.util.List;

/**
 * Capa de negocio del CRUD de armas.
 *
 * Las invariantes de cada objeto (precio >= 0, nombre obligatorio, cargador > 0...)
 * las garantiza el dominio en sus constructores. Acá se validan las reglas que
 * dependen del catálogo completo o del juego; cada una lanza su propia
 * excepción de la jerarquía (exception/):
 *
 * - Nombre único en el catálogo, sin distinguir mayúsculas → ArmaDuplicadaException.
 * - Bando T, CT o CT/T → BandoInvalidoException.
 * - Precio de hasta $16.000, el dinero máximo de una partida competitiva → PrecioExcedidoException.
 * - Las granadas no admiten skins → SkinNoPermitidaException.
 * - Un PUT no puede cambiar el tipo del arma → CambioDeTipoException.
 * - Id inexistente → ArmaNoEncontradaException.
 */
@Service
@Transactional(readOnly = true)
public class ArmaService {

    public static final int PRECIO_MAXIMO = 16_000;
    public static final List<String> BANDOS_VALIDOS = List.of("T", "CT", "CT/T");

    private final ArmaRepository armaRepository;

    // Inyección por constructor: el service solo conoce la interfaz del repositorio.
    public ArmaService(ArmaRepository armaRepository) {
        this.armaRepository = armaRepository;
    }

    public List<Arma> listar() {
        return armaRepository.findAll(Sort.by("id"));
    }

    public List<Arma> listarPorBando(String bando) {
        validarBando(bando);
        return armaRepository.findByBandoOrderByIdAsc(bando);
    }

    public Arma obtener(Long id) {
        return armaRepository.findById(id)
                .orElseThrow(() -> new ArmaNoEncontradaException(id));
    }

    @Transactional
    public Arma crear(Arma nueva) {
        validarReglas(nueva);
        if (armaRepository.existsByNombreIgnoreCase(nueva.getNombre())) {
            throw new ArmaDuplicadaException(nueva.getNombre());
        }
        return armaRepository.save(nueva);
    }

    @Transactional
    public Arma actualizar(Long id, Arma datos) {
        Arma existente = obtener(id);
        if (existente.getClass() != datos.getClass()) {
            throw new CambioDeTipoException(existente.getClass().getSimpleName(), datos.getClass().getSimpleName());
        }
        validarReglas(datos);
        if (armaRepository.existsByNombreIgnoreCaseAndIdNot(datos.getNombre(), id)) {
            throw new ArmaDuplicadaException(datos.getNombre());
        }
        existente.actualizarCon(datos);
        return armaRepository.save(existente);
    }

    @Transactional
    public void eliminar(Long id) {
        if (!armaRepository.existsById(id)) {
            throw new ArmaNoEncontradaException(id);
        }
        armaRepository.deleteById(id);
    }

    private void validarReglas(Arma arma) {
        validarBando(arma.getBando());
        if (arma.getPrecio() > PRECIO_MAXIMO) {
            throw new PrecioExcedidoException(arma.getPrecio(), PRECIO_MAXIMO);
        }
        // Polimorfismo: cada clase dice si admite skins, el service no pregunta por el tipo.
        if (!arma.admiteSkin() && arma.tieneSkinPersonalizada()) {
            throw new SkinNoPermitidaException(arma.getClass().getSimpleName());
        }
    }

    private void validarBando(String bando) {
        if (bando == null || !BANDOS_VALIDOS.contains(bando)) {
            throw new BandoInvalidoException(bando, BANDOS_VALIDOS);
        }
    }
}
