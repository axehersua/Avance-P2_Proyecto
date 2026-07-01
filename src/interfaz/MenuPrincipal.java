package interfaz;

import modelo.*;
import negocio.GestorPrioridad;
import java.util.ArrayList;
import java.util.Scanner;

public class MenuPrincipal {

    private Scanner          scanner;
    private Estudiante       estudiante;
    private FormularioPerfil formularioPerfil;
    private VistaAgenda      vistaAgenda;
    private GestorPrioridad  gestorPrioridad;

    public MenuPrincipal(Scanner scanner, Estudiante estudiante) {
        this.scanner          = scanner;
        this.estudiante       = estudiante;
        this.gestorPrioridad  = new GestorPrioridad();
        this.formularioPerfil = new FormularioPerfil(scanner);
        this.vistaAgenda      = new VistaAgenda(scanner);
    }

    public void iniciar() {
        // RF06 - Perfil obligatorio antes de mostrar el menu
        if (estudiante.getPerfil() == null) {
            System.out.println();
            System.out.println("  Bienvenido, " + estudiante.getNombre() + "!");
            System.out.println("  Debes configurar tu perfil antes de continuar.");
            formularioPerfil.configurarPerfil(estudiante);
        } else {
            System.out.println();
            System.out.println("  Bienvenido de nuevo, " + estudiante.getNombre() + "!");
        }
        mostrarMenu();
    }

    private void mostrarMenu() {
        boolean ejecutando = true;

        while (ejecutando) {
            // Fecha del sistema en el encabezado
            java.time.LocalDate hoy = java.time.LocalDate.now();
            String fechaHoy = String.format("%02d/%02d/%d",
                    hoy.getDayOfMonth(), hoy.getMonthValue(), hoy.getYear());

            System.out.println();
            System.out.println("========================================");
            System.out.println("  MENU PRINCIPAL - " + estudiante.getNombre());
            System.out.println("  Hoy: " + fechaHoy);
            System.out.println("========================================");
            System.out.println("  1. Ver / Actualizar perfil");
            System.out.println("  2. Gestionar tareas");
            System.out.println("  3. Ver agenda del dia");
            System.out.println("  4. Consultar tareas por estado");
            System.out.println("  5. Ver agenda de un dia especifico");
            System.out.println("  6. Historial de agendas por rango");
            System.out.println("  7. Registrar penalizacion");
            System.out.println("  8. Historial de penalizaciones");
            System.out.println("  9. Ver recomendaciones");
            System.out.println("  0. Cerrar sesion");
            System.out.println("----------------------------------------");
            System.out.print("  Opcion: ");

            String opcion = scanner.nextLine().trim();

            if (opcion.equals("1")) {
                menuPerfil();
            } else if (opcion.equals("2")) {
                menuTareas();
            } else if (opcion.equals("3")) {
                vistaAgenda.mostrarAgendaDiaria(estudiante);
            } else if (opcion.equals("4")) {
                vistaAgenda.verTareasPorEstado(estudiante);
            } else if (opcion.equals("5")) {
                vistaAgenda.verAgendaPorFecha(estudiante);
            } else if (opcion.equals("6")) {
                vistaAgenda.verHistoricoAgendas(estudiante);
            } else if (opcion.equals("7")) {
                vistaAgenda.registrarPenalizacion(estudiante);
            } else if (opcion.equals("8")) {
                vistaAgenda.verHistorialPenalizaciones(estudiante);
            } else if (opcion.equals("9")) {
                vistaAgenda.mostrarRecomendaciones(estudiante);
            } else if (opcion.equals("0")) {
                ejecutando = false;
                System.out.println("  Sesion cerrada. Hasta luego, " +
                        estudiante.getNombre() + "!");
            } else {
                System.out.println("  Opcion no valida.");
            }
        }
    }

    private void menuPerfil() {
        System.out.println();
        System.out.println("  1. Actualizar perfil");
        System.out.println("  2. Ver perfil actual");
        System.out.print("  Opcion: ");
        String op = scanner.nextLine().trim();

        if (op.equals("1")) {
            formularioPerfil.configurarPerfil(estudiante);
        } else if (op.equals("2")) {
            formularioPerfil.mostrarPerfil(estudiante);
        } else {
            System.out.println("  Opcion no valida.");
        }
    }

    private void menuTareas() {
        boolean volver = false;

        while (!volver) {
            System.out.println();
            System.out.println("========================================");
            System.out.println("         GESTION DE TAREAS             ");
            System.out.println("========================================");
            System.out.println("  1. Registrar nueva tarea");
            System.out.println("  2. Ver todas las tareas");
            System.out.println("  3. Marcar tarea como completada");
            System.out.println("  4. Eliminar tarea");
            System.out.println("  0. Volver");
            System.out.print("  Opcion: ");

            String op = scanner.nextLine().trim();

            if (op.equals("1")) {
                registrarTarea();
            } else if (op.equals("2")) {
                listarTareas();
            } else if (op.equals("3")) {
                completarTarea();
            } else if (op.equals("4")) {
                eliminarTarea();
            } else if (op.equals("0")) {
                volver = true;
            } else {
                System.out.println("  Opcion no valida.");
            }
        }
    }

