package vista;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.JTableHeader;
import java.awt.*;

/**
 * Paleta de colores, fuentes y helpers de estilo compartidos por todo el
 * dashboard, para que todas las pantallas (paneles) se vean consistentes.
 * No contiene logica de negocio, solo apariencia.
 */
public class EstiloUI {

    // ---------- Paleta de colores ----------
    public static final Color COLOR_SIDEBAR         = new Color(30, 41, 59);    // slate-800
    public static final Color COLOR_SIDEBAR_ACTIVO  = new Color(37, 99, 235);   // blue-600
    public static final Color COLOR_SIDEBAR_HOVER    = new Color(51, 65, 85);   // slate-700
    public static final Color COLOR_TEXTO_SIDEBAR    = new Color(226, 232, 240);// slate-200

    public static final Color COLOR_FONDO           = new Color(241, 245, 249); // slate-100
    public static final Color COLOR_TARJETA         = Color.WHITE;
    public static final Color COLOR_BORDE           = new Color(226, 232, 240); // slate-200

    public static final Color COLOR_ACENTO          = new Color(37, 99, 235);   // blue-600
    public static final Color COLOR_ACENTO_HOVER    = new Color(29, 78, 216);   // blue-700

    public static final Color COLOR_TEXTO           = new Color(15, 23, 42);    // slate-900
    public static final Color COLOR_TEXTO_SECUNDARIO = new Color(100, 116, 139);// slate-500

    public static final Color COLOR_EXITO           = new Color(22, 163, 74);   // green-600
    public static final Color COLOR_PELIGRO         = new Color(220, 38, 38);   // red-600
    public static final Color COLOR_ADVERTENCIA     = new Color(217, 119, 6);   // amber-600

    // ---------- Fuentes ----------
    public static final Font FUENTE_TITULO         = new Font("SansSerif", Font.BOLD, 22);
    public static final Font FUENTE_SUBTITULO      = new Font("SansSerif", Font.BOLD, 15);
    public static final Font FUENTE_NORMAL         = new Font("SansSerif", Font.PLAIN, 13);
    public static final Font FUENTE_BOTON_SIDEBAR  = new Font("SansSerif", Font.PLAIN, 14);
    public static final Font FUENTE_TARJETA_VALOR  = new Font("SansSerif", Font.BOLD, 28);
    public static final Font FUENTE_TARJETA_TITULO = new Font("SansSerif", Font.PLAIN, 13);
    public static final Font FUENTE_MONO           = new Font(Font.MONOSPACED, Font.PLAIN, 12);

    // ---------- Helpers de componentes ----------

    /** Boton de navegacion del sidebar (plano, ancho completo, alineado a la izquierda). */
    public static JButton crearBotonSidebar(String texto) {
        JButton boton = new JButton(texto);
        boton.setFont(FUENTE_BOTON_SIDEBAR);
        boton.setForeground(COLOR_TEXTO_SIDEBAR);
        boton.setBackground(COLOR_SIDEBAR);
        boton.setBorder(new EmptyBorder(12, 20, 12, 20));
        boton.setHorizontalAlignment(SwingConstants.LEFT);
        boton.setFocusPainted(false);
        boton.setBorderPainted(false);
        boton.setContentAreaFilled(true);
        boton.setOpaque(true);
        boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        boton.setAlignmentX(Component.LEFT_ALIGNMENT);
        boton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        return boton;
    }

    /** Marca visualmente un boton del sidebar como la seccion activa. */
    public static void marcarActivo(JButton boton, boolean activo) {
        boton.setBackground(activo ? COLOR_SIDEBAR_ACTIVO : COLOR_SIDEBAR);
        boton.setFont(activo
                ? FUENTE_BOTON_SIDEBAR.deriveFont(Font.BOLD)
                : FUENTE_BOTON_SIDEBAR);
    }

    /** Boton de accion principal (Guardar, Registrar, etc.) con color de acento. */
    public static JButton crearBotonPrimario(String texto) {
        JButton boton = new JButton(texto);
        boton.setFont(FUENTE_SUBTITULO);
        boton.setForeground(Color.WHITE);
        boton.setBackground(COLOR_ACENTO);
        boton.setBorder(new EmptyBorder(10, 22, 10, 22));
        boton.setFocusPainted(false);
        boton.setBorderPainted(false);
        boton.setOpaque(true);
        boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return boton;
    }

    /** Boton secundario (Refrescar, Consultar, acciones menores). */
    public static JButton crearBotonSecundario(String texto) {
        JButton boton = new JButton(texto);
        boton.setFont(FUENTE_NORMAL);
        boton.setForeground(COLOR_TEXTO);
        boton.setBackground(Color.WHITE);
        boton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDE, 1),
                new EmptyBorder(8, 16, 8, 16)));
        boton.setFocusPainted(false);
        boton.setOpaque(true);
        boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return boton;
    }

    /** Titulo grande de encabezado de seccion. */
    public static JLabel crearTitulo(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(FUENTE_TITULO);
        label.setForeground(COLOR_TEXTO);
        return label;
    }

    /** Subtitulo / etiqueta de sub-seccion. */
    public static JLabel crearSubtitulo(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(FUENTE_SUBTITULO);
        label.setForeground(COLOR_TEXTO);
        return label;
    }

    /** Panel tipo "tarjeta" blanca con borde suave, usado en toda la app. */
    public static JPanel crearTarjeta() {
        JPanel tarjeta = new JPanel();
        tarjeta.setBackground(COLOR_TARJETA);
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDE, 1),
                new EmptyBorder(16, 16, 16, 16)));
        return tarjeta;
    }

    /** Aplica estilo consistente a una JTable (tareas, etc.). */
    public static void estilizarTabla(JTable tabla) {
        tabla.setRowHeight(28);
        tabla.setFont(FUENTE_NORMAL);
        tabla.setGridColor(COLOR_BORDE);
        tabla.setSelectionBackground(COLOR_ACENTO);
        tabla.setSelectionForeground(Color.WHITE);
        tabla.setShowVerticalLines(false);

        JTableHeader header = tabla.getTableHeader();
        header.setFont(FUENTE_SUBTITULO.deriveFont(13f));
        header.setBackground(COLOR_FONDO);
        header.setForeground(COLOR_TEXTO);
        header.setBorder(BorderFactory.createLineBorder(COLOR_BORDE));
    }

    /** Aplica estilo consistente a un JTextArea de solo lectura (resultados, recomendaciones). */
    public static void estilizarAreaTexto(JTextArea area) {
        area.setFont(FUENTE_MONO);
        area.setEditable(false);
        area.setBackground(Color.WHITE);
        area.setForeground(COLOR_TEXTO);
        area.setBorder(new EmptyBorder(10, 10, 10, 10));
    }
}