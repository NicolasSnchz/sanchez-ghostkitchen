# ADR-001: Capas simples con MVC de Spring Boot; sin puertos hexagonales

## Estado: Aceptado (2026-11-11)

## Contexto

GhostKitchen Hunter es una sola aplicación web con una base de datos. Las cocinas reciben sus pedidos por sus propios canales (Rappi, DiDi Food, WhatsApp o domicilio propio); la aplicación solo guarda cuáles usa cada una y nunca se comunica con esos servicios. No hay pagos, ni correo, ni otra API externa. Tampoco hay ningún plan de cambiar de motor de base de datos: H2 en memoria alcanza para el alcance del curso.

El curso vio capas, MVC, hexagonal, clean architecture y DDD con CQRS. La pregunta es si alguna de las más elaboradas resuelve un problema que este proyecto tenga hoy.

## Decisión

Se usan **capas** (presentación, aplicación, persistencia y dominio, con seguridad y configuración como transversales) y **MVC de Spring Boot** en la presentación. Los servicios de aplicación dependen directamente de los repositorios de Spring Data, sin una interfaz de puerto en medio. El sentido de las dependencias, la ausencia de ciclos y el aislamiento del dominio se comprueban con ArchUnit en cada build (`ArquitecturaTest`, reglas ARQ-01 a ARQ-09).

Criterios concretos con los que se decidió:

| Criterio | Valor en este proyecto | Qué pediría hexagonal |
|---|---|---|
| Sistemas externos que se integran | 0 | Al menos uno que pueda cambiar |
| Motores de base de datos previstos | 1 (H2, y cualquier relacional por configuración) | Dos o más, o uno no relacional |
| Interfaces de puerto que habría que escribir | 8, una por repositorio, idénticas a las de Spring Data | Interfaces con una razón de cambio propia |
| Formas de entrar a la aplicación | 2 (web y API REST), ambas HTTP y ya separadas en `presentacion.web` y `presentacion.api` | Entradas de naturaleza distinta (colas, consola, eventos) |

## Alternativas consideradas

- **Hexagonal o clean architecture.** Protegen el dominio de la infraestructura con puertos y adaptadores. Aquí cada puerto sería una copia del repositorio de Spring Data con otro nombre, más un adaptador que solo delega: abstracción especulativa (YAGNI) sin ningún caso de uso que la pida.
- **CQRS o event sourcing.** Las lecturas son consultas JPQL que devuelven records y caben en el mismo modelo. Separar escritura y lectura, o guardar eventos, multiplicaría el código para un problema que no existe (KISS).

## Consecuencias

Positivas:
- La estructura se entiende de una vez para quien conoce Spring, y cada capa tiene una sola responsabilidad.
- ArchUnit evita que con el tiempo aparezcan dependencias al revés o ciclos; el cálculo de acoplamiento en [`docs/acoplamiento-paquetes.md`](../acoplamiento-paquetes.md) muestra que todas las dependencias van hacia paquetes más estables.
- El único cambio que se ve venir, avisar al cazador por otro medio cuando completa un reto o sube de nivel, ya tiene su punto de extensión: basta otro `ObservadorProgreso` ([ADR-006](ADR-006-observer-propio.md)).

Negativas:
- Si algún día se integra una API externa (por ejemplo, leer pedidos de Rappi) o se cambia a una base no relacional, habrá que introducir el puerto en ese momento. Es una deuda aceptada de forma consciente.
- Un revisor que espere puertos en todas las dependencias puede leer la falta de simetría como un descuido si no lee este ADR.

## Señal para reconsiderar

La primera integración con un sistema externo real, o un segundo mecanismo de persistencia. Con uno de los dos, el puerto deja de ser especulativo.
