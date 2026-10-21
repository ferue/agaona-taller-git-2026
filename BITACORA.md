# Bitácora de Desarrollo — Taller POO-06 (Counter-Strike 2)

**Asignatura:** Lenguaje de Programación 3 (CYT646)  
**Edición:** 2026  
**Alumno:** Ariel Gaona  
**Dominio:** Counter-Strike 2 (Opción B)  

---

## 🤖 Información de Asistencia de IA
* **Asistente / Agente de IA:** Gemini (Google)
* **Modelo de LLM exacto:** Gemini 3 Flash (Web)
* **Modo de operación:** Free tier

---

## 📝 Resumen de Prompts y Interacciones

1. **Revisión inicial del repositorio:**
   * *Prompt:* Análisis del enlace al repositorio de GitHub (`https://github.com/ferue/agaona-taller-git-2026`) para verificar el estado de los paquetes, los controladores y la estructura del dominio de Counter-Strike 2.
   * *Acción:* Diagnóstico de los componentes existentes y plan de acción para cumplir con la rúbrica de POO-06.

2. **Refactorización de la clase base (`Arma.java`):**
   * *Prompt:* Adaptación del modelo para incluir constructores simples y sobrecargados, además de incorporar la sobrecarga de métodos enfocada en la temática real del juego (gestión de skins y acabados StatTrak™).
   * *Acción:* Actualización del código Java aplicando encapsulamiento y métodos abstractos.

3. **Actualización de la Documentación (`README.md`):**
   * *Prompt:* Estructuración del archivo README con la Licencia Apache 2.0, el diagrama de clases completo en sintaxis Mermaid alineado con la estructura real de `src/`, y un apartado detallado explicando las diferencias entre sobrecarga y sobrescritura.
   * *Acción:* Generación y corrección del contenido Markdown para el repositorio.

4. **Preparación de Entregables:**
   * *Prompt:* Creación de las especificaciones técnicas y la bitácora de desarrollo para la presentación en Classroom y el repositorio.
   * *Acción:* Redacción de los entregables requeridos por la cátedra.
---

## 🤖 Sesión 2 — Corrección contra la rúbrica oficial (21/10/2026)

* **Asistente / Agente de IA:** Claude (Anthropic), mediante Claude Code
* **Modelo de LLM exacto:** Claude Opus 5.5
* **Rama de trabajo:** `correcciones-rubrica`

### Resumen de prompts e interacciones

5. **Revisión contra la rúbrica (8 criterios):**
   * *Prompt:* Revisar el repositorio con la rúbrica oficial de CYT646 y generar un informe de corrección con evidencias de lo probado.
   * *Acción:* Revisión del código y del historial Git, 37 pruebas sobre el dominio y los controladores. Hallazgos: el constructor `Arma(nombre, precio, bando)` descartaba el bando, los `package` no coincidían con las carpetas, no había invariantes, `new Tienda()` lanzaba NullPointerException y la API devolvía 200 en los errores. Puntaje estimado: 15/24.

6. **Corrección para cumplir todos los criterios:**
   * *Prompt:* Corregir el proyecto para que cumpla con todos los requisitos de la rúbrica.
   * *Acción:* Commits separados por tema:
     * `refactor`: `package` alineados con `domain/` y `rest/`.
     * `fix(domain)`: bando corregido, atributos `final` sin setters, validaciones con `IllegalArgumentException`, constructores sobrecargados en las clases hijas, `recargar()`, `Tienda` con catálogo de solo lectura y `ResultadoCompra`.
     * `feat(api)`: `ManejadorErrores` (400/404), `FabricaArmas`, códigos 201/400/404/409 en la tienda, endpoints `/api/armas/rafaga` y `/api/armas/polimorfismo`, skin StatTrak™ desde `/api/armas/probar`.
     * `build`: se quitaron JPA y H2 (no se usaban).
     * `test`: 18 tests JUnit 5 del dominio y de los controladores.
     * `docs`: README (versión de Spring Boot, estructura, endpoints con `curl`, diagrama completo) y esta bitácora.

### Verificación realizada y limitaciones
* El código y los tests se compilaron con `javac` (JDK 21) y los 18 tests pasaron.
* En el entorno de la IA no había acceso a Maven Central, por lo que `./mvnw test` y `./mvnw spring-boot:run` **deben ejecutarse localmente** antes de la entrega para confirmar el arranque de Spring Boot y las respuestas HTTP reales.
