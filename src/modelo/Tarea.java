package modelo;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class Tarea extends Actividad {

    // Estados posibles de una tarea
    public static final String PENDIENTE  = "PENDIENTE";
    public static final String COMPLETADA = "COMPLETADA";
    public static final String ATRASADA   = "ATRASADA";

    // Niveles de prioridad
    public static final String PRIORIDAD_BAJA    = "BAJA";
    public static final String PRIORIDAD_MEDIA   = "MEDIA";
    public static final String PRIORIDAD_ALTA    = "ALTA";
    public static final String PRIORIDAD_CRITICA = "CRITICA";

    private String deadline;         // Formato DD/MM/YYYY
    private int    dificultad;       // Escala 1-5
    private String prioridad;
    private String estado;
    private String fechaCompletada;  // Se llena automaticamente al completar
    private String horaCompletada;   // Se llena automaticamente al completar

    public Tarea(String id, String nombre, String deadline, int dificultad) {
        super(id, nombre);
        this.deadline        = deadline;
        this.dificultad      = dificultad;
        this.estado          = PENDIENTE;   // Toda tarea nueva nace en PENDIENTE
        this.prioridad       = PRIORIDAD_BAJA; // La prioridad real la calcula GestorPrioridad
        this.fechaCompletada = null;
        this.horaCompletada  = null;
    }

    // Calcula automaticamente los dias restantes usando la fecha del sistema
    // Formato esperado del deadline: DD/MM/YYYY
    public int calcularDiasRestantes() {
        try {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate fechaDL = LocalDate.parse(deadline, fmt);
            LocalDate hoy     = LocalDate.now();
            return (int) ChronoUnit.DAYS.between(hoy, fechaDL);
        } catch (Exception e) {
            return 0;
        }
    }

    // RF - Marca la tarea como completada y guarda fecha y hora del sistema automaticamente
    // No requiere que el usuario ingrese la fecha, la toma de LocalDate y LocalTime
    public void completar() {
        this.estado = COMPLETADA;
        LocalDate hoy   = LocalDate.now();
        LocalTime ahora = LocalTime.now();
        // Guardar fecha en formato DD/MM/YYYY
        this.fechaCompletada = String.format("%02d/%02d/%d",
                hoy.getDayOfMonth(), hoy.getMonthValue(), hoy.getYear());
        // Guardar hora en formato HH:mm
        this.horaCompletada  = String.format("%02d:%02d",
                ahora.getHour(), ahora.getMinute());
    }

    // Actualiza el estado de la tarea
    // Si se marca como ATRASADA, registra la fecha del sistema
    public void actualizarEstado(String nuevoEstado) {
        this.estado = nuevoEstado;
        if (nuevoEstado.equals(ATRASADA)) {
            LocalDate hoy = LocalDate.now();
            this.fechaCompletada = String.format("%02d/%02d/%d",
                    hoy.getDayOfMonth(), hoy.getMonthValue(), hoy.getYear());
        }
    }

    // Muestra la informacion completa de la tarea con formato detallado
    public void mostrar() {
        int dias = calcularDiasRestantes();
        String diasStr = dias < 0
                ? "VENCIDA hace " + Math.abs(dias) + " dia(s)"
                : dias == 0 ? "Vence HOY"
                : "Faltan " + dias + " dia(s)";

        System.out.println("  [" + getId() + "] " + getNombre());
        System.out.println("      Entrega    : " + deadline + " (" + diasStr + ")");
        System.out.println("      Dificultad : " + dificultad + "/5" +
                           "  |  Prioridad: " + prioridad +
                           "  |  Estado: " + estado);
        // Muestra el rastro solo si la tarea fue completada
        if (estado.equals(COMPLETADA) && fechaCompletada != null) {
            System.out.println("      Completada : " + fechaCompletada +
                               " a las " + horaCompletada);
        }
    }

    // Representacion en una sola linea para listas
    public String toString() {
        int dias = calcularDiasRestantes();
        String diasStr = dias < 0 ? "VENCIDA" : dias == 0 ? "Hoy" : dias + " dia(s)";
        String base = "[" + getId() + "] " + getNombre() +
                      " | Entrega: " + deadline +
                      " | Dias restantes: " + diasStr +
                      " | Dificultad: " + dificultad + "/5" +
                      " | Prioridad: " + prioridad +
                      " | Estado: " + estado;
        // Agrega rastro de completada si aplica
        if (estado.equals(COMPLETADA) && fechaCompletada != null) {
            base += " | Completada: " + fechaCompletada + " " + horaCompletada;
        }
        return base;
    }

    // Getters y Setters
    public String getDeadline()                { return deadline; }
    public void setDeadline(String deadline)   { this.deadline = deadline; }

    public int getDificultad()                 { return dificultad; }
    public void setDificultad(int d)           { this.dificultad = d; }

    public String getPrioridad()               { return prioridad; }
    public void setPrioridad(String prioridad) { this.prioridad = prioridad; }

    public String getEstado()                  { return estado; }
    public String getFechaCompletada()         { return fechaCompletada; }
    public String getHoraCompletada()          { return horaCompletada; }
}
