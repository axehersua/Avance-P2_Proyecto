package vista;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/**
 * Utilidad de la capa vista.
 * Varios metodos de negocio.* y modelo.* (por ejemplo
 * GestorAgenda.verAgendaPorFecha, GestorRecomendaciones.generarRecomendaciones,
 * AgendaDiaria.mostrarAgenda) fueron escritos para la version de consola y
 * usan System.out.println() en vez de retornar un String.
 *
 * En vez de modificar esas clases (lo cual rompe la version de consola ya
 * evaluada), esta utilidad captura temporalmente lo que imprimen y lo
 * devuelve como texto, para poder mostrarlo en un JTextArea.
 */
public class CapturadorConsola {

    public static String capturar(Runnable accion) {
        PrintStream salidaOriginal = System.out;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        System.setOut(new PrintStream(buffer));
        try {
            accion.run();
        } finally {
            System.setOut(salidaOriginal);
        }
        return buffer.toString();
    }
}