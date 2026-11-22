package py.edu.uc.lp3.ag.cs2.service;

import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import py.edu.uc.lp3.ag.cs2.domain.GranadaHE;
import py.edu.uc.lp3.ag.cs2.domain.Jugador;
import py.edu.uc.lp3.ag.cs2.domain.JugadaAtaque;
import py.edu.uc.lp3.ag.cs2.domain.Posicion;
import py.edu.uc.lp3.ag.cs2.exception.externo.A3NoDisponibleException;
import py.edu.uc.lp3.ag.cs2.exception.externo.RechazoA2Exception;
import py.edu.uc.lp3.ag.cs2.exception.externo.RechazoA3Exception;
import py.edu.uc.lp3.ag.cs2.exception.negocio.GranadaYaDetonadaException;
import py.edu.uc.lp3.ag.cs2.repository.JugadaAtaqueRepository;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Escenario del POO-09 de punta a punta: A2 y A3 son un servidor HTTP de
 * prueba (com.sun.net.httpserver) que responde como dicen sus OpenAPI. Así se
 * prueban los clientes WebClient reales, el orden y que la base quede igual.
 */
@SpringBootTest
@Transactional
class JugadaServiceTest {

    private static final HttpServer COMPANEROS;
    private static final AtomicInteger STATUS_A2 = new AtomicInteger(200);
    private static final AtomicInteger STATUS_A3 = new AtomicInteger(200);
    private static final List<String> PEDIDOS = new ArrayList<>();

