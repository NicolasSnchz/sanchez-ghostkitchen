# ADR-002. Command con historial persistido y deshacer en 15 minutos

**Estado:** aceptada

## Contexto

El patrón asignado es Command: encapsular solicitudes como objetos con historial. En el juego, un cazador puede marcar mal una cocina o publicar una reseña por error, y lo justo es dejarlo corregir. Pero en una aplicación web cada petición crea objetos nuevos: el comando que se ejecutó ya no existe cuando el usuario pulsa «Deshacer».

## Decisión

- Tres comandos concretos (`DescubrirCocinaComando`, `PublicarResenaComando`, `UnirseARetoComando`) que implementan `Comando` con `ejecutar()` y `deshacer()`. Son `final` y de paquete: solo la fábrica los crea.
- `HistorialAcciones` es el invocador: ejecuta, guarda una `AccionRegistrada` con lo mínimo para revertir (tipo, cazador y objetivo) y avisa a los observadores.
- Para deshacer, `FabricaComandos.paraDeshacer()` (Factory Method) reconstruye el comando desde la entrada del historial.
- Se deshace como una pila (siempre la última acción vigente) y solo durante 15 minutos, medidos con un `Clock` inyectado.
- No hay rehacer.

## Alternativas consideradas

- **Guardar el comando serializado en la sesión.** Se perdería al cerrar sesión y acoplaría el historial a la memoria del servidor.
- **Memento con instantáneas completas.** Copiar el estado del cazador en cada acción es más pesado y no hace falta: cada comando sabe cómo revertirse.
- **Rehacer.** No aporta al juego y abre casos raros (por ejemplo, rehacer una caza cuando la cocina ya cerró).

## Consecuencias

- Una reseña reconstruida desde el historial no trae su texto, así que ese comando solo puede deshacerse (`soloParaDeshacer`). Es una limitación aceptada y está cubierta por pruebas.
- El orden LIFO más la ventana de tiempo impiden deshacer una caza mientras su reseña sigue vigente: primero hay que deshacer la reseña.
