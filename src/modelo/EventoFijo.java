package modelo;

public class EventoFijo extends Actividad {

    private String horaInicio;
    private String horaFin;
    private String diaSemana;
    private int    duracionHoras;

    public EventoFijo(String id, String nombre, String horaInicio,
                      String horaFin, String diaSemana, int duracionHoras) {
        super(id, nombre);
        this.horaInicio    = horaInicio;
        this.horaFin       = horaFin;
        this.diaSemana     = diaSemana.toUpperCase();
        this.duracionHoras = duracionHoras;
    }

    public String toString() {
        return "[" + getId() + "] " + getNombre() +
               " | Dia: " + diaSemana +
               " | Horario: " + horaInicio + " - " + horaFin +
               " | Duracion: " + duracionHoras + "h";
    }

    public String getHoraInicio() { return horaInicio; }
    public void setHoraInicio(String horaInicio) { this.horaInicio = horaInicio; }

    public String getHoraFin() { return horaFin; }
    public void setHoraFin(String horaFin) { this.horaFin = horaFin; }

    public String getDiaSemana() { return diaSemana; }
    public void setDiaSemana(String diaSemana) { this.diaSemana = diaSemana.toUpperCase(); }

    public int getDuracionHoras() { return duracionHoras; }
    public void setDuracionHoras(int duracionHoras) { this.duracionHoras = duracionHoras; }
}
