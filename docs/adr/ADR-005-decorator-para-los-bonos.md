# ADR-005. Decorator para calcular los puntos de una caza

**Estado:** aceptada

## Contexto

Una caza da puntos base según la rareza de la cocina (100, 150 o 250) y puede sumar bonos: primer cazador de esa cocina (+50), racha de días seguidos (+10 por día, hasta 5) y hora fantasma, de 10 p. m. a 4 a. m. (x1,5). El orden importa: el multiplicador debe aplicarse sobre todo lo anterior. Además el juego necesita mostrar el desglose en la comanda.

## Decisión

`CalculadoraPuntos` es la interfaz; `PuntosBasePorRareza` es el componente concreto y cada bono es un decorador que hereda de `BonificacionPuntos`. La clase base fija el esqueleto con un método `final` (Template Method): primero calcula lo envuelto y, si el bono aplica, lo suma. La cadena se arma una sola vez en `ConfiguracionDominio`.

## Alternativas consideradas

- **Un método con if/else por bono.** Crece con cada bono nuevo y mezcla reglas que cambian por motivos distintos.
- **Chain of Responsibility.** Está pensado para que un solo eslabón atienda la petición; aquí todos aportan.
- **Strategy.** Elige un algoritmo entre varios; aquí se necesitan varios a la vez.

## Consecuencias

- Agregar un bono es crear una clase y añadirla a la cadena; ninguna clase existente cambia.
- El desglose sale gratis: cada decorador agrega su línea al `Puntaje`.
- Las pruebas PU-001 y PU-002 verifican el orden y la delegación.
