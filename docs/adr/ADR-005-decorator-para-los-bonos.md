# ADR-005: Decorator con Template Method para calcular los puntos de una caza

## Estado: Aceptado (2026-11-11)

## Contexto

Una caza da puntos base según la rareza de la cocina (común 100, rara 150, legendaria 250) y puede sumar bonos: primer cazador de esa cocina (+50), racha de días seguidos (+10 por día, hasta 5 días) y hora fantasma, de 10 p. m. a 4 a. m. (x1,5). Los bonos se combinan entre sí y el orden importa: el multiplicador debe aplicarse sobre todo lo anterior. Además, la comanda que ve el cazador muestra el desglose línea por línea.

## Decisión

`CalculadoraPuntos` es la interfaz; `PuntosBasePorRareza` es el componente concreto y cada bono es un decorador que hereda de `BonificacionPuntos`. La clase base fija el esqueleto con un método `final` (Template Method): primero calcula lo envuelto y, si el bono aplica, agrega su aporte. La cadena se arma una sola vez en `ConfiguracionDominio`, con el multiplicador por fuera:

```text
MultiplicadorHoraFantasma( BonoRachaDiaria( BonoPrimerCazador( PuntosBasePorRareza ) ) )
```

## Alternativas consideradas

- **Un método con un `if` por bono.** Crece con cada bono nuevo y mezcla reglas que cambian por motivos distintos.
- **Chain of Responsibility.** Está pensado para que un eslabón atienda la petición y la corte; aquí todos aportan siempre.
- **Strategy.** Elige un algoritmo entre varios; aquí se necesitan varios a la vez, apilados.

## Consecuencias

Positivas:
- Agregar un bono es crear una clase y añadirla a la cadena; ninguna clase existente cambia.
- El desglose sale gratis: cada decorador agrega su línea al `Puntaje`.
- El método `final` impide que un decorador olvide delegar.

Negativas:
- El orden de la cadena es una regla de negocio escondida en la configuración. Para que no se pierda, está comentado en `ConfiguracionDominio` y PU-001 falla si alguien lo cambia (espera 525 puntos: (250 + 50 + 50) × 1,5).
- Para un lector nuevo, cuatro objetos anidados son menos directos que una suma.

## Señal para reconsiderar

Si los bonos pasaran a configurarse desde la administración (activar o desactivar uno, cambiar su valor), la cadena tendría que armarse en tiempo de ejecución a partir de datos, y convendría una fábrica de la cadena.
