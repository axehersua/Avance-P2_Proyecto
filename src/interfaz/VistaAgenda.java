package interfaz;

import modelo.*;
import negocio.GestorAgenda;
import negocio.GestorPrioridad;
import negocio.GestorRecomendaciones;
import java.util.ArrayList;
import java.util.Scanner;

public class VistaAgenda {

    private Scanner               scanner;
    private GestorAgenda          gestorAgenda;
    private GestorPrioridad       gestorPrioridad;
    private GestorRecomendaciones gestorRecomendaciones;

    public VistaAgenda(Scanner scanner) {
        this.scanner               = scanner;
        this.gestorAgenda          = new GestorAgenda();
        this.gestorPrioridad       = new GestorPrioridad();
        this.gestorRecomendaciones = new GestorRecomendaciones();
    }

    /**
     * RF05 - Agenda del dia
     * Si el perfil es de horario variable, pide el horario del dia al usuario.
     * Si es fijo, genera la agenda directamente.
     */
    public void mostrarAgendaDiaria(Estudiante estudiante) {
        System.out.println();
        System.out.println("========================================");
        System.out.println("          AGENDA DEL DIA               ");
        System.out.println("========================================");

        if (estudiante.getTareas().size() == 0) {
            System.out.println("  No tienes tareas registradas.");
            return;
        }

        AgendaDiaria agenda;
        PerfilUsuario perfil = estudiante.getPerfil();

        if (perfil != null && !perfil.isTieneHorarioFijo()) {
            // Horario variable: pedir horario del dia
            System.out.println("  Tu perfil tiene horario variable.");
            System.out.print("  Hora de inicio de hoy (ej: 07): ");
            String horaInicio = scanner.nextLine().trim() + ":00";
            System.out.print("  Hora en que puedes terminar hoy (ej: 22): ");
            String horaFin = scanner.nextLine().trim() + ":00";
            agenda = gestorAgenda.generarCronograma(estudiante, horaInicio, horaFin);
        } else {
            // Horario fijo: usar el configurado
            agenda = gestorAgenda.generarCronograma(estudiante);
        }

        agenda.mostrarAgenda();
        System.out.println();

        // Mostrar resumen de prioridades de tareas pendientes
        ArrayList<Tarea> pendientes = new ArrayList<Tarea>();
        for (int i = 0; i < estudiante.getTareas().size(); i++) {
            if (!estudiante.getTareas().get(i).getEstado().equals(Tarea.COMPLETADA)) {
                pendientes.add(estudiante.getTareas().get(i));
            }
        }
        if (pendientes.size() > 0) {
            gestorPrioridad.mostrarResumenPrioridades(pendientes);
        }
    }

    // Ver tareas filtradas por estado
    public void verTareasPorEstado(Estudiante estudiante) {
        System.out.println();
        System.out.println("========================================");
        System.out.println("       CONSULTA DE TAREAS              ");
        System.out.println("========================================");
        System.out.println("  1. Tareas pendientes");
        System.out.println("  2. Tareas completadas");
        System.out.println("  3. Tareas atrasadas");
        System.out.println("  4. Todas las tareas");
        System.out.print("  Opcion: ");

        String op = scanner.nextLine().trim();
        String filtro = "";

        if (op.equals("1"))      filtro = Tarea.PENDIENTE;
        else if (op.equals("2")) filtro = Tarea.COMPLETADA;
        else if (op.equals("3")) filtro = Tarea.ATRASADA;
        else if (op.equals("4")) filtro = "TODAS";
        else { System.out.println("  Opcion no valida."); return; }

        System.out.println();
        ArrayList<Tarea> tareas = estudiante.getTareas();

        if (tareas.size() == 0) {
            System.out.println("  No hay tareas registradas.");
            return;
        }

        gestorPrioridad.recalcularTodas(tareas);
        gestorPrioridad.ordenarTareas(tareas);

        int contador = 0;
        for (int i = 0; i < tareas.size(); i++) {
            Tarea t = tareas.get(i);
            if (filtro.equals("TODAS") || t.getEstado().equals(filtro)) {
                t.mostrar();
                System.out.println();
                contador++;
            }
        }

        if (contador == 0) {
            System.out.println("  No hay tareas con ese estado.");
        } else {
            System.out.println("  Total: " + contador + " tarea(s).");
        }
    }

    // Ver agenda de un dia especifico
    public void verAgendaPorFecha(Estudiante estudiante) {
        System.out.println();
        System.out.println("========================================");
        System.out.println("     AGENDA DE UN DIA ESPECIFICO       ");
        System.out.println("========================================");
        System.out.print("  Ingresa la fecha (DD/MM/YYYY): ");
        String fecha = scanner.nextLine().trim();
        gestorAgenda.verAgendaPorFecha(estudiante, fecha);
    }

    /**
     * Historial de agendas por rango de fechas
     * Ejemplo: agenda proxima del 1 al 6 de junio
     */
    public void verHistoricoAgendas(Estudiante estudiante) {
        System.out.println();
        System.out.println("========================================");
        System.out.println("      HISTORICO DE AGENDAS             ");
        System.out.println("========================================");
        System.out.println("  Ejemplo: del 01/06/2026 al 06/06/2026");
        System.out.print("  Fecha inicio (DD/MM/YYYY): ");
        String fechaInicio = scanner.nextLine().trim();
        System.out.print("  Fecha fin    (DD/MM/YYYY): ");
        String fechaFin = scanner.nextLine().trim();
        gestorAgenda.verHistoricoAgendas(estudiante, fechaInicio, fechaFin);
    }

