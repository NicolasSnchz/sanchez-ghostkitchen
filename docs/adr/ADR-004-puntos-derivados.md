# ADR-004: El total de puntos y el nivel se derivan; las insignias se sincronizan

## Estado: Aceptado (2026-11-11)

## Contexto

Lo natural sería guardar un campo `puntos` en el cazador y sumarle cada vez que caza. Pero con el Command de por medio las acciones se deshacen, los retos se completan y luego pueden volver a quedar pendientes, y un contador acumulado puede desincronizarse con lo que de verdad pasó. Un error ahí se ve en público, en el ranking.

Al mismo tiempo, los puntos de una caza dependen del momento en que ocurrió: si fue la primera de esa cocina, cuántos días de racha llevaba el cazador y si era hora fantasma. Ese contexto no se puede reconstruir después.

## Decisión

- Cada caza guarda los puntos que le dio la cadena de decoradores en su momento (`Descubrimiento.puntosObtenidos`), porque es un hecho histórico.
- El total **no** se guarda: `PuntosCazador.total()` lo calcula sumando los puntos de las cazas, 20 por cada reseña y las recompensas de los retos completados. El panel y el ranking usan esa misma fórmula.
- El nivel se deriva del total con `Nivel.paraPuntos()`.
- Las insignias sí se guardan, para conservar la fecha en que se ganaron, pero el observador las sincroniza con las reglas en cada cambio: otorga las nuevas y retira las que ya no se merecen.

## Alternativas consideradas

- **Contador acumulado con suma y resta.** Cada comando tendría que saber cuánto restar al deshacer, incluido el efecto en cadena de un reto que deja de estar completo. Es justo la fuente de errores que se quiere evitar.
- **Recalcular también los puntos de cada caza.** Imposible de hacer bien: la racha y el bono de primer cazador de ese día ya no se pueden saber con certeza después.

## Consecuencias

Positivas:
- Deshacer cualquier acción deja el total correcto sin lógica extra: basta con que el comando borre lo que creó.
- `DatosDemoIT` comprueba que los puntos del ranking coinciden con los del panel de cada cazador.

Negativas:
- El ranking hace consultas de agregación (con proyecciones record) en vez de leer un campo. Con el volumen del proyecto no se nota, pero con miles de cazadores habría que medirlo.
- Si cambian las reglas de puntos, las cazas viejas conservan los puntos con que se ganaron. Es lo justo para el juego, pero hay que explicarlo si alguien compara.

## Señal para reconsiderar

Que el ranking tarde de forma medible con datos reales. La salida sería una tabla de totales mantenida por el mismo Observer, no volver al contador acumulado.
