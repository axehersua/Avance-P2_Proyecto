import vista.VentanaInicio;
import javax.swing.SwingUtilities;

/**
 * Punto de entrada de la version GUI.
 * El Main.java original (consola) se deja intacto para no perder esa version.
 */
public class MainGUI {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new VentanaInicio().setVisible(true));
    }
}