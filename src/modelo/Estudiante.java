package modelo;

import java.util.ArrayList;

public class Estudiante {

    private String id;
    private String nombre;
    private String correo;
    private String contrasena;
    private PerfilUsuario perfil;                      // Relacion 1:1 con PerfilUsuario
    private ArrayList<Tarea> tareas;
    private ArrayList<EventoFijo> eventosFijos;
    private ArrayList<Penalizacion> penalizaciones;
    private ArrayList<AgendaDiaria> historicoAgendas;  // Relacion 1:* con AgendaDiaria

    public Estudiante(String id, String nombre, String correo, String contrasena) {
        this.id               = id;
        this.nombre           = nombre;
        this.correo           = correo;
        this.contrasena       = contrasena;
        this.tareas           = new ArrayList<Tarea>();
        this.eventosFijos     = new ArrayList<EventoFijo>();
        this.penalizaciones   = new ArrayList<Penalizacion>();
        this.historicoAgendas = new ArrayList<AgendaDiaria>();
    }

    public void registrarTarea(Tarea tarea) {
        tareas.add(tarea);
    }

    public void eliminarTarea(String idTarea) {
        for (int i = 0; i < tareas.size(); i++) {
            if (tareas.get(i).getId().equals(idTarea)) {
                tareas.remove(i);
                break;
            }
        }
    }

    public void agregarEventoFijo(EventoFijo evento) {
        eventosFijos.add(evento);
    }

    public void registrarPenalizacion(Penalizacion penalizacion) {
        penalizaciones.add(penalizacion);
    }

    // Guarda la agenda generada en el historico del estudiante
    public void guardarAgenda(AgendaDiaria agenda) {
        // Evitar duplicados: si ya existe una agenda para esa fecha, reemplazarla
        for (int i = 0; i < historicoAgendas.size(); i++) {
            if (historicoAgendas.get(i).getFecha().equals(agenda.getFecha())) {
                historicoAgendas.set(i, agenda);
                return;
            }
        }
        historicoAgendas.add(agenda);
    }

    public Tarea buscarTareaPorId(String id) {
        for (int i = 0; i < tareas.size(); i++) {
            if (tareas.get(i).getId().equals(id)) {
                return tareas.get(i);
            }
        }
        return null;
    }

    public String toString() {
        return "Estudiante: " + nombre + " | Correo: " + correo;
    }

    // Getters y Setters
    public String getId()                              { return id; }
    public void setId(String id)                       { this.id = id; }

    public String getNombre()                          { return nombre; }
    public void setNombre(String nombre)               { this.nombre = nombre; }

    public String getCorreo()                          { return correo; }
    public void setCorreo(String correo)               { this.correo = correo; }

    public String getContrasena()                      { return contrasena; }
    public void setContrasena(String contrasena)       { this.contrasena = contrasena; }

    public PerfilUsuario getPerfil()                   { return perfil; }
    public void setPerfil(PerfilUsuario perfil)        { this.perfil = perfil; }

    public ArrayList<Tarea> getTareas()                { return tareas; }
    public ArrayList<EventoFijo> getEventosFijos()     { return eventosFijos; }
    public ArrayList<Penalizacion> getPenalizaciones() { return penalizaciones; }
    public ArrayList<AgendaDiaria> getHistoricoAgendas() { return historicoAgendas; }
}
