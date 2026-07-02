package excepciones;

/**
 * Excepcion verificada (checked) para errores de validacion de datos
 * ingresados por el usuario en la interfaz grafica (paquete vista).
 * Se usa junto con util.Validador: los metodos de Validador declaran
 * "throws ValidacionException" y cada pantalla la captura con try/catch
 * para mostrar un mensaje de error claro en pantalla, en vez de dejar
 * que el programa se caiga con una excepcion no controlada
 * (NumberFormatException, DateTimeParseException, etc.).
 */
public class ValidacionException extends Exception {

    public ValidacionException(String mensaje) {
        super(mensaje);
    }
}