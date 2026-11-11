package co.edu.udes.ghostkitchen.aplicacion.catalogo;

/** Si el visitante puede cazar una cocina ahora mismo y, si no, por que. */
public enum EstadoCaza {
    DISPONIBLE,
    YA_CAZADA,
    CERRADA,
    LIMITE_DIARIO,
    SIN_SESION
}
