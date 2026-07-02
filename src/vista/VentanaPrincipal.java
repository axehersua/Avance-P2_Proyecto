package vista;

import modelo.Estudiante;
import negocio.GestorUsuarios;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Dashboard principal: una sola ventana con barra lateral de navegacion
 * (sidebar) y un area de contenido central que cambia con CardLayout,
 * sin abrir ventanas nuevas por cada seccion.
 */
public class VentanaPrincipal extends JFrame {

    private Estudiante estudiante;
    private GestorUsuarios gestorUsuarios;

    private CardLayout cardLayout;
    private JPanel panelContenido;
    private JPanel panelSidebar;

    private final Map<String, JButton> botonesSidebar = new LinkedHashMap<String, JButton>();
    private final Map<String, Refrescable> paneles = new LinkedHashMap<String, Refrescable>();
    private String seccionActual;

    // Recibe la MISMA instancia de GestorUsuarios que se creo una sola vez
    // en MainGUI, para poder pasarla de vuelta a VentanaInicio al cerrar
    // sesion y no perder los usuarios registrados durante la ejecucion.
    public VentanaPrincipal(Estudiante estudiante, GestorUsuarios gestorUsuarios) {
        this.estudiante = estudiante;
        this.gestorUsuarios = gestorUsuarios;
        configurarVentana();
        construirInterfaz();
        mostrarSeccion("INICIO");
    }

    private void configurarVentana() {
        setTitle("Smart Planner - " + estudiante.getNombre());
        setSize(1000, 640);
        setMinimumSize(new Dimension(860, 560));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void construirInterfaz() {
        setLayout(new BorderLayout());

        panelSidebar = crearSidebar();
        add(panelSidebar, BorderLayout.WEST);

        cardLayout = new CardLayout();
        panelContenido = new JPanel(cardLayout);
        panelContenido.setBackground(EstiloUI.COLOR_FONDO);

        agregarSeccion("INICIO", new PanelInicio(estudiante));
        agregarSeccion("PERFIL", new PanelPerfil(estudiante));
        agregarSeccion("TAREAS", new PanelTareas(estudiante));
        agregarSeccion("AGENDA", new PanelAgenda(estudiante));
        agregarSeccion("PENALIZACION", new PanelPenalizacion(estudiante));
        agregarSeccion("RECOMENDACIONES", new PanelRecomendaciones(estudiante));

        add(panelContenido, BorderLayout.CENTER);
    }

    private void agregarSeccion(String clave, JComponent panel) {
        panelContenido.add(panel, clave);
        if (panel instanceof Refrescable) {
            paneles.put(clave, (Refrescable) panel);
        }
    }

    private JPanel crearSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(EstiloUI.COLOR_SIDEBAR);
        sidebar.setPreferredSize(new Dimension(230, 0));

        JLabel logo = new JLabel("SMART PLANNER");
        logo.setFont(new Font("SansSerif", Font.BOLD, 17));
        logo.setForeground(Color.WHITE);
        logo.setBorder(BorderFactory.createEmptyBorder(24, 20, 8, 20));
        logo.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(logo);

        JLabel subLogo = new JLabel(estudiante.getNombre());
        subLogo.setFont(new Font("SansSerif", Font.PLAIN, 12));
        subLogo.setForeground(EstiloUI.COLOR_TEXTO_SIDEBAR);
        subLogo.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));
        subLogo.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(subLogo);

        sidebar.add(crearSeparadorSidebar());

        agregarBotonSidebar(sidebar, "INICIO", "Inicio");
        agregarBotonSidebar(sidebar, "PERFIL", "Mi Perfil");
        agregarBotonSidebar(sidebar, "TAREAS", "Tareas");
        agregarBotonSidebar(sidebar, "AGENDA", "Agenda");
        agregarBotonSidebar(sidebar, "PENALIZACION", "Penalizacion");
        agregarBotonSidebar(sidebar, "RECOMENDACIONES", "Recomendaciones");

        sidebar.add(Box.createVerticalGlue());
        sidebar.add(crearSeparadorSidebar());

        JButton botonCerrarSesion = EstiloUI.crearBotonSidebar("Cerrar sesion");
        botonCerrarSesion.setForeground(new Color(252, 165, 165));
        botonCerrarSesion.addActionListener(e -> {
            dispose();
            // Reutiliza el mismo GestorUsuarios: los usuarios registrados
            // NO se pierden al cerrar sesion, solo al cerrar el programa.
            new VentanaInicio(gestorUsuarios).setVisible(true);
        });
        sidebar.add(botonCerrarSesion);
        sidebar.add(Box.createVerticalStrut(10));

        return sidebar;
    }

    private void agregarBotonSidebar(JPanel sidebar, String clave, String texto) {
        JButton boton = EstiloUI.crearBotonSidebar(texto);
        boton.addActionListener(e -> mostrarSeccion(clave));
        sidebar.add(boton);
        botonesSidebar.put(clave, boton);
    }

    private JSeparator crearSeparadorSidebar() {
        JSeparator separador = new JSeparator();
        separador.setForeground(EstiloUI.COLOR_SIDEBAR_HOVER);
        separador.setBackground(EstiloUI.COLOR_SIDEBAR);
        separador.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        return separador;
    }

    private void mostrarSeccion(String clave) {
        cardLayout.show(panelContenido, clave);

        if (seccionActual != null && botonesSidebar.containsKey(seccionActual)) {
            EstiloUI.marcarActivo(botonesSidebar.get(seccionActual), false);
        }
        if (botonesSidebar.containsKey(clave)) {
            EstiloUI.marcarActivo(botonesSidebar.get(clave), true);
        }
        seccionActual = clave;

        if (paneles.containsKey(clave)) {
            paneles.get(clave).refrescar();
        }
    }
}