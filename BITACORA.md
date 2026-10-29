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

### Sesión 2 (continuación) — Verificación final y entregables

7. **Verificación contra el enunciado POO-06:**
   * *Prompt:* Verificar si el repositorio cumple con el enunciado del ejercicio POO-06 (consignas, entregables y template de paquetes) antes de mergear el PR.
   * *Acción:* Se compararon las 10 consignas con el código: todas se cumplen. Quedaron pendientes los entregables (enlace al commit, especificaciones para Classroom) y la prueba con Maven.

8. **Merge y prueba local:**
   * *Prompt:* Mergear el PR, bajarlo a la notebook y probar Maven.
   * *Acción:* El PR #2 se mergeó en `main`. El alumno ejecutó en su notebook `./mvnw test` y `./mvnw spring-boot:run`, y todo funcionó (21/10/2026).

9. **Documentación de entrega:**
   * *Prompt:* Agregar el enlace al commit en el README, mejorar la sección de sobrecarga y sobrescritura y preparar el archivo de especificaciones.
   * *Acción:* La sección del README ahora explica qué se agregó, en qué clases y cómo distinguir sobrecarga de sobrescritura. Se generó el documento de especificaciones para Classroom.

---

## 🤖 Sesión 3 — CRUD REST con persistencia (28/10/2026)

* **Asistente / Agente de IA:** Claude (Anthropic), mediante Claude Code
* **Modelo de LLM exacto:** Claude Opus 5.5
* **Modo de trabajo:** la IA editó solo los archivos locales; el alumno hace los commits y el push.

### Resumen de prompts e interacciones

10. **CRUD de la raíz abstracta `Arma`:**
    * *Prompt:* Crear un CRUD REST para la clase `Arma` (la raíz abstracta del proyecto), con arquitectura de 3 capas, una capa de negocio (service) con validaciones y persistencia en H2 con Spring Data.
    * *Acción:*
      * `domain`: `Arma`, `ArmaDeFuego`, `Granada`, `Rifle`, `Pistola` y `GranadaHumo` pasan a ser entidades JPA con `@Inheritance(strategy = JOINED)`. Se agregaron el `id`, constructores `protected` sin parámetros para JPA, `admiteSkin()` (sobrescrito en `Granada`) y `actualizarCon(Arma)` (sobrescrito en cada subclase). Los atributos dejan de ser `final` porque los completa Hibernate, pero siguen sin setters.
      * `repository`: interfaz `ArmaRepository extends JpaRepository<Arma, Long>` con consultas derivadas.
      * `service`: `ArmaService` con 6 reglas de negocio (nombre único, bando válido, precio máximo de $16.000, granadas sin skin, no cambiar el tipo en un `PUT`, 404 si el id no existe).
      * `rest`: `ArmaCrudController` (`GET`/`POST`/`PUT`/`DELETE` en `/api/armas`), `ArmaRequest`, `ArmaGuardadaResponse` y nuevos casos en `ManejadorErrores` (404 y 409).
      * Configuración de H2 en archivo con consola web, carga inicial de 5 armas y base en memoria aparte para los tests.
      * Tests `ArmaServiceTest` y `ArmaCrudControllerTest`, colección `insomnia/cs2-armas-crud.json` y README actualizado.

### Verificación realizada y limitaciones
* En el entorno de la IA no había acceso a Maven Central. El código se compiló con `javac` contra versiones mínimas de las anotaciones de Spring y JPA, y las reglas del service se probaron con un repositorio en memoria (16 casos correctos).
* El alumno ejecutó `.\mvnw.cmd test` en su notebook y pasaron los 31 tests (28/10/2026). También levantó la app y probó el CRUD con Insomnia y la consola de H2.

---

## 🤖 Sesión 4 — Simulación de explosiones y jerarquía de excepciones (29/10/2026)

* **Asistente / Agente de IA:** Claude (Anthropic), mediante Claude Code
* **Modelo de LLM exacto:** Claude Opus 5.5
* **Modo de trabajo:** la IA editó solo los archivos locales; el alumno hace los commits y el push.

### Resumen de prompts e interacciones

11. **Consigna de Zulip:**
    * *Prompt:* Agregar una regla de negocio conectada a un servicio REST en la que participen tres clases del modelo (en CS2, una simulación de daño), con jerarquía de excepciones y un handler por tipo, la colección de Insomnia con el caso feliz y cada error, y las dos reglas nombradas en el README.
    * *Decisiones del alumno:* simular la explosión de una granada HE; la HE daña también a los aliados; los jugadores se guardan en H2; el daño depende de la distancia; la segunda regla es que las armas dentro del radio salgan volando; la granada se consume al explotar; todo error con su propia excepción, sin agrupar handlers.
    * *Acción:*
      * `domain`: nuevas `GranadaHE` (hija de `Granada`), `Jugador` (entidad) y `Posicion` (`@Embeddable`, objeto valor). `Arma` gana una posición opcional (arma tirada en el mapa).
      * `exception`: jerarquía con raíz `Cs2Exception` y tres ramas (`DatoInvalidoException`, `RecursoNoEncontradoException`, `ReglaNegocioException`) y 26 excepciones concretas. Se reemplazaron todas las `IllegalArgumentException` del proyecto, incluidas las de los constructores del dominio, `Tienda` y `ArmaController`.
      * `service`: `ExplosionService` con las dos reglas (daño según la distancia con chaleco a la mitad, y empuje de armas) y `JugadorService`.
      * `rest`: `POST /api/explosiones`, `GET/POST /api/jugadores` y `ManejadorErrores` con un `@ExceptionHandler` por cada excepción. Los errores responden también el nombre de la excepción (`tipo`).
      * Carga inicial con un escenario de explosión, tests nuevos (`PosicionTest`, `GranadaHETest`, `JugadorTest`, `JugadorServiceTest`, `ExplosionServiceTest`, `ManejadorErroresTest`), tests existentes actualizados a las excepciones nuevas, colección de Insomnia con 55 peticiones en 8 carpetas y README.

### Verificación realizada y limitaciones
* En el entorno de la IA no había acceso a Maven Central, así que se usaron las librerías que Maven ya había descargado en la notebook del alumno (carpeta `.m2`, solo lectura) para correr Maven sin conexión con JDK 21.
* `mvn test`: **51 tests, 0 fallas, BUILD SUCCESS** con Spring Boot, Hibernate y H2 reales.
* Se levantó la app con la base recién creada y se ejecutaron en orden las 55 peticiones de la colección de Insomnia: todas respondieron el código y el tipo de excepción esperados.
* Falta que el alumno lo repita en su notebook (`.\mvnw.cmd test` y la colección) antes del commit.
