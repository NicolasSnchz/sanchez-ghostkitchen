# ADR-001. Arquitectura en capas con MVC, sin hexagonal

**Estado:** aceptada

## Contexto

GhostKitchen Hunter es una sola aplicación web, con una base de datos y sin integraciones externas (las cocinas reciben pedidos por sus propios canales). El curso propone varias arquitecturas: capas, MVC con Spring Boot, hexagonal, clean architecture, DDD con CQRS y event sourcing. La rúbrica pide justificar que no haya sobreingeniería.

## Decisión

Usar **capas** (presentación, aplicación, persistencia y dominio, más seguridad y configuración como transversales) con **MVC de Spring Boot** en la presentación. Las dependencias van en un solo sentido y se comprueban con ArchUnit en cada build (`ArquitecturaTest`).

## Alternativas consideradas

- **Hexagonal o clean architecture.** Protegen el dominio con puertos y adaptadores. Aquí no hay sistemas externos que cambien, y un puerto por repositorio duplicaría las interfaces de Spring Data sin ganar nada (YAGNI).
- **CQRS o event sourcing.** Las lecturas son sencillas y caben en el mismo modelo. Separar escritura y lectura, o guardar eventos, multiplicaría el código para un problema que no existe.

## Consecuencias

- La estructura es fácil de leer para alguien que conoce Spring y cada capa tiene una responsabilidad clara.
- ArchUnit evita que con el tiempo aparezcan dependencias al revés.
- Si algún día hubiera una integración externa real (por ejemplo, leer pedidos de Rappi), se agregaría un adaptador puntual en ese momento.
