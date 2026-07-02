import negocio.GestorUsuarios;
import vista.VentanaInicio;
import javax.swing.SwingUtilities;


public class MainGUI {
    public static void main(String[] args) {
        GestorUsuarios gestorUsuarios = new GestorUsuarios();
        SwingUtilities.invokeLater(() -> new VentanaInicio(gestorUsuarios).setVisible(true));
    }
}