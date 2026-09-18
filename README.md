# Taller de GIT + Práctica POO & API REST (CS2)

**Asignatura:** CYT646 — LP3  
**Alumno:** Ariel Gaona  
**Dominio Seleccionado:** Opción B — Counter-Strike 2  
**Licencia:** Apache License 2.0  

---

## 📌 Descripción del Proyecto

Este repositorio contiene la implementación de un servicio API REST desarrollado con **Spring Boot** y **Java 21**, estructurado bajo el paquete `py.edu.uc.lp3.ag.cs2`. 

El proyecto modela las armas del videojuego **Counter-Strike 2** aplicando conceptos clave de la Programación Orientada a Objetos:
* **Encapsulamiento y Ocultamiento de Información:** Atributos declarados como `private` y manipulación de estado interna mediante métodos de dominio.
* **Herencia y Generalización (3 Niveles):** Jerarquía estructurada en tres niveles (`Arma` ➔ `ArmaDeFuego` / `Granada` ➔ `Rifle` / `Pistola` / `GranadaHumo`).
* **Polimorfismo:** Uso de métodos abstractos (`ejecutarAccionPrincipal()`, `obtenerDetalles()`) invocados desde el tipo base (`Arma`) dentro del controlador REST sin requerir estructuras condicionales `if/else` por cada tipo concreto.

---

## 📐 Diagrama de Clases (Mermaid)

```mermaid
classDiagram
    %% Nivel 1: Clase Base General
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

    %% Nivel 2: Especializaciones Intermedias
    class ArmaDeFuego {
        <<abstract>>
        -int cargadorCapacidad
        -int municionRestante
        -int danoBase
        +disparar() String
        +getMunicionRestante() int
        +getDanoBase() int
    }

    class Granada {
        <<abstract>>
        -float tiempoRetardo
        +getTiempoRetardo() float
    }

    %% Nivel 3: Clases Concretas (6 Clases en Total)
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

    class GranadaHumo {
        -float duracionHumo
        +ejecutarAccionPrincipal() String
        +obtenerDetalles() String
    }

    %% Relaciones de Herencia (2 Niveles)
    Arma <|-- ArmaDeFuego
    Arma <|-- Granada
    ArmaDeFuego <|-- Rifle
    ArmaDeFuego <|-- Pistola
    Granada <|-- GranadaHumo
