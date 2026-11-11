# Matriz de trazabilidad

Cada caso de uso del protocolo se conecta con el patrón que lo atiende, las clases que lo implementan y las pruebas que lo verifican. Los identificadores PU y PI coinciden con el `@DisplayName` de cada prueba.

| Caso de uso | Patrón | Clases principales | Pruebas unitarias | Pruebas de integración |
|---|---|---|---|---|
| CU-01 Cazar una cocina | Command, Decorator, Observer | `CocinaControlador`, `FabricaComandosCazador`, `HistorialAcciones`, `DescubrirCocinaComando`, `RegistroCaza`, `CalculadoraPuntos` y bonos | PU-001, PU-002, PU-003, PU-004, PU-012, PU-013, PU-014 | PI-001, PI-003 |
| CU-02 Deshacer la última acción | Command, Factory Method | `AccionesControlador`, `HistorialAcciones`, `FabricaComandos`, `AccionRegistrada` | PU-009, PU-015, PU-016, PU-017, PU-023 | PI-002 |
| CU-03 Publicar una reseña | Command | `CocinaControlador`, `PublicarResenaComando`, `RegistroResenas`, `Descubrimiento`, `Resena` | PU-007, PU-014 | `CazarYDeshacerIT.resenar_sumaPuntosYSePuedeDeshacer`, PI-009 |
| CU-04 Explorar cocinas cercanas | Strategy | `ExplorarControlador`, `CocinasApi`, `ServicioCatalogo`, `FiltroCocinas`, `SelectorCriterioOrden`, `CriterioOrden` | PU-004, PU-011, PU-019 | PI-006, PI-009 |
| CU-05 Unirse a un reto y completarlo | Observer, Command | `RetosControlador`, `UnirseARetoComando`, `RegistroParticipaciones`, `SincronizadorRetos`, `AvanceRetos` | PU-008 | PI-004 |
| CU-06 Insignias y nivel automáticos | Observer | `NotificadorProgreso`, `SincronizadorInsignias`, `DetectorCambioNivel`, `TipoInsignia`, `Nivel` | PU-006, PU-010, PU-018, PU-020 | PI-001, PI-002, PI-010 |
| CU-07 Panel del cazador | Facade | `PerfilControlador`, `PanelCazadorFacade` | (se cubre con integración) | PI-009 |
| CU-08 Ranking | Ninguno a propósito | `RankingControlador`, `RankingApi`, `ServicioRanking`, `PuntosCazador` | PU-021 | PI-004, PI-006, PI-010, PI-011 |
| CU-09 Gestionar el catálogo | Builder | `AdminCocinasControlador`, `AdministracionCocinas`, `CocinaFantasma.Builder`, `FormularioCocina` | PU-005 | PI-008 |
| CU-10 Registrarse e iniciar sesión | Template Method | `CuentaControlador`, `ServicioCuentas`, `ServicioDetallesUsuario`, `RedireccionPorRol`, `ConfiguracionSeguridad` | PU-022, PU-023 | PI-005, PI-007 |

Además:

- `ArquitecturaTest` (ArchUnit) protege las decisiones de la sección 5: capas, dominio sin Spring, presentación sin entidades, sin ciclos, comandos encapsulados y observadores ordenados.
- `DatosDemoIT` (PI-011) garantiza que los datos de demostración cumplen las mismas reglas que la aplicación.
