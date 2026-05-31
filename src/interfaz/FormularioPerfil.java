package interfaz;

import modelo.Estudiante;
import modelo.EventoFijo;
import modelo.PerfilUsuario;
import java.util.ArrayList;
import java.util.Scanner;

public class FormularioPerfil {

    private Scanner scanner;

    public FormularioPerfil(Scanner scanner) {
        this.scanner = scanner;
    }

    /**
     * RF06 - Configurar perfil del estudiante
     * Primero pregunta si el estudiante tiene horario fijo de dormir o variable.
     * Si es fijo: pide hora de inicio y hora limite.
     * Si es variable: solo pide horas minimas de sueno (el horario se configura cada dia).
     */
    public void configurarPerfil(Estudiante estudiante) {
        System.out.println();
        System.out.println("========================================");
        System.out.println("   CONFIGURACION DE PERFIL             ");
        System.out.println("========================================");

        // Preguntar si tiene horario fijo de dormir
        System.out.println("  Tienes un horario fijo todos los dias?");
        System.out.println("  (Por ejemplo: siempre te levantas a las 7am y");
        System.out.println("   siempre te duermes a las 11pm)");
        System.out.print("  Responde (s/n): ");
        String resp = scanner.nextLine().trim().toLowerCase();
        boolean horarioFijo = resp.equals("s");

        PerfilUsuario perfil;

        if (horarioFijo) {
            // Configurar perfil con horario fijo
            System.out.print("  Hora de inicio del dia (ej: 07): ");
            String horaInicio = scanner.nextLine().trim() + ":00";

            System.out.print("  Hora limite para dormir (ej: 23): ");
            String horaLimite = scanner.nextLine().trim() + ":00";

            System.out.print("  Horas minimas de sueno (ej: 7): ");
            int horasSueno = Integer.parseInt(scanner.nextLine().trim());

            if (estudiante.getPerfil() != null) {
                perfil = estudiante.getPerfil();
                perfil.configurar(true, horaInicio, horaLimite, horasSueno);
            } else {
                perfil = new PerfilUsuario(true, horaInicio, horaLimite, horasSueno);
                estudiante.setPerfil(perfil);
            }

            System.out.println("  Horas disponibles estimadas por dia: " +
                    perfil.calcularHorasDisponibles() + "h");
        } else {
            // Configurar perfil con horario variable
            System.out.println("  Entendido. Cada dia podras ingresar tu horario disponible.");
            System.out.print("  Horas minimas de sueno que necesitas (ej: 7): ");
            int horasSueno = Integer.parseInt(scanner.nextLine().trim());

            if (estudiante.getPerfil() != null) {
                perfil = estudiante.getPerfil();
                perfil.configurar(false, null, null, horasSueno);
            } else {
                perfil = new PerfilUsuario(horasSueno);
                estudiante.setPerfil(perfil);
            }
        }

        // Agregar actividades fijas opcionales
        System.out.println();
        System.out.print("  Deseas agregar actividades fijas recurrentes? (s/n): ");
        String respAct = scanner.nextLine().trim();
        if (respAct.equals("s")) {
            agregarActividadesFijas(estudiante, estudiante.getPerfil());
        }

        System.out.println("  Perfil guardado correctamente.");
    }

    private void agregarActividadesFijas(Estudiante estudiante, PerfilUsuario perfil) {
        boolean continuar = true;
        int contador = 1;

        while (continuar) {
            System.out.println();
            System.out.println("  --- Actividad fija #" + contador + " ---");

            System.out.print("  Nombre: ");
            String nombre = scanner.nextLine().trim();

            System.out.print("  Dia (ej: LUNES): ");
            String dia = scanner.nextLine().trim().toUpperCase();

            System.out.print("  Hora de inicio (ej: 18:00): ");
            String horaInicio = scanner.nextLine().trim();

            System.out.print("  Hora de fin (ej: 20:00): ");
            String horaFin = scanner.nextLine().trim();

            System.out.print("  Duracion en horas (ej: 2): ");
            int duracion = Integer.parseInt(scanner.nextLine().trim());

            String id = "EV" + String.format("%03d", contador);
            EventoFijo evento = new EventoFijo(id, nombre, horaInicio, horaFin, dia, duracion);
            estudiante.agregarEventoFijo(evento);
            perfil.agregarActividadFija(nombre + " (" + dia + " " + horaInicio + "-" + horaFin + ")");

            System.out.print("  Agregar otra? (s/n): ");
            String r = scanner.nextLine().trim();
            continuar = r.equals("s");
            contador++;
        }
    }

    // RF06 - Mostrar perfil actual
    public void mostrarPerfil(Estudiante estudiante) {
        System.out.println();
        System.out.println("========================================");
        System.out.println("         PERFIL ACTUAL                 ");
        System.out.println("========================================");
        System.out.println("  " + estudiante.toString());
        System.out.println();

        if (estudiante.getPerfil() == null) {
            System.out.println("  Sin perfil configurado.");
        } else {
            estudiante.getPerfil().mostrarPerfil();
            System.out.println();

            ArrayList<EventoFijo> eventos = estudiante.getEventosFijos();
            System.out.println("  Eventos fijos registrados: " + eventos.size());
            for (int i = 0; i < eventos.size(); i++) {
                System.out.println("  " + eventos.get(i).toString());
            }
        }
    }
}
