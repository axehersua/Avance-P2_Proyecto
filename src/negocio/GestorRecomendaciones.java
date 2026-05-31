package negocio;

import modelo.*;
import java.util.ArrayList;

public class GestorRecomendaciones {

    /*
     * Genera un conjunto de recomendaciones detalladas basadas en:
       - Estado actual de las tareas (criticas, atrasadas)
       - Historial de penalizaciones
       - Horas disponibles del perfil
       - Sugerencia de que tarea hacer primero y cuanto tiempo dedicarle
     */
    public void generarRecomendaciones(Estudiante estudiante) {
        System.out.println();
        System.out.println("========================================");
        System.out.println("        RECOMENDACIONES DEL DIA        ");
        System.out.println("========================================");

        if (estudiante.getPerfil() == null) {
            System.out.println("  ! Configura tu perfil para recibir recomendaciones.");
            return;
        }

        ArrayList<Tarea> tareas = estudiante.getTareas();

        if (tareas.size() == 0) {
            System.out.println("  No tienes tareas registradas.");
            System.out.println("  Recomendacion: Registra tus proximas entregas");
            System.out.println("  para que el sistema pueda organizarte el dia.");
            return;
        }

        // Recalcular prioridades antes de analizar
        GestorPrioridad gestorPrioridad = new GestorPrioridad();
        gestorPrioridad.recalcularTodas(tareas);
        gestorPrioridad.ordenarTareas(tareas);

        // Clasificar tareas por estado
        ArrayList<Tarea> criticas   = new ArrayList<Tarea>();
        ArrayList<Tarea> altas      = new ArrayList<Tarea>();
        ArrayList<Tarea> atrasadas  = new ArrayList<Tarea>();
        ArrayList<Tarea> pendientes = new ArrayList<Tarea>();
        int completadas = 0;

        for (int i = 0; i < tareas.size(); i++) {
            Tarea t = tareas.get(i);
            if (t.getEstado().equals(Tarea.COMPLETADA)) {
                completadas++;
            } else if (t.getEstado().equals(Tarea.ATRASADA)) {
                atrasadas.add(t);
            } else if (t.getPrioridad().equals(Tarea.PRIORIDAD_CRITICA)) {
                criticas.add(t);
            } else if (t.getPrioridad().equals(Tarea.PRIORIDAD_ALTA)) {
                altas.add(t);
            } else {
                pendientes.add(t);
            }
        }

        int contador = 1;

        // Recomendacion sobre tareas atrasadas
        if (atrasadas.size() > 0) {
            System.out.println();
            System.out.println("  " + contador + ". ATENCION - TAREAS ATRASADAS");
            System.out.println("     Tienes " + atrasadas.size() + " tarea(s) vencida(s):");
            for (int i = 0; i < atrasadas.size(); i++) {
                System.out.println("     - " + atrasadas.get(i).getNombre() +
                        " (vencio hace " + Math.abs(atrasadas.get(i).calcularDiasRestantes()) +
                        " dia(s))");
            }
            System.out.println("     Accion sugerida: Contacta a tu profesor lo antes");
            System.out.println("     posible para consultar si aun puedes entregarlas.");
            contador++;
        }

        // Recomendacion sobre tareas criticas
        if (criticas.size() > 0) {
            System.out.println();
            System.out.println("  " + contador + ". TAREAS CRITICAS HOY");
            System.out.println("     Tienes " + criticas.size() + " tarea(s) que vencen pronto:");

            for (int i = 0; i < criticas.size(); i++) {
                Tarea t = criticas.get(i);
                int tiempoSugerido = t.getDificultad() * 45; // 45 min por punto de dificultad
                System.out.println("     - " + t.getNombre());
                System.out.println("       Dedica aproximadamente " + tiempoSugerido +
                        " minutos a esta tarea hoy.");
            }

            if (criticas.size() >= 2) {
                System.out.println("     Considera cancelar actividades opcionales de hoy");
                System.out.println("     para enfocarte en estas entregas.");
            }
            contador++;
        }

        // Sugerencia de que tarea hacer primero
        Tarea primeraTarea = obtenerPrimeraTarea(tareas);
        if (primeraTarea != null) {
            System.out.println();
            System.out.println("  " + contador + ". EMPIEZA POR ESTA TAREA");
            System.out.println("     -> " + primeraTarea.getNombre());
            System.out.println("        Prioridad : " + primeraTarea.getPrioridad());
            System.out.println("        Entrega   : " + primeraTarea.getDeadline());
            int tiempoIdeal = calcularTiempoIdeal(primeraTarea);
            System.out.println("        Tiempo recomendado: " + tiempoIdeal + " minutos");
            System.out.println("        Divide el trabajo en bloques de 25 min con");
            System.out.println("        5 min de descanso (tecnica Pomodoro).");
            contador++;
        }

        // Recomendacion sobre penalizaciones recientes
        ArrayList<Penalizacion> pens = estudiante.getPenalizaciones();
        if (pens.size() > 0) {
            int totalMin = 0;
            for (int i = 0; i < pens.size(); i++) {
                totalMin += pens.get(i).getMinutosPercidos();
            }
            System.out.println();
            System.out.println("  " + contador + ". CONTROL DE DISTRACCIONES");
            System.out.println("     Has perdido " + totalMin + " minutos en total por distracciones.");
            if (totalMin > 120) {
                System.out.println("     Esto es mas de 2 horas perdidas.");
                System.out.println("     Sugerencia: Usa el modo No Molestar en tu celular");
                System.out.println("     mientras estudias y coloca el telefono boca abajo.");
            } else if (totalMin > 60) {
                System.out.println("     Sugerencia: Intenta trabajar en bloques de 25 minutos");
                System.out.println("     sin interrupciones antes de revisar el celular.");
            } else {
                System.out.println("     Buen control. Mantente enfocado.");
            }
            contador++;
        }

        // Recomendacion positiva
        if (atrasadas.size() == 0 && criticas.size() == 0) {
            System.out.println();
            System.out.println("  " + contador + ". TODO EN ORDEN");
            System.out.println("     No tienes tareas criticas ni atrasadas.");
            if (completadas > 0) {
                System.out.println("     Has completado " + completadas + " tarea(s). Buen trabajo.");
            }
            System.out.println("     Aprovecha para adelantar las tareas de prioridad MEDIA");
            System.out.println("     antes de que se vuelvan urgentes.");
            contador++;
        }

        // Recomendacion sobre horas disponibles
        if (estudiante.getPerfil().isTieneHorarioFijo()) {
            int horasDisp = estudiante.getPerfil().calcularHorasDisponibles();
            System.out.println();
            System.out.println("  " + contador + ". APROVECHA TU TIEMPO");
            System.out.println("     Tienes aproximadamente " + horasDisp + " horas disponibles hoy.");
            int tareasPendientes = criticas.size() + altas.size() + pendientes.size();
            if (horasDisp >= tareasPendientes) {
                System.out.println("     Tiempo suficiente para cubrir todas tus tareas pendientes.");
            } else {
                System.out.println("     Tiempo justo. Enfocate en las tareas de mayor prioridad");
                System.out.println("     y deja las de prioridad BAJA para manana.");
            }
        }

        System.out.println();
        System.out.println("  ----------------------------------------");
        System.out.println("  Recuerda: El descanso tambien es parte");
        System.out.println("  del rendimiento. Duerme tus horas minimas.");
        System.out.println("========================================");
    }

    // Determina la primera tarea a realizar considerando prioridad y dificultad
    private Tarea obtenerPrimeraTarea(ArrayList<Tarea> tareas) {
        for (int i = 0; i < tareas.size(); i++) {
            Tarea t = tareas.get(i);
            if (!t.getEstado().equals(Tarea.COMPLETADA) &&
                !t.getEstado().equals(Tarea.ATRASADA)) {
                return t;
            }
        }
        return null;
    }

    // Calcula el tiempo ideal a dedicar segun dificultad y dias restantes
    // Formula: (dificultad * 45 min) ajustado por urgencia
    private int calcularTiempoIdeal(Tarea tarea) {
        int base = tarea.getDificultad() * 45;
        int dias = tarea.calcularDiasRestantes();

        if (dias <= 1) {
            // Urgente: dedicar el maximo tiempo posible
            return base;
        } else if (dias <= 3) {
            // Proximo: dividir en sesiones de trabajo
            return (int)(base * 0.7);
        } else {
            // Hay tiempo: sesion mas corta para avanzar
            return (int)(base * 0.5);
        }
    }
}
