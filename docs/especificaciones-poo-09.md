# Especificaciones — POO-09: APIs cruzadas (Counter-Strike 2)

**Asignatura:** Lenguaje de Programación 3 (CYT646) · **Edición:** 2026  
**Alumno:** Ariel Gaona · **Repositorio:** https://github.com/ferue/agaona-taller-git-2026 (rama `feature/RESTApi`)  
**Enunciado:** `ejercicio-poo-09-apis-cruzadas-2026-11-16`  
**Commit de la entrega:** se indica en Classroom y en el README.

---

## 1. Corrección del POO-08 (3 puntos)

Las tres observaciones de la corrección quedaron cerradas (detalle en el README, sección «Situación del POO-08»):

| Observación | Cómo se cerró |
|---|---|
| Validaciones, reglas y excepciones en el dominio (−1) | El paquete `domain/` ya no tiene ningún `throw` ni importa `exception/`. Las invariantes están en `ArmaService.validar` y `JugadorService.validar`; las dos reglas y su geometría, en `ExplosionService`. |
| Lógica en `ArmaController` y `TiendaController` (mejora) | Nuevos `DemostracionArmasService`, `TiendaService` y `UIService`; los controllers solo convierten HTTP ↔ objetos. |
| curl de la Desert Eagle con `id 6` (−0,5) | Con la base limpia responde `id 10`; corregido en el README. |

## 2. Grupo (trío de CS2)

| Rol | Nombre | URL base | OpenAPI | Regla que se consume |
|---|---|---|---|---|
| A1 | Ariel Gaona | `https://ariel.tail26e794.ts.net` | `/v3/api-docs` | (este servicio) Daño y empuje de la HE; sin efectos en `POST /api/explosiones/validar` |
| A2 | Mauricio Pecci | `https://desktop-hrm8vjo.tailde75e4.ts.net` | `/v3/api-docs` | Disparo: `POST /api/armas/{id}/disparar` |
| A3 | Vanessa Ricciardi | `http://100.95.213.93:8081` | `/v3/api-docs` | Regla 1, disparar: `POST /api/armas/{id}/disparar?distancia=10` |

Repositorios: A2 `MauricioPecci/MPecci-tplp3-2026` (rama `feature/rest`) · A3 `luckvani-gif/vricciardi-tallerlp3-2026`. Las reglas de A2 y A3 se tomaron de sus documentos OpenAPI, sin copiar código.

## 3. El escenario en una frase

**Ataque coordinado:** antes de lanzar la HE, dos compañeros abren fuego (uno con un arma de A2 contra un objetivo, otro con un rifle de A3); si los dos disparos son legales, la HE explota con las dos reglas propias y la jugada se guarda en el H2 de A1.

* `POST /api/jugadas` con `{"granadaId": 15, "x": 0, "y": 0, "z": 0}` (opcionales `armaA2Id`, `objetivoA2Id`, `armaA3Id`).
* Orden: 1) reglas propias sin efectos, 2) A2, 3) A3, 4) explosión y guardado. Las reglas propias van primero porque los disparos de A2 y A3 cambian sus bases (gastan munición).
* Éxito → **201** y `GET /api/jugadas` muestra la jugada. Rechazo → la base de A1 queda igual y el error dice de quién fue: `RechazoA2Exception` / `RechazoA3Exception` (409), `A2NoDisponibleException` / `A3NoDisponibleException` (502).
* Las URLs y los ids de los compañeros se configuran en `application.properties` (`cs2.a2.*`, `cs2.a3.*`) o con variables de entorno (`CS2_A2_URL`, `CS2_A3_URL`...).

## 4. Cómo probarlo

1. Arrancar A3 (`./mvnw spring-boot:run`, puerto 8081) y A2 (`./mvnw spring-boot:run`; su H2 es en memoria: crear sus armas 1 y 2 con los pedidos «Preparar A2» de Insomnia).
2. Arrancar A1: `cd cs2` y `./mvnw spring-boot:run`, con `cs2.a2.url` y `cs2.a3.url` apuntando a A2 y A3.
3. Insomnia: importar `insomnia/cs2-armas-crud.json` y correr la carpeta **11. POO-09** en orden:
   * OpenAPI de A1, A2 y A3;
   * crear la HE del escenario y la de los rechazos;
   * **camino feliz → 201**, y `GET /api/jugadas` la muestra;
   * **rechazo de A3** (`armaA3Id: 999`, un arma que A3 no tiene) → 409 `RechazoA3Exception`; si A3 tiene un arma con el cargador vacío, el pedido «Rechazo de A3 por regla» muestra el rechazo por su regla de negocio (`CargadorVacioException`);
   * **rechazo de A2** (`armaA2Id: 999`) → 409 `RechazoA2Exception`;
   * `GET /api/jugadas` → los rechazos no agregaron ninguna jugada.

## 5. Resultados reales (22/11/2026)

| Caso | Respuesta |
|---|---|
| Camino feliz | 201 · A2: «pistola le disparó a Pi: 1 de daño» · A3: «AK-47 lleno disparó: 34 de daño (29/90)» · la HE dañó a 3 jugadores y desplazó 3 armas |
| Rechazo de A3 | 409 `RechazoA3Exception` · «Vanessa rechazó con HTTP 409: CargadorVacioException» |
| Rechazo de A2 | 409 `RechazoA2Exception` · «Mauricio rechazó con HTTP 404: ARMA_NO_ENCONTRADA» |
| A2 caído | 502 `A2NoDisponibleException` · «respondió HTTP 500» |

Las respuestas completas están en `insomnia/respuestas-poo09/`, para la corrección si un compañero no está en línea. Los diagramas de secuencia (feliz, rechazo de A2, rechazo de A3 y compañero caído) están en el README.

## 6. Criterios de la rúbrica

| Criterio | Dónde se ve |
|---|---|
| 1. OpenAPI | `/v3/api-docs` y `/swagger-ui.html`: CRUD, simulación, `/api/explosiones/validar`, `/api/jugadas` y los errores de cada uno |
| 2. Round robin | README: tabla A1/A2/A3 con nombres, URLs, OpenAPI y reglas |
| 3. Consumo | `client/A2ArmasClient` y `client/A3ArmasClient` (WebClient), URLs configuradas, log DEBUG de cada paso |
| 4. Escenario persistido | `POST /api/jugadas` → `JugadaService`; `GET /api/jugadas`; Insomnia carpeta 11 con el feliz y un rechazo por compañero; 78 tests (`JugadaServiceTest` simula A2 y A3 por HTTP) |
