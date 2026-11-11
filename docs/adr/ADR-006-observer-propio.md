# ADR-006. Observer propio con orden explícito en lugar de eventos de Spring

**Estado:** aceptada

## Contexto

Después de cada acción (y de cada deshacer) hay que actualizar retos, insignias y nivel. Las recompensas de los retos cambian los puntos, así que los retos deben actualizarse antes que el nivel. Además, el controlador necesita las novedades para mostrarlas («Subiste a nivel Explorador»).

## Decisión

`NotificadorProgreso` es el sujeto y recibe de Spring la lista de `ObservadorProgreso` ordenada con `@Order` (retos 1, insignias 2, nivel 3). Cada observador devuelve sus novedades y el notificador las junta.

## Alternativas consideradas

- **ApplicationEventPublisher de Spring.** Los oyentes no devuelven valores y el orden depende de configurarlo aparte; habría que guardar las novedades en otro lado.
- **Llamadas directas desde cada comando.** Acoplaría los comandos a todas las reacciones y obligaría a tocarlos para agregar una nueva.

## Consecuencias

- Agregar una reacción es crear un `@Component` que implemente la interfaz y darle su `@Order`; ArchUnit exige que lo tenga.
- Todo ocurre dentro de la misma transacción del comando, así que una falla revierte todo.
