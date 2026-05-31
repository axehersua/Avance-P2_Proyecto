package modelo;

import java.util.ArrayList;

public class PerfilUsuario {

    private boolean tieneHorarioFijo;    // true = mismo horario todos los dias
    private String  horaInicioDia;       // Formato "HH:mm" - solo si tieneHorarioFijo
    private String  horaLimiteSueno;     // Hora maxima para dormir
    private int     horasMinSueno;       // Horas minimas de sueno requeridas
    private ArrayList<String> actividadesFijas;

    // Constructor para perfil con horario fijo
    public PerfilUsuario(boolean tieneHorarioFijo, String horaInicioDia,
                         String horaLimiteSueno, int horasMinSueno) {
        this.tieneHorarioFijo  = tieneHorarioFijo;
        this.horaInicioDia     = horaInicioDia;
        this.horaLimiteSueno   = horaLimiteSueno;
        this.horasMinSueno     = horasMinSueno;
        this.actividadesFijas  = new ArrayList<String>();
    }

    // Constructor para perfil sin horario fijo (horario variable por dia)
    public PerfilUsuario(int horasMinSueno) {
        this.tieneHorarioFijo = false;
        this.horaInicioDia    = null;
        this.horaLimiteSueno  = null;
        this.horasMinSueno    = horasMinSueno;
        this.actividadesFijas = new ArrayList<String>();
    }

    // Actualiza el perfil manteniendo el tipo de horario
    public void configurar(boolean tieneHorarioFijo, String horaInicio,
                           String horaLimite, int horasSueno) {
        this.tieneHorarioFijo = tieneHorarioFijo;
        this.horaInicioDia    = horaInicio;
        this.horaLimiteSueno  = horaLimite;
        this.horasMinSueno    = horasSueno;
        System.out.println("  Perfil actualizado correctamente.");
    }

    public void agregarActividadFija(String actividad) {
        actividadesFijas.add(actividad);
    }

    // Calcula horas disponibles con horario fijo
    public int calcularHorasDisponibles() {
        if (!tieneHorarioFijo || horaInicioDia == null || horaLimiteSueno == null) {
            return 0;
        }
        int inicioHora = Integer.parseInt(horaInicioDia.split(":")[0]);
        int limiteHora = Integer.parseInt(horaLimiteSueno.split(":")[0]);
        int totalHoras = limiteHora - inicioHora;
        int disponible = totalHoras - horasMinSueno;
        if (disponible < 0) return 0;
        return disponible;
    }

    // Calcula horas disponibles con horario variable (se pasa el horario del dia)
    public int calcularHorasDisponiblesVariable(String horaInicio, String horaFin) {
        int inicioHora = Integer.parseInt(horaInicio.split(":")[0]);
        int finHora    = Integer.parseInt(horaFin.split(":")[0]);
        int totalHoras = finHora - inicioHora;
        int disponible = totalHoras - horasMinSueno;
        if (disponible < 0) return 0;
        return disponible;
    }

    public void mostrarPerfil() {
        System.out.println("  Tipo de horario   : " +
                (tieneHorarioFijo ? "FIJO" : "VARIABLE (se configura cada dia)"));
        if (tieneHorarioFijo) {
            System.out.println("  Hora de inicio    : " + horaInicioDia);
            System.out.println("  Hora limite sueno : " + horaLimiteSueno);
        }
        System.out.println("  Horas min. sueno  : " + horasMinSueno + "h");
        if (tieneHorarioFijo) {
            System.out.println("  Horas disponibles : " + calcularHorasDisponibles() + "h/dia");
        }
        String actividades = actividadesFijas.size() == 0 ? "Ninguna" : "";
        for (int i = 0; i < actividadesFijas.size(); i++) {
            actividades += actividadesFijas.get(i);
            if (i < actividadesFijas.size() - 1) actividades += ", ";
        }
        System.out.println("  Actividades fijas : " + actividades);
    }

    public String toString() {
        return "Horario: " + (tieneHorarioFijo ? "FIJO (" + horaInicioDia +
               " - " + horaLimiteSueno + ")" : "VARIABLE") +
               " | Sueno min: " + horasMinSueno + "h";
    }

    // Getters y Setters
    public boolean isTieneHorarioFijo()                  { return tieneHorarioFijo; }
    public void setTieneHorarioFijo(boolean v)           { this.tieneHorarioFijo = v; }

    public String getHoraInicioDia()                     { return horaInicioDia; }
    public void setHoraInicioDia(String h)               { this.horaInicioDia = h; }

    public String getHoraLimiteSueno()                   { return horaLimiteSueno; }
    public void setHoraLimiteSueno(String h)             { this.horaLimiteSueno = h; }

    public int getHorasMinSueno()                        { return horasMinSueno; }
    public void setHorasMinSueno(int h)                  { this.horasMinSueno = h; }

    public ArrayList<String> getActividadesFijas()       { return actividadesFijas; }
}
