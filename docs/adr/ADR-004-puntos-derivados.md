# ADR-004. Puntos, nivel e insignias se derivan, no se acumulan

**Estado:** aceptada

## Contexto

Lo natural sería guardar un campo `puntos` en el cazador y sumarle cada vez que caza. Pero al deshacer acciones, completar retos o revertirlos, ese contador puede quedar desincronizado con lo que de verdad pasó.

## Decisión

- Los puntos se calculan siempre con `PuntosCazador.total()` a partir de las cazas, las reseñas y las recompensas de retos completados. El panel y el ranking usan la misma fórmula.
- El nivel se deriva de los puntos con `Nivel.paraPuntos()`.
- Las insignias sí se guardan (para conservar la fecha en que se ganaron), pero el observador las sincroniza con las reglas en cada cambio: otorga las nuevas y retira las que ya no se merecen.

## Consecuencias

- Deshacer cualquier acción deja los puntos correctos sin lógica extra.
- El ranking hace consultas de agregación (con proyecciones record) en vez de leer un campo; con el volumen del proyecto no hay problema de rendimiento.
- `DatosDemoIT` comprueba que los puntos del ranking coinciden con los del panel de cada cazador.
