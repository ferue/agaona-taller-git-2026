# Bitácora de Práctica: Taller de GIT + POO & API REST

**Asignatura:** CYT646 — LP3  
**Alumno:** Ariel Gaona  
**Dominio Seleccionado:** Opción B — Counter-Strike 2  
**Fecha:** Septiembre 2026  
**Repositorio GitHub:** [https://github.com/agaona/agaona-taller-git-2026](https://github.com/agaona/agaona-taller-git-2026)  

---

## 1. Registro de Pasos Realizados

### Paso 1: Configuración del Repositorio e Inicialización
1. Creación del repositorio público en GitHub con el nombre `agaona-taller-git-2026`, inicializado con un archivo `README.md` y la licencia **Apache License 2.0**.
2. Clonación local del repositorio mediante HTTPS.
3. Generación del proyecto base en **Spring Boot 3.x** utilizando **Java 21** y **Maven** desde [start.spring.io](https://start.spring.io/), incluyendo la dependencia `Spring Web`.
4. Estructuración dentro del paquete raíz: `py.edu.uc.lp3.ag.cs2`.

### Paso 2: Implementación y Endurecimiento del Modelo POO
Se diseñó la jerarquía de clases aplicando principios de Orientación a Objetos:
* **Encapsulamiento estricto:** Todos los atributos de las entidades son declarados como `private`. La lectura de datos se realiza por medio de getters y la modificación de estados internos se limita a los métodos de dominio (`disparar()`).
* **Generalización y Polimorfismo:** Se estableció la clase base abstracta `Arma` con el método abstracto `ejecutarAccionPrincipal()`. La clase abstracta `ArmaDeFuego` extiende de `Arma` y añade el manejo de cargadores y munición, mientras que las clases concretas (`Rifle` y `Pistola`) redefinen el comportamiento especializado.

### Paso 3: Exposición de Servicios REST
* **`IndexController`:** Expone el endpoint raíz `GET /` devolviendo el estado y confirmación del servicio HTTP.
* **`ArmaController`:** Expone el endpoint `GET /api/armas/probar`. Construye instancias concretas (`Rifle` o `Pistola`) asignadas a una variable del tipo padre (`Arma`) según los parámetros recibidos por la URL (`tipo`, `nombre`, `precio`), devolviendo una respuesta JSON polimórfica sin recurrir a validaciones manuales por tipo.

---

## 2. Diagrama de Clases (Mermaid)

```mermaid
classDiagram
    class Arma {
        <<abstract>>
        -String nombre
        -int precio
        -String bando
        +getNombre() String
        +getPrecio() int
        +getBando() String
        +ejecutarAccionPrincipal()* String
        +obtenerDetalles()* String
    }

    class ArmaDeFuego {
        <<abstract>>
        -int cargadorCapacidad
        -int municionRestante
        -int danoBase
        +disparar() String
        +getMunicionRestante() int
        +getDanoBase() int
    }

    class Rifle {
        -boolean tieneMira
        +ejecutarAccionPrincipal() String
        +obtenerDetalles() String
    }

    class Pistola {
        -boolean tieneSilenciador
        +ejecutarAccionPrincipal() String
        +obtenerDetalles() String
    }

    Arma <|-- ArmaDeFuego
    ArmaDeFuego <|-- Rifle
    ArmaDeFuego <|-- Pistola