    /**
     * Historial de penalizaciones con filtro por rango de fechas
     * Ejemplo: penalizaciones del ultimo mes
     */
    public void verHistorialPenalizaciones(Estudiante estudiante) {
        System.out.println();
        System.out.println("========================================");
        System.out.println("   HISTORIAL DE PENALIZACIONES         ");
        System.out.println("========================================");
        System.out.println("  1. Ver todas");
        System.out.println("  2. Filtrar por rango de fechas");
        System.out.print("  Opcion: ");

        String op = scanner.nextLine().trim();

        ArrayList<Penalizacion> lista = estudiante.getPenalizaciones();

        if (lista.size() == 0) {
            System.out.println("  No hay penalizaciones registradas.");
            return;
        }

        if (op.equals("1")) {
            mostrarPenalizaciones(lista);
        } else if (op.equals("2")) {
            System.out.print("  Fecha inicio (DD/MM/YYYY): ");
            String fechaInicio = scanner.nextLine().trim();
            System.out.print("  Fecha fin    (DD/MM/YYYY): ");
            String fechaFin = scanner.nextLine().trim();
            filtrarPenalizacionesPorRango(lista, fechaInicio, fechaFin);
        } else {
            System.out.println("  Opcion no valida.");
        }
    }

    // Muestra todas las penalizaciones
    private void mostrarPenalizaciones(ArrayList<Penalizacion> lista) {
        int totalMin = 0;
        for (int i = 0; i < lista.size(); i++) {
            totalMin += lista.get(i).getMinutosPercidos();
        }
        System.out.println("  Total: " + lista.size() + " penalizacion(es)");
        System.out.println("  Minutos perdidos en total: " + totalMin + " min");
        System.out.println();
        for (int i = 0; i < lista.size(); i++) {
            System.out.println("  " + lista.get(i).toString());
        }
    }

    // Filtra penalizaciones por rango de fechas DD/MM/YYYY
    private void filtrarPenalizacionesPorRango(ArrayList<Penalizacion> lista,
                                                String fechaInicio, String fechaFin) {
        System.out.println();
        System.out.println("  Penalizaciones del " + fechaInicio + " al " + fechaFin + ":");
        System.out.println("  " + "-".repeat(52));

        int contador = 0;
        int totalMin = 0;

        for (int i = 0; i < lista.size(); i++) {
            Penalizacion pen = lista.get(i);
            // Comparar fechas como strings en formato DD/MM/YYYY
            if (estaEnRango(pen.getFecha(), fechaInicio, fechaFin)) {
                System.out.println("  " + pen.toString());
                totalMin += pen.getMinutosPercidos();
                contador++;
            }
        }

        if (contador == 0) {
            System.out.println("  No hay penalizaciones en ese rango.");
        } else {
            System.out.println();
            System.out.println("  Total en el rango: " + contador + " penalizacion(es)");
            System.out.println("  Minutos perdidos : " + totalMin + " min");
        }
    }

    // Verifica si una fecha DD/MM/YYYY esta dentro de un rango
    private boolean estaEnRango(String fecha, String inicio, String fin) {
        try {
            java.time.format.DateTimeFormatter fmt =
                    java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy");
            java.time.LocalDate f  = java.time.LocalDate.parse(fecha, fmt);
            java.time.LocalDate i  = java.time.LocalDate.parse(inicio, fmt);
            java.time.LocalDate fi = java.time.LocalDate.parse(fin, fmt);
            return !f.isBefore(i) && !f.isAfter(fi);
        } catch (Exception e) {
            return false;
        }
    }

    // RF04 - Registrar penalizacion
    public void registrarPenalizacion(Estudiante estudiante) {
        System.out.println();
        System.out.println("========================================");
        System.out.println("       REGISTRAR PENALIZACION          ");
        System.out.println("========================================");

        System.out.print("  Motivo de la distraccion: ");
        String motivo = scanner.nextLine().trim();

        System.out.print("  Minutos perdidos: ");
        int minutos = Integer.parseInt(scanner.nextLine().trim());

        java.time.LocalDate hoy = java.time.LocalDate.now();
        String fecha = String.format("%02d/%02d/%d",
                hoy.getDayOfMonth(), hoy.getMonthValue(), hoy.getYear());

        String id = "PEN" + String.format("%03d",
                estudiante.getPenalizaciones().size() + 1);
        Penalizacion pen = new Penalizacion(id, minutos, motivo, fecha);

        AgendaDiaria agenda = gestorAgenda.generarCronograma(estudiante);
        AgendaDiaria nueva  = gestorAgenda.aplicarPenalizacion(pen, agenda, estudiante);

        System.out.println("  --- Agenda recalculada ---");
        nueva.mostrarAgenda();
    }

    // Genera y muestra recomendaciones detalladas
    public void mostrarRecomendaciones(Estudiante estudiante) {
        gestorRecomendaciones.generarRecomendaciones(estudiante);
    }
}