    static {
        try {
            COMPANEROS = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        COMPANEROS.createContext("/a2/api/armas", ex -> {
            String cuerpoPedido = new String(ex.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            PEDIDOS.add("A2 " + ex.getRequestMethod() + " " + ex.getRequestURI().getPath() + " " + cuerpoPedido);
            int status = STATUS_A2.get();
            responder(ex, status, status == 200
                    ? "{\"atacanteNombre\":\"AK-47\",\"atacanteMunicionDespues\":29,\"objetivoNombre\":\"Glock-18\","
                            + "\"objetivoDurabilidadDespues\":64,\"danoAplicado\":36,\"objetivoDestruido\":false}"
                    : "{\"tipo\":\"DISPARO_INVALIDO\",\"error\":\"El arma no puede disparar\"}");
        });
        COMPANEROS.createContext("/a3/api/armas", ex -> {
            PEDIDOS.add("A3 " + ex.getRequestMethod() + " " + ex.getRequestURI());
            int status = STATUS_A3.get();
            responder(ex, status, status == 200
                    ? "{\"arma\":\"AK-47 lleno\",\"tipo\":\"rifle\",\"exito\":true,\"danio\":36,\"efecto\":\"impacto\",\"estado\":\"29/90\"}"
                    : "{\"tipo\":\"ArmaSinMunicionException\",\"codigo\":" + status + ",\"error\":\"AK-47 vacio no tiene balas\"}");
        });
        COMPANEROS.start();
    }

    private static void responder(com.sun.net.httpserver.HttpExchange ex, int status, String cuerpo) throws IOException {
        byte[] bytes = cuerpo.getBytes(StandardCharsets.UTF_8);
        ex.getResponseHeaders().add("Content-Type", "application/json");
        ex.sendResponseHeaders(status, bytes.length);
        try (OutputStream out = ex.getResponseBody()) {
            out.write(bytes);
        }
    }

    @DynamicPropertySource
    static void urlsDeLosCompaneros(DynamicPropertyRegistry registro) {
        String base = "http://127.0.0.1:" + COMPANEROS.getAddress().getPort();
        registro.add("cs2.a2.url", () -> base + "/a2");
        registro.add("cs2.a3.url", () -> base + "/a3");
    }

    @AfterAll
    static void apagar() {
        COMPANEROS.stop(0);
    }

    private static final Posicion CENTRO = new Posicion(0, 0, 0);

    @Autowired private JugadaService jugadaService;
    @Autowired private ArmaService armaService;
    @Autowired private JugadorService jugadorService;
    @Autowired private JugadaAtaqueRepository jugadaRepository;

    private GranadaHE he;
    private Jugador fenix;

    @BeforeEach
    void escenario() {
        STATUS_A2.set(200);
        STATUS_A3.set(200);
        PEDIDOS.clear();
        he = (GranadaHE) armaService.crear(new GranadaHE("HE Grenade", 300, "CT/T"));
        fenix = jugadorService.crear(new Jugador("Fénix", "CT", new Posicion(1, 0, 0)));
    }

    @Test
    @DisplayName("Camino feliz: A2 y A3 aceptan → explota la HE y la jugada queda guardada")
    void caminoFeliz() {
        JugadaAtaque jugada = jugadaService.atacar(he.getId(), CENTRO, null, null, null);

        assertEquals(List.of(
                "A2 POST /a2/api/armas/1/disparar {\"objetivoId\":2,\"distancia\":0,\"critico\":false}",
                "A3 POST /a3/api/armas/4/disparar?distancia=10.0"), PEDIDOS);   // ids por defecto, en orden
        assertEquals(1, jugadaRepository.count());
        assertEquals(jugada.getId(), jugadaService.listar().get(0).getId());
        assertTrue(jugada.getResultadoA2().startsWith("A2 (Mauricio Pecci): AK-47 le disparó a Glock-18"));
        assertTrue(jugada.getResultadoA3().startsWith("A3 (Vanessa Ricciardi): AK-47 lleno disparó"));
        assertEquals(1, jugada.getJugadoresDanados());
        assertTrue(he.isDetonada());
        assertEquals(14, fenix.getVida());
    }

    @Test
    @DisplayName("A2 rechaza → RechazoA2Exception, A3 no se consulta y la base queda igual")
    void rechazaA2() {
        STATUS_A2.set(400);
        assertThrows(RechazoA2Exception.class, () -> jugadaService.atacar(he.getId(), CENTRO, 7L, 8L, null));

        assertEquals(1, PEDIDOS.size());
        assertTrue(PEDIDOS.get(0).startsWith("A2 POST /a2/api/armas/7/disparar {\"objetivoId\":8"));
        assertEquals(0, jugadaRepository.count());
        assertFalse(he.isDetonada());
        assertEquals(100, fenix.getVida());
    }

    @Test
    @DisplayName("A3 rechaza → RechazoA3Exception con su 409, y la base queda igual")
    void rechazaA3() {
        STATUS_A3.set(409);
        RechazoA3Exception e = assertThrows(RechazoA3Exception.class,
                () -> jugadaService.atacar(he.getId(), CENTRO, null, null, 3L));

        assertEquals(409, e.getStatusRecibido());
        assertTrue(e.getMessage().contains("AK-47 vacio no tiene balas"));
        assertEquals(2, PEDIDOS.size());
        assertEquals("A3 POST /a3/api/armas/3/disparar?distancia=10.0", PEDIDOS.get(1));
        assertEquals(0, jugadaRepository.count());
        assertFalse(he.isDetonada());
        assertEquals(100, fenix.getVida());
    }

    @Test
    @DisplayName("A3 con error propio (500) → A3NoDisponibleException y la base queda igual")
    void a3Caido() {
        STATUS_A3.set(500);
        assertThrows(A3NoDisponibleException.class, () -> jugadaService.atacar(he.getId(), CENTRO, null, null, null));
        assertEquals(0, jugadaRepository.count());
        assertFalse(he.isDetonada());
    }

    @Test
    @DisplayName("Si la granada propia ya explotó, se corta antes de consultar a los compañeros")
    void reglasPropiasPrimero() {
        jugadaService.atacar(he.getId(), CENTRO, null, null, null);
        PEDIDOS.clear();

        assertThrows(GranadaYaDetonadaException.class, () -> jugadaService.atacar(he.getId(), CENTRO, null, null, null));
        assertTrue(PEDIDOS.isEmpty());
        assertEquals(1, jugadaRepository.count());
    }
}
