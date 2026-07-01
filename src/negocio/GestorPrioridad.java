package negocio;

import modelo.Tarea;
import java.util.ArrayList;

public class GestorPrioridad {

    /**
     * RF02 - Categorizacion de Urgencia (Regla de Negocio)
     * Calcula y asigna el nivel de prioridad a una tarea segun:
     *   - Dias restantes calculados automaticamente desde el sistema
     *   - Nivel de dificultad (1-5)
     *
     * Matriz de decision:
     *   Dias <= 0              -> CRITICA (vencida o vence hoy)
     *   Dias <= 2              -> ALTA    (muy proxima)
     *   Dias <= 5 + dific >= 4 -> ALTA    (proxima y dificil)
     *   Dias <= 5              -> MEDIA
     *   Dias <= 10 + dific >= 4-> MEDIA   (lejana pero dificil)
     *   Resto                  -> BAJA
     */
    public void calcularPrioridad(Tarea tarea) {
        // Dias restantes calculados automaticamente por la tarea
        int dias       = tarea.calcularDiasRestantes();
        int dificultad = tarea.getDificultad();
        String prioridad;

        if (dias <= 0) {
            // Tarea vencida: prioridad maxima y cambiar estado a ATRASADA
            prioridad = Tarea.PRIORIDAD_CRITICA;
            tarea.actualizarEstado(Tarea.ATRASADA);
        } else if (dias <= 2) {
            // Menos de 2 dias: urgente sin importar dificultad
            prioridad = Tarea.PRIORIDAD_ALTA;
        } else if (dias <= 5 && dificultad >= 4) {
            // Hasta 5 dias pero muy dificil: necesita mas tiempo de preparacion
            prioridad = Tarea.PRIORIDAD_ALTA;
        } else if (dias <= 5) {
            // Hasta 5 dias con dificultad baja o media
            prioridad = Tarea.PRIORIDAD_MEDIA;
        } else if (dias <= 10 && dificultad >= 4) {
            // Hasta 10 dias pero muy dificil: anticipar su complejidad
            prioridad = Tarea.PRIORIDAD_MEDIA;
        } else {
            // Mas de 10 dias o tarea facil
            prioridad = Tarea.PRIORIDAD_BAJA;
        }

        tarea.setPrioridad(prioridad);
    }

    // Recalcula la prioridad de todas las tareas pendientes de la lista
    // Las tareas COMPLETADAS se excluyen porque ya no necesitan priorizacion
    public void recalcularTodas(ArrayList<Tarea> tareas) {
        for (int i = 0; i < tareas.size(); i++) {
            Tarea t = tareas.get(i);
            if (!t.getEstado().equals(Tarea.COMPLETADA)) {
                calcularPrioridad(t);
            }
        }
    }

    /**
     * RF03 (apoyo) - Ordena tareas por urgencia usando algoritmo burbuja
     * Orden descendente: CRITICA > ALTA > MEDIA > BAJA
     * Usa valorPrioridad() para convertir String a numero y comparar
     */
    public void ordenarTareas(ArrayList<Tarea> tareas) {
        // Algoritmo de burbuja: compara cada par de tareas adyacentes
        for (int i = 0; i < tareas.size() - 1; i++) {
            for (int j = 0; j < tareas.size() - 1 - i; j++) {
                // Si la tarea actual tiene menor prioridad que la siguiente, intercambiar
                if (valorPrioridad(tareas.get(j)) < valorPrioridad(tareas.get(j + 1))) {
                    Tarea temp = tareas.get(j);
                    tareas.set(j, tareas.get(j + 1));
                    tareas.set(j + 1, temp);
                }
            }
        }
    }

    // Convierte el nivel de prioridad String a valor numerico para comparar en el burbuja
    // CRITICA=4, ALTA=3, MEDIA=2, BAJA=1
    private int valorPrioridad(Tarea tarea) {
        if (tarea.getPrioridad().equals(Tarea.PRIORIDAD_CRITICA)) return 4;
        if (tarea.getPrioridad().equals(Tarea.PRIORIDAD_ALTA))    return 3;
        if (tarea.getPrioridad().equals(Tarea.PRIORIDAD_MEDIA))   return 2;
        return 1;
    }

    // Muestra un resumen contando cuantas tareas hay por nivel de prioridad
    public void mostrarResumenPrioridades(ArrayList<Tarea> tareas) {
        int criticas = 0, altas = 0, medias = 0, bajas = 0;
        for (int i = 0; i < tareas.size(); i++) {
            String p = tareas.get(i).getPrioridad();
            if (p.equals(Tarea.PRIORIDAD_CRITICA))       criticas++;
            else if (p.equals(Tarea.PRIORIDAD_ALTA))     altas++;
            else if (p.equals(Tarea.PRIORIDAD_MEDIA))    medias++;
            else                                          bajas++;
        }
        System.out.println("  Resumen de prioridades:");
        System.out.println("    CRITICA : " + criticas + " tarea(s)");
        System.out.println("    ALTA    : " + altas    + " tarea(s)");
        System.out.println("    MEDIA   : " + medias   + " tarea(s)");
        System.out.println("    BAJA    : " + bajas    + " tarea(s)");
    }
}
