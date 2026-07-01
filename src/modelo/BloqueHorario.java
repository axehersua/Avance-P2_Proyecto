package modelo;

public class BloqueHorario {

    // Tipos posibles de bloque en la agenda
    public static final String TIPO_TAREA    = "TAREA";
    public static final String TIPO_EVENTO   = "EVENTO";
    public static final String TIPO_DESCANSO = "DESCANSO";
    public static final String TIPO_AVISO    = "AVISO";

    private String horaInicio;    // Formato HH:mm, puede ser null si no aplica
    private String horaFin;       // Formato HH:mm, puede ser null si no aplica
    private String descripcion;   // Texto descriptivo del bloque
    private String tipo;          // TAREA, EVENTO, DESCANSO o AVISO
    private String estado;        // Estado de la tarea si aplica (PENDIENTE, COMPLETADA, etc.)

    // Constructor completo con horario
    public BloqueHorario(String horaInicio, String horaFin,
                         String descripcion, String tipo) {
        this.horaInicio  = horaInicio;
        this.horaFin     = horaFin;
        this.descripcion = descripcion;
        this.tipo        = tipo;
        this.estado      = "";
    }

    // Constructor sin horario (para avisos y separadores)
    public BloqueHorario(String descripcion, String tipo) {
        this.horaInicio  = null;
        this.horaFin     = null;
        this.descripcion = descripcion;
        this.tipo        = tipo;
        this.estado      = "";
    }

    // Formatea el bloque para mostrarlo en consola
    public void mostrar() {
        if (horaInicio != null && horaFin != null) {
            System.out.printf("  [%s - %s] %-10s %s%n",
                    horaInicio, horaFin, "[" + tipo + "]", descripcion);
        } else {
            System.out.println("  " + descripcion);
        }
    }

    public String toString() {
        if (horaInicio != null && horaFin != null) {
            return "[" + horaInicio + " - " + horaFin + "] " +
                   "[" + tipo + "] " + descripcion;
        }
        return "[" + tipo + "] " + descripcion;
    }

    // Getters y Setters
    public String getHoraInicio()                  { return horaInicio; }
    public void setHoraInicio(String horaInicio)   { this.horaInicio = horaInicio; }

    public String getHoraFin()                     { return horaFin; }
    public void setHoraFin(String horaFin)         { this.horaFin = horaFin; }

    public String getDescripcion()                 { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getTipo()                        { return tipo; }
    public void setTipo(String tipo)               { this.tipo = tipo; }

    public String getEstado()                      { return estado; }
    public void setEstado(String estado)           { this.estado = estado; }
}
