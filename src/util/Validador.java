package util;

import excepciones.ValidacionException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

/**
 * Utilidad de validacion compartida por toda la interfaz grafica
 * (paquete vista). Cada metodo declara "throws ValidacionException" y
 * lanza esa excepcion verificada cuando el dato no es valido, con un
 * mensaje claro para mostrar directamente al usuario. No modifica ni
 * depende de clases de modelo ni de negocio: es una capa puramente de
 * validacion de entrada.
 */
public class Validador {

    private static final String[] DIAS_VALIDOS = {
            "LUNES", "MARTES", "MIERCOLES", "JUEVES", "VIERNES", "SABADO", "DOMINGO"
    };

    private static final DateTimeFormatter FORMATO_FECHA =
            DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter FORMATO_HORA =
            DateTimeFormatter.ofPattern("HH:mm").withResolverStyle(ResolverStyle.STRICT);

    private Validador() {
        // Clase de utilidades: no se instancia.
    }

    public static void validarTextoNoVacio(String valor, String nombreCampo) throws ValidacionException {
        if (valor == null || valor.trim().isEmpty()) {
            throw new ValidacionException("El campo \"" + nombreCampo + "\" no puede estar vacio.");
        }
    }

    public static void validarEmail(String correo) throws ValidacionException {
        validarTextoNoVacio(correo, "Correo");
        // Soporta dominios de varios niveles (ej: nombre@udla.edu.ec),
        // no solo dominio.com de un solo punto.
        if (!correo.trim().matches("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)*\\.[a-zA-Z]{2,}$")) {
            throw new ValidacionException("El correo ingresado no tiene un formato valido.");
        }
    }

    public static void validarFecha(String fecha) throws ValidacionException {
        validarTextoNoVacio(fecha, "Fecha");
        try {
            LocalDate.parse(fecha.trim(), FORMATO_FECHA);
        } catch (DateTimeParseException e) {
            throw new ValidacionException(
                    "Formato de fecha invalido. Usa DD/MM/YYYY (ej: 15/07/2026).");
        }
    }

    public static void validarHora(String hora, String nombreCampo) throws ValidacionException {
        validarTextoNoVacio(hora, nombreCampo);
        try {
            LocalTime.parse(hora.trim(), FORMATO_HORA);
        } catch (DateTimeParseException e) {
            throw new ValidacionException(
                    "Formato de \"" + nombreCampo + "\" invalido. Usa HH:mm (ej: 07:00).");
        }
    }

    public static void validarRangoEntero(int valor, int min, int max, String nombreCampo)
            throws ValidacionException {
        if (valor < min || valor > max) {
            throw new ValidacionException(
                    "El campo \"" + nombreCampo + "\" debe estar entre " + min + " y " + max + ".");
        }
    }

    public static void validarDificultad(int dificultad) throws ValidacionException {
        validarRangoEntero(dificultad, 1, 5, "Dificultad");
    }

    public static void validarHorasSueno(int horas) throws ValidacionException {
        validarRangoEntero(horas, 0, 24, "Horas de sueno");
    }

    public static void validarDuracionHoras(int horas) throws ValidacionException {
        validarRangoEntero(horas, 1, 24, "Duracion");
    }

    public static void validarMinutosPerdidos(int minutos) throws ValidacionException {
        if (minutos <= 0) {
            throw new ValidacionException("Los minutos perdidos deben ser mayores a 0.");
        }
    }

    // Verifica que el dia sea uno de los 7 dias de la semana, sin acentos,
    // exactamente como los espera negocio.GestorAgenda al generar la
    // agenda diaria. Corrige el bug donde una actividad fija registrada
    // con un dia mal escrito (o con tilde) nunca aparecia en la agenda.
    public static void validarDiaSemana(String dia) throws ValidacionException {
        validarTextoNoVacio(dia, "Dia");
        String diaNormalizado = dia.trim().toUpperCase();
        for (int i = 0; i < DIAS_VALIDOS.length; i++) {
            if (DIAS_VALIDOS[i].equals(diaNormalizado)) {
                return;
            }
        }
        throw new ValidacionException(
                "Dia invalido. Usa uno de: LUNES, MARTES, MIERCOLES, JUEVES, VIERNES, SABADO, DOMINGO.");
    }

    public static String[] getDiasValidos() {
        return DIAS_VALIDOS.clone();
    }
}