package vista;

/**
 * Implementada por cada panel del dashboard que necesita recargar sus
 * datos desde el Estudiante cada vez que el usuario navega a esa seccion
 * (por ejemplo, si se registro una tarea nueva desde otra pantalla).
 */
public interface Refrescable {
    void refrescar();
}