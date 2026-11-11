# ADR-003. Anotaciones de JPA dentro del dominio

**Estado:** aceptada

## Contexto

En una arquitectura en capas pura, el dominio no debería saber nada de la base de datos. Separar por completo implicaría tener entidades de dominio y entidades JPA distintas, con mapeadores entre ellas.

## Decisión

Las clases del dominio llevan anotaciones de `jakarta.persistence` (`@Entity`, `@Embeddable`, `@Column`), pero **no** dependen de Spring. ArchUnit lo verifica con la regla `dominioSinSpring`. Los constructores sin argumentos que pide JPA son `protected` para que nadie los use por error.

## Alternativas consideradas

- **Modelo separado con mapeadores.** Duplicaría casi veinte clases y sus pruebas para proteger el dominio de un cambio de persistencia que no está previsto.

## Consecuencias

- Menos código y un solo lugar para cada regla.
- Las pruebas unitarias del dominio corren sin base de datos: las anotaciones no hacen nada fuera de JPA.
- Si se cambiara a una base no relacional, habría que revisar las entidades. Es la limitación asumida en la sección 5.1 del protocolo.
