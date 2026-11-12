# ADR-006: Observer propio con orden explícito en lugar de eventos de Spring

## Estado: Aceptado (2026-11-11)

## Contexto

Después de cada acción, y de cada deshacer, hay que actualizar retos, insignias y nivel. Las recompensas de los retos cambian los puntos, así que los retos deben actualizarse antes que el nivel. Además, el controlador necesita las novedades en la misma petición para mostrarlas («Completaste el reto Doble o nada y ganaste 120 puntos», «Subiste a nivel Explorador»).

## Decisión

`NotificadorProgreso` es el sujeto y recibe de Spring la lista de `ObservadorProgreso` ordenada con `@Order`: retos (1), insignias (2) y nivel (3). Cada observador devuelve sus novedades y el notificador las junta. Todo ocurre en la misma transacción del comando.

## Alternativas consideradas

- **`ApplicationEventPublisher` de Spring.** Los oyentes no devuelven valores, así que las novedades tendrían que guardarse en otro lado para leerlas después; y el orden habría que configurarlo aparte.
- **Llamadas directas desde cada comando.** Acoplaría los tres comandos a todas las reacciones y obligaría a tocarlos para agregar una nueva.

## Consecuencias

Positivas:
- Agregar una reacción es crear un `@Component` que implemente la interfaz y darle su `@Order`; ArchUnit exige que lo tenga (ARQ-06).
- Una falla en cualquier observador revierte toda la acción, así que nunca queda una caza sin su progreso.
- Es el punto de extensión para el único cambio que se ve venir: avisar por otro medio cuando se completa un reto ([ADR-001](ADR-001-capas-y-mvc.md)).

Negativas:
- Los observadores corren en el mismo hilo; uno lento haría lenta la respuesta de cazar.
- Mantener la numeración de `@Order` es manual.

## Señal para reconsiderar

Un observador que haga entrada y salida externa (correo, notificación push). Ese en particular debería ir con `@TransactionalEventListener` y asíncrono, para no bloquear la caza ni revertirla si el servicio externo falla.
