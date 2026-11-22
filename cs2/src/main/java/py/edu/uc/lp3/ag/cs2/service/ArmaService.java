package py.edu.uc.lp3.ag.cs2.service;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import py.edu.uc.lp3.ag.cs2.domain.Arma;
import py.edu.uc.lp3.ag.cs2.domain.ArmaDeFuego;
import py.edu.uc.lp3.ag.cs2.domain.Granada;
import py.edu.uc.lp3.ag.cs2.domain.GranadaHE;
import py.edu.uc.lp3.ag.cs2.domain.GranadaHumo;
import py.edu.uc.lp3.ag.cs2.exception.negocio.ArmaDuplicadaException;
import py.edu.uc.lp3.ag.cs2.exception.negocio.BandoInvalidoException;
import py.edu.uc.lp3.ag.cs2.exception.negocio.CambioDeTipoException;
import py.edu.uc.lp3.ag.cs2.exception.negocio.PrecioExcedidoException;
import py.edu.uc.lp3.ag.cs2.exception.negocio.SkinNoPermitidaException;
import py.edu.uc.lp3.ag.cs2.exception.noencontrado.ArmaNoEncontradaException;
import py.edu.uc.lp3.ag.cs2.exception.validacion.CampoObligatorioException;
import py.edu.uc.lp3.ag.cs2.exception.validacion.CargadorInvalidoException;
import py.edu.uc.lp3.ag.cs2.exception.validacion.DanoInvalidoException;
import py.edu.uc.lp3.ag.cs2.exception.validacion.DuracionHumoInvalidaException;
import py.edu.uc.lp3.ag.cs2.exception.validacion.EmpujeInvalidoException;
import py.edu.uc.lp3.ag.cs2.exception.validacion.PrecioNegativoException;
import py.edu.uc.lp3.ag.cs2.exception.validacion.RadioExplosionInvalidoException;
import py.edu.uc.lp3.ag.cs2.exception.validacion.TiempoRetardoInvalidoException;
import py.edu.uc.lp3.ag.cs2.repository.ArmaRepository;

import java.util.List;

/**
 * Capa de negocio de las armas. El dominio solo guarda el estado; todas las
 * validaciones y reglas están acá y cada una lanza su propia excepción de la
 * jerarquía (exception/).
 *
 * Invariantes de cada arma (validar): las controla antes de usar o guardar
 * un arma, también para las armas en memoria de la demostración y la tienda.
 * - nombre, bando y skin obligatorios → CampoObligatorioException.
 * - precio >= 0 → PrecioNegativoException.
 * - si está tirada en el mapa, coordenadas finitas → CoordenadaInvalidaException.
 * - armas de fuego: cargador > 0 → CargadorInvalidoException; daño base > 0 → DanoInvalidoException.
 * - granadas: retardo >= 0 → TiempoRetardoInvalidoException.
 * - humo: duración > 0 → DuracionHumoInvalidaException.
 * - HE: radio > 0 → RadioExplosionInvalidoException; daño máximo > 0 →
 *   DanoInvalidoException; empuje >= 0 → EmpujeInvalidoException.
 *
 * Reglas del catálogo (solo para el CRUD guardado en H2):
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
        validar(nueva);
        validarReglas(nueva);
        if (armaRepository.existsByNombreIgnoreCase(nueva.getNombre())) {
            throw new ArmaDuplicadaException(nueva.getNombre());
        }
        return armaRepository.save(nueva);
    }

    @Transactional
    public Arma actualizar(Long id, Arma datos) {
        Arma existente = obtener(id);
        validar(datos);
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

    /**
     * Invariantes de un arma, guardada o no. Primero lo común a toda Arma y
     * después lo propio de cada rama de la jerarquía.
     */
    public void validar(Arma arma) {
        if (arma == null) {
            throw new CampoObligatorioException("arma");
        }
        Validaciones.requerirTexto(arma.getNombre(), "nombre");
        if (arma.getPrecio() < 0) {
            throw new PrecioNegativoException(arma.getPrecio());
        }
        Validaciones.requerirTexto(arma.getBando(), "bando");
        Validaciones.requerirTexto(arma.getSkin(), "skin");
        if (arma.estaEnElMapa()) {
            Validaciones.requerirPosicion(arma.getPosicion(), "posicion");
        }
        if (arma instanceof ArmaDeFuego fuego) {
            validarArmaDeFuego(fuego);
        }
        if (arma instanceof Granada granada) {
            validarGranada(granada);
        }
    }

    private void validarArmaDeFuego(ArmaDeFuego arma) {
        if (arma.getCargadorCapacidad() <= 0) {
            throw new CargadorInvalidoException(arma.getCargadorCapacidad());
        }
        if (arma.getDanoBase() <= 0) {
            throw new DanoInvalidoException("El daño base debe ser mayor a 0", arma.getDanoBase());
        }
    }

    private void validarGranada(Granada granada) {
        if (granada.getTiempoRetardo() < 0) {
            throw new TiempoRetardoInvalidoException(granada.getTiempoRetardo());
        }
        if (granada instanceof GranadaHumo humo && humo.getDuracionHumo() <= 0) {
            throw new DuracionHumoInvalidaException(humo.getDuracionHumo());
        }
        if (granada instanceof GranadaHE he) {
            validarGranadaHE(he);
        }
    }

    private void validarGranadaHE(GranadaHE he) {
        if (he.getRadioExplosion() <= 0) {
            throw new RadioExplosionInvalidoException(he.getRadioExplosion());
        }
        if (he.getDanoMaximo() <= 0) {
            throw new DanoInvalidoException("El daño máximo debe ser mayor a 0", he.getDanoMaximo());
        }
        if (he.getEmpujeMaximo() < 0) {
            throw new EmpujeInvalidoException(he.getEmpujeMaximo());
        }
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
