package negocio;

import modelo.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class GestorAgenda {

    // Composicion: GestorAgenda contiene a GestorPrioridad
    private GestorPrioridad gestorPrioridad;

    public GestorAgenda() {
        this.gestorPrioridad = new GestorPrioridad();
    }

    /**
     * RF03 - Reordenamiento Inteligente
     * Genera el cronograma diario. Si el perfil tiene horario fijo lo usa
     * directamente. Si es variable, pide al usuario el horario del dia.
     * Guarda la agenda en el historico del estudiante.
     */
    public AgendaDiaria generarCronograma(Estudiante estudiante) {
        return generarCronograma(estudiante, null, null);
    }

    // Sobrecarga para horario variable (recibe horario del dia)
    public AgendaDiaria generarCronograma(Estudiante estudiante,
                                          String horaInicioDia,
                                          String horaFinDia) {
        LocalDate hoy   = LocalDate.now();
        String fechaHoy = String.format("%02d/%02d/%d",
                hoy.getDayOfMonth(), hoy.getMonthValue(), hoy.getYear());
        String diaHoy   = obtenerDiaSemana(hoy.getDayOfWeek().getValue());

        AgendaDiaria agenda  = new AgendaDiaria(fechaHoy);
        PerfilUsuario perfil = estudiante.getPerfil();

        if (perfil == null) {
            agenda.agregarAviso("! Configura tu perfil primero.");
            return agenda;
        }

        ArrayList<Tarea>      tareas  = estudiante.getTareas();

        gestorPrioridad.recalcularTodas(tareas);
        gestorPrioridad.ordenarTareas(tareas);

        // Calcular horas disponibles segun tipo de horario
        int horasDisponibles;
        if (perfil.isTieneHorarioFijo()) {
            // Horario fijo: usar el configurado en el perfil
            horasDisponibles = perfil.calcularHorasDisponibles();
        } else {
            // Horario variable: usar el horario pasado como parametro
            if (horaInicioDia != null && horaFinDia != null) {
                horasDisponibles = perfil.calcularHorasDisponiblesVariable(
                        horaInicioDia, horaFinDia);
            } else {
                horasDisponibles = 0;
            }
        }

        // Descontar eventos fijos del dia (y agregarlos como bloques a la agenda)
        int horasEventos = agregarEventosDelDia(agenda, estudiante, diaHoy);

        int horasReales = horasDisponibles - horasEventos;
        if (horasReales < 0) horasReales = 0;
        agenda.setHorasLibres(horasReales);

        // Asignar tareas a bloques
        asignarBloquesDeTareas(agenda, tareas, horasReales);

        // Guardar agenda en el historico del estudiante
        estudiante.guardarAgenda(agenda);

        return agenda;
    }

    // Agrega a la agenda los bloques de EVENTO correspondientes al dia indicado
    // y retorna el total de horas que esos eventos consumen.
    // Extraido como metodo reutilizable para que tanto generarCronograma()
    // como aplicarPenalizacion() muestren siempre los eventos fijos del dia.
    private int agregarEventosDelDia(AgendaDiaria agenda, Estudiante estudiante, String diaHoy) {
        int horasEventos = 0;
        ArrayList<EventoFijo> eventos = estudiante.getEventosFijos();
        for (int i = 0; i < eventos.size(); i++) {
            EventoFijo ev = eventos.get(i);
            if (ev.getDiaSemana().equals(diaHoy)) {
                horasEventos += ev.getDuracionHoras();
                agenda.agregarBloque(new BloqueHorario(
                        ev.getHoraInicio(), ev.getHoraFin(),
                        ev.getNombre(), BloqueHorario.TIPO_EVENTO));
            }
        }
        return horasEventos;
    }

    // Reparte las tareas pendientes en bloques AHORA / PUEDE ESPERAR
    // segun las horas reales disponibles.
    private void asignarBloquesDeTareas(AgendaDiaria agenda, ArrayList<Tarea> tareas, int horasReales) {
        int bloquesUsados     = 0;
        boolean seccionEspera = false;

        for (int i = 0; i < tareas.size(); i++) {
            Tarea tarea = tareas.get(i);
            if (tarea.getEstado().equals(Tarea.COMPLETADA)) continue;

            if (bloquesUsados < horasReales) {
                // Bloque AHORA con tipo TAREA
                BloqueHorario bloque = new BloqueHorario(
                        tarea.getNombre() + " | Prioridad: " + tarea.getPrioridad() +
                                " | Dificultad: " + tarea.getDificultad() + "/5" +
                                " | Dias restantes: " + tarea.calcularDiasRestantes(),
                        BloqueHorario.TIPO_TAREA);
                bloque.setEstado("AHORA");
                agenda.agregarBloque(bloque);
                bloquesUsados++;
            } else {
                if (!seccionEspera) {
                    agenda.agregarAviso("-- PUEDE ESPERAR PARA OTRO DIA --");
                    seccionEspera = true;
                }
                BloqueHorario bloque = new BloqueHorario(
                        tarea.getNombre() + " | Prioridad: " + tarea.getPrioridad(),
                        BloqueHorario.TIPO_TAREA);
                bloque.setEstado("ESPERAR");
                agenda.agregarBloque(bloque);
            }
        }

        if (bloquesUsados == 0 && !seccionEspera) {
            agenda.agregarAviso("Todas las tareas completadas. Puedes descansar.");
        }
    }

    // Ver agenda de una fecha especifica
    public void verAgendaPorFecha(Estudiante estudiante, String fechaConsulta) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate fechaBuscada;

        try {
            fechaBuscada = LocalDate.parse(fechaConsulta, fmt);
        } catch (Exception e) {
            System.out.println("  ! Formato invalido. Usa DD/MM/YYYY.");
            return;
        }

        String diaSemana = obtenerDiaSemana(fechaBuscada.getDayOfWeek().getValue());

        System.out.println();
        System.out.println("  Fecha: " + fechaConsulta + " (" + diaSemana + ")");
        System.out.println("  " + "-".repeat(52));

        boolean hayAlgo = false;

        // Eventos fijos del dia
        ArrayList<EventoFijo> eventos = estudiante.getEventosFijos();
        for (int i = 0; i < eventos.size(); i++) {
            EventoFijo ev = eventos.get(i);
            if (ev.getDiaSemana().equals(diaSemana)) {
                System.out.println("  [ EVENTO FIJO ] " + ev.getNombre() +
                        "  " + ev.getHoraInicio() + " - " + ev.getHoraFin());
                hayAlgo = true;
            }
        }

        // Tareas con deadline en esa fecha
        ArrayList<Tarea> tareas = estudiante.getTareas();
        for (int i = 0; i < tareas.size(); i++) {
            Tarea t = tareas.get(i);
            if (t.getDeadline().equals(fechaConsulta)) {
                System.out.println("  [ TAREA       ] " + t.getNombre() +
                        "  |  " + t.getPrioridad() +
                        "  |  Estado: " + t.getEstado());
                hayAlgo = true;
            }
        }

        if (!hayAlgo) {
            System.out.println("  No hay actividades programadas para este dia.");
        }
    }

    /**
     * Muestra el historico de agendas en un rango de fechas
     * Formato: DD/MM/YYYY
     */
    public void verHistoricoAgendas(Estudiante estudiante,
                                    String fechaInicio, String fechaFin) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate inicio, fin;

        try {
            inicio = LocalDate.parse(fechaInicio, fmt);
            fin    = LocalDate.parse(fechaFin, fmt);
        } catch (Exception e) {
            System.out.println("  ! Formato invalido. Usa DD/MM/YYYY.");
            return;
        }

        ArrayList<AgendaDiaria> historico = estudiante.getHistoricoAgendas();
        int encontradas = 0;

        System.out.println();
        System.out.println("  Agendas del " + fechaInicio + " al " + fechaFin + ":");
        System.out.println("  " + "-".repeat(52));

        for (int i = 0; i < historico.size(); i++) {
            AgendaDiaria ag = historico.get(i);
            try {
                LocalDate fechaAg = LocalDate.parse(ag.getFecha(), fmt);
                // Verificar si la fecha esta dentro del rango
                if (!fechaAg.isBefore(inicio) && !fechaAg.isAfter(fin)) {
                    ag.mostrarAgenda();
                    System.out.println();
                    encontradas++;
                }
            } catch (Exception e) {
                // Si la fecha no es valida, ignorar
            }
        }

        if (encontradas == 0) {
            System.out.println("  No hay agendas registradas en ese rango.");
        } else {
            System.out.println("  Total de agendas encontradas: " + encontradas);
        }
    }

    /**
     * RF04 - Aplica penalizacion y recalcula agenda.
     * La agenda recalculada ahora conserva los bloques de EVENTO del dia
     * (antes desaparecian al reconstruir la agenda desde cero).
     */
    public AgendaDiaria aplicarPenalizacion(Penalizacion pen,
                                            AgendaDiaria agenda,
                                            Estudiante estudiante) {
        System.out.println();
        System.out.println("  " + pen.generarAlerta());
        System.out.println();

        estudiante.registrarPenalizacion(pen);

        int impactoHoras = pen.calcularImpacto() / 60;
        int horasNuevas  = agenda.getHorasLibres() - impactoHoras;
        if (horasNuevas < 0) horasNuevas = 0;

        LocalDate hoy   = LocalDate.now();
        String fechaHoy = String.format("%02d/%02d/%d",
                hoy.getDayOfMonth(), hoy.getMonthValue(), hoy.getYear());
        String diaHoy   = obtenerDiaSemana(hoy.getDayOfWeek().getValue());

        AgendaDiaria nueva = new AgendaDiaria(fechaHoy);
        nueva.agregarAviso("-- AGENDA RECALCULADA tras penalizacion --");

        // Volver a agregar los eventos fijos del dia para que no desaparezcan
        // de la agenda recalculada (entrenamientos, clases, etc.)
        agregarEventosDelDia(nueva, estudiante, diaHoy);

        nueva.setHorasLibres(horasNuevas);

        ArrayList<Tarea> tareas = estudiante.getTareas();
        gestorPrioridad.ordenarTareas(tareas);

        asignarBloquesDeTareas(nueva, tareas, horasNuevas);

        // Guardar agenda recalculada en historico
        estudiante.guardarAgenda(nueva);

        return nueva;
    }

    /**
     * Convierte numero del dia (1=Lunes ... 7=Domingo) a nombre sin acentos
     * Evita el bug de comparacion MIERCOLES vs MIÉRCOLES
     */
    private String obtenerDiaSemana(int numeroDia) {
        String[] dias = {
                "LUNES",      // 1
                "MARTES",     // 2
                "MIERCOLES",  // 3 - sin acento
                "JUEVES",     // 4
                "VIERNES",    // 5
                "SABADO",     // 6 - sin acento
                "DOMINGO"     // 7
        };
        return dias[numeroDia - 1];
    }
}