    // RF01 - Registrar tarea con validacion de fecha vencida
    private void registrarTarea() {
        System.out.println();
        System.out.println("  --- REGISTRAR TAREA ---");

        System.out.print("  Nombre de la tarea: ");
        String nombre = scanner.nextLine().trim();

        // Fecha validada: se vuelve a pedir mientras el formato sea incorrecto
        String deadline = leerFechaValida("  Fecha de entrega (DD/MM/YYYY): ");

        // Dificultad validada: solo se acepta un entero entre 1 y 5
        int dificultad = leerEntero("  Dificultad (1=facil, 5=muy dificil): ", 1, 5);

        // ID generado por el propio Estudiante: nunca se repite, ni tras eliminar tareas
        String id = estudiante.generarNuevoIdTarea();
        Tarea tarea = new Tarea(id, nombre, deadline, dificultad);

        // Calcular dias restantes automaticamente
        int dias = tarea.calcularDiasRestantes();

        // Validar si la fecha ya paso
        if (dias < 0) {
            System.out.println();
            System.out.println("  ! ATENCION: La fecha " + deadline +
                    " ya paso hace " + Math.abs(dias) + " dia(s).");
            System.out.println("  Esta tarea no puede agendarse para hoy.");
            System.out.print("  Deseas registrarla igual como ATRASADA? (s/n): ");
            String resp = scanner.nextLine().trim().toLowerCase();

            if (!resp.equals("s")) {
                System.out.println("  Tarea no registrada.");
                return;
            }
            gestorPrioridad.calcularPrioridad(tarea);
            estudiante.registrarTarea(tarea);
            System.out.println("  Tarea registrada como ATRASADA.");
            System.out.println("  Prioridad : " + tarea.getPrioridad());
            return;
        }

        // Aviso si vence hoy
        if (dias == 0) {
            System.out.println();
            System.out.println("  ! ATENCION: Esta tarea vence HOY.");
            System.out.println("  Se registrara con prioridad CRITICA.");
        }

        gestorPrioridad.calcularPrioridad(tarea);
        estudiante.registrarTarea(tarea);

        System.out.println("  Tarea registrada.");
        System.out.println("  Dias restantes : " + (dias == 0 ? "Vence HOY" : dias));
        System.out.println("  Prioridad      : " + tarea.getPrioridad());
    }

    // Pide una fecha en formato DD/MM/YYYY y no continua hasta que sea valida.
    // Corrige el bug donde una fecha mal escrita se interpretaba silenciosamente como "HOY".
    private String leerFechaValida(String mensaje) {
        java.time.format.DateTimeFormatter fmt =
                java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy");
        while (true) {
            System.out.print(mensaje);
            String fecha = scanner.nextLine().trim();
            try {
                java.time.LocalDate.parse(fecha, fmt);
                return fecha;
            } catch (Exception e) {
                System.out.println("  ! Formato invalido. Usa DD/MM/YYYY (ej: 15/07/2026).");
            }
        }
    }

    // Pide un numero entero dentro de un rango [min, max] y no continua hasta
    // que la entrada sea valida. Evita que el programa se cierre por
    // NumberFormatException cuando el usuario escribe texto en vez de un numero.
    private int leerEntero(String mensaje, int min, int max) {
        while (true) {
            System.out.print(mensaje);
            String entrada = scanner.nextLine().trim();
            try {
                int valor = Integer.parseInt(entrada);
                if (valor < min || valor > max) {
                    System.out.println("  ! Ingresa un numero entre " + min + " y " + max + ".");
                    continue;
                }
                return valor;
            } catch (NumberFormatException e) {
                System.out.println("  ! Eso no es un numero valido. Intenta de nuevo.");
            }
        }
    }

    private void listarTareas() {
        System.out.println();
        System.out.println("  --- LISTA DE TAREAS ---");

        ArrayList<Tarea> tareas = estudiante.getTareas();
        if (tareas.size() == 0) {
            System.out.println("  No hay tareas registradas.");
            return;
        }

        gestorPrioridad.recalcularTodas(tareas);
        gestorPrioridad.ordenarTareas(tareas);

        for (int i = 0; i < tareas.size(); i++) {
            tareas.get(i).mostrar();
            System.out.println();
        }

        gestorPrioridad.mostrarResumenPrioridades(tareas);
    }

    // Marca tarea como completada con fecha y hora del sistema
    private void completarTarea() {
        System.out.println();
        listarTareas();
        if (estudiante.getTareas().size() == 0) return;

        System.out.print("  ID de la tarea a completar (ej: T001): ");
        String id = scanner.nextLine().trim().toUpperCase();
        Tarea tarea = estudiante.buscarTareaPorId(id);

        if (tarea != null) {
            if (tarea.getEstado().equals(Tarea.COMPLETADA)) {
                System.out.println("  Esta tarea ya fue completada el " +
                        tarea.getFechaCompletada() +
                        " a las " + tarea.getHoraCompletada() + ".");
                return;
            }
            // Fecha y hora del sistema automaticamente
            tarea.completar();
            System.out.println("  Tarea \"" + tarea.getNombre() + "\" completada.");
            System.out.println("  Fecha : " + tarea.getFechaCompletada());
            System.out.println("  Hora  : " + tarea.getHoraCompletada());
        } else {
            System.out.println("  No se encontro la tarea: " + id);
        }
    }

    private void eliminarTarea() {
        System.out.println();
        listarTareas();
        if (estudiante.getTareas().size() == 0) return;

        System.out.print("  ID de la tarea a eliminar (ej: T001): ");
        String id = scanner.nextLine().trim().toUpperCase();
        Tarea tarea = estudiante.buscarTareaPorId(id);

        if (tarea != null) {
            estudiante.eliminarTarea(id);
            System.out.println("  Tarea \"" + tarea.getNombre() + "\" eliminada.");
        } else {
            System.out.println("  No se encontro la tarea: " + id);
        }
    }
}