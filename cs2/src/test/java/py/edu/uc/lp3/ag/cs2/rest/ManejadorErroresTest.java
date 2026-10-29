package py.edu.uc.lp3.ag.cs2.rest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.web.bind.annotation.ExceptionHandler;
import py.edu.uc.lp3.ag.cs2.exception.Cs2Exception;
import py.edu.uc.lp3.ag.cs2.exception.negocio.ArmaDuplicadaException;
import py.edu.uc.lp3.ag.cs2.exception.negocio.GranadaYaDetonadaException;
import py.edu.uc.lp3.ag.cs2.exception.negocio.PrecioExcedidoException;
import py.edu.uc.lp3.ag.cs2.exception.noencontrado.ArmaNoEncontradaException;
import py.edu.uc.lp3.ag.cs2.exception.validacion.CampoObligatorioException;
import py.edu.uc.lp3.ag.cs2.rest.response.ErrorResponse;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ManejadorErroresTest {

    private final ManejadorErrores manejador = new ManejadorErrores();

    @Test
    @DisplayName("Cada excepción concreta de la jerarquía tiene su propio handler (uno por tipo)")
    void unHandlerPorTipo() throws ClassNotFoundException {
        // Busca todas las clases concretas que heredan de Cs2Exception.
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AssignableTypeFilter(Cs2Exception.class));
        Set<Class<?>> excepciones = new HashSet<>();
        for (BeanDefinition bd : scanner.findCandidateComponents("py.edu.uc.lp3.ag.cs2.exception")) {
            excepciones.add(Class.forName(bd.getBeanClassName()));
        }
        assertFalse(excepciones.isEmpty());

        List<Class<?>> manejadas = new ArrayList<>();
        for (Method m : ManejadorErrores.class.getDeclaredMethods()) {
            ExceptionHandler h = m.getAnnotation(ExceptionHandler.class);
            if (h != null) {
                assertEquals(1, h.value().length, "El handler " + m.getName() + " agrupa varias excepciones");
                manejadas.add(h.value()[0]);
            }
        }
        for (Class<?> e : excepciones) {
            assertTrue(manejadas.contains(e), "Falta el handler de " + e.getSimpleName());
        }
    }

    @Test
    @DisplayName("Cada rama responde con su código HTTP y el nombre de la excepción")
    void codigos() {
        ErrorResponse r = manejador.campoObligatorio(new CampoObligatorioException("x")).getBody();
        assertEquals(400, r.status());
        assertEquals("CampoObligatorioException", r.tipo());

        assertEquals(404, manejador.armaNoEncontrada(new ArmaNoEncontradaException(1L)).getStatusCode().value());
        assertEquals(400, manejador.precioExcedido(new PrecioExcedidoException(20000, 16000)).getStatusCode().value());
        assertEquals(409, manejador.armaDuplicada(new ArmaDuplicadaException("AK-47")).getStatusCode().value());
        assertEquals(409, manejador.granadaYaDetonada(new GranadaYaDetonadaException("HE")).getStatusCode().value());
    }
}
