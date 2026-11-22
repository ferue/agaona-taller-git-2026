package py.edu.uc.lp3.ag.cs2.service;

import org.springframework.stereotype.Service;
import py.edu.uc.lp3.ag.cs2.client.ObjetoExternoDto;
import py.edu.uc.lp3.ag.cs2.client.ObjetosApiClient;
import py.edu.uc.lp3.ag.cs2.exception.noencontrado.ObjetoExternoNoEncontradoException;
import py.edu.uc.lp3.ag.cs2.exception.validacion.CampoObligatorioException;

import java.util.List;
import java.util.Locale;

/**
 * Capa de negocio de la consulta a restful-api.dev. Usa el cliente WebClient
 * y decide qué hacer con lo que responde:
 * - Filtra por nombre si se pide (sin distinguir mayúsculas).
 * - Si el objeto no existe → ObjetoExternoNoEncontradoException (404).
 * - Si la API no responde → ServicioExternoNoDisponibleException (502), desde el cliente.
 */
@Service
public class ObjetoExternoService {

    private final ObjetosApiClient cliente;

    public ObjetoExternoService(ObjetosApiClient cliente) {
        this.cliente = cliente;
    }

    public List<ObjetoExternoDto> listar(String nombre) {
        List<ObjetoExternoDto> objetos = cliente.listar();
        if (nombre == null || nombre.isBlank()) {
            return objetos;
        }
        String buscado = nombre.trim().toLowerCase(Locale.ROOT);
        return objetos.stream()
                .filter(o -> o.name() != null && o.name().toLowerCase(Locale.ROOT).contains(buscado))
                .toList();
    }

    public ObjetoExternoDto obtener(String id) {
        if (id == null || id.isBlank()) {
            throw new CampoObligatorioException("id");
        }
        return cliente.buscar(id.trim())
                .orElseThrow(() -> new ObjetoExternoNoEncontradoException(id));
    }
}
