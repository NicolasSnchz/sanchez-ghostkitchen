# ADR-002: Command con historial persistido, deshacer en pila y ventana de 15 minutos

## Estado: Aceptado (2026-11-11)

## Contexto

El patrón asignado es Command: encapsular cada solicitud como un objeto que se puede registrar y revertir. En el juego, un cazador puede marcar por error una cocina que no visitó, publicar una reseña equivocada o unirse al reto que no era, y lo justo es dejarlo corregir. El problema es que en una aplicación web cada petición crea objetos nuevos: el comando que se ejecutó ya no existe cuando el usuario pulsa «Deshacer» en la siguiente petición.

También hay que evitar el abuso: si se pudiera deshacer cualquier cosa en cualquier momento, alguien podría cazar una cocina a media tarde y, horas después, deshacer y volver a cazarla en la hora fantasma para ganar el x1,5.

## Decisión

- Tres comandos concretos (`DescubrirCocinaComando`, `PublicarResenaComando`, `UnirseARetoComando`) implementan `Comando` con `ejecutar()` y `deshacer()`. Son `final` y de paquete; ArchUnit lo exige (ARQ-05), así que solo la fábrica los puede crear.
- `HistorialAcciones` es el invocador: ejecuta, guarda una `AccionRegistrada` con lo mínimo para revertir (tipo, cazador y objetivo) y avisa a los observadores.
- Para deshacer, `FabricaComandos.paraDeshacer()` (Factory Method) reconstruye el comando desde la entrada del historial.
- Se deshace como una pila, siempre la última acción vigente, y solo durante 15 minutos medidos con un `Clock` inyectado.
- No hay rehacer.

## Alternativas consideradas

- **Guardar el comando serializado en la sesión HTTP.** Se perdería al cerrar sesión o reiniciar el servidor, y el historial no se podría mostrar en el panel.
- **Memento con instantáneas del estado del cazador.** Habría que copiar cazas, reseñas, participaciones e insignias en cada acción. No hace falta: cada comando sabe revertir exactamente lo que hizo y los puntos se recalculan ([ADR-004](ADR-004-puntos-derivados.md)).
- **Deshacer cualquier acción de la lista, no solo la última.** Rompe dependencias: se podría deshacer una caza dejando viva la reseña que solo existe porque hubo caza.
- **Rehacer.** Abre casos raros, como rehacer una caza cuando la cocina ya cerró, y nadie lo pidió (YAGNI).

## Consecuencias

Positivas:
- El historial sobrevive a la sesión y se muestra en el panel con las acciones deshechas tachadas.
- El orden LIFO garantiza coherencia sin reglas extra: para deshacer una caza que tiene reseña, primero hay que deshacer la reseña.
- La ventana con `Clock` se prueba de forma determinista (PU-016) y corta el abuso del multiplicador nocturno.

Negativas:
- Una reseña reconstruida desde el historial no trae su texto, así que ese comando solo sirve para deshacer (`soloParaDeshacer`). Es una limitación aceptada y está cubierta por pruebas.
- Pasados 15 minutos la acción queda firme y no se puede corregir desde la interfaz. Es la regla del juego, no un descuido: sin ese límite el multiplicador nocturno se podría explotar.

## Señal para reconsiderar

Si los usuarios piden deshacer acciones antiguas o rehacer, o si aparecen acciones sin una forma natural de revertirse (por ejemplo, editar una reseña), sería el momento de evaluar Memento para esa acción concreta.
