# ADR-003: Anotaciones de JPA dentro del dominio, sin dependencias de Spring

## Estado: Aceptado (2026-11-11)

## Contexto

En el ejemplo de referencia de la unidad 12, el paquete de reglas no depende ni de Spring ni de `jakarta.persistence`. Llevar eso al pie de la letra aquí obligaría a tener dos modelos: entidades de dominio puras y entidades JPA, con mapeadores entre ellas. El dominio de GhostKitchen Hunter tiene 23 clases (entidades, objetos de valor y enums) y casi todas se guardan tal cual.

## Decisión

Las clases del dominio llevan anotaciones de `jakarta.persistence` (`@Entity`, `@Embeddable`, `@Column`) pero **no** dependen de Spring. La regla ARQ-02 de ArchUnit (`dominioSinSpring`) lo verifica en cada build. Los constructores sin argumentos que exige JPA son `protected` para que nadie los use por error, y las reglas del juego viven en métodos del dominio, no en los servicios.

## Alternativas consideradas

- **Modelo separado con mapeadores.** Duplicaría unas veinte clases, sus pruebas y un mapeador por entidad para proteger el dominio de un cambio de persistencia que no está previsto ([ADR-001](ADR-001-capas-y-mvc.md)). Es la misma abstracción especulativa que se descartó para los puertos.
- **Mapeo por XML (`orm.xml`).** Deja el dominio sin anotaciones, pero esconde el mapeo en otro archivo que el compilador no revisa; un cambio de nombre en una clase rompería el mapeo en tiempo de ejecución.

## Consecuencias

Positivas:
- Un solo lugar para cada regla y menos código que mantener.
- Las pruebas unitarias del dominio corren sin base de datos ni Spring: fuera de JPA las anotaciones no hacen nada.

Negativas:
- El dominio queda atado a JPA. Si se cambiara a una base no relacional, habría que revisar las entidades. Es la limitación asumida en la sección 5.1 del protocolo.
- La regla de ArchUnit es más débil que la del ejemplo del curso (prohíbe Spring, no JPA). Se hizo así a propósito y queda documentado aquí para que no se lea como un olvido.

## Señal para reconsiderar

Que el dominio empiece a necesitar anotaciones propias de Hibernate (`@Formula`, `@BatchSize`) o que aparezca un segundo mecanismo de persistencia. En ese punto el modelo separado sí se paga.
