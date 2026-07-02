package vista;

import modelo.AgendaDiaria;
import modelo.Estudiante;
import modelo.PerfilUsuario;
import negocio.GestorAgenda;

import javax.swing.*;
import java.awt.*;

/**
 * RF05 - Panel de agenda (version dashboard de VentanaAgenda), con tres
 * pestanas. Llama exactamente a los mismos metodos de negocio.GestorAgenda
 * que ya existen; usa CapturadorConsola para mostrar lo que esos metodos
 * imprimen por consola. No se modifica ninguna clase de modelo ni de
 * negocio.
 */
public class PanelAgenda extends JPanel implements Refrescable {

    private Estudiante estudiante;
    private GestorAgenda gestorAgenda;

    private JTextArea areaAgendaHoy;
    private JTextArea areaAgendaFecha;
    private JTextArea areaHistorico;

    private JTextField campoFechaConsulta;
    private JTextField campoFechaInicio;
    private JTextField campoFechaFin;

    public PanelAgenda(Estudiante estudiante) {
        this.estudiante = estudiante;
        this.gestorAgenda = new GestorAgenda();
        setLayout(new BorderLayout(0, 16));
        setBackground(EstiloUI.COLOR_FONDO);
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        construirInterfaz();
    }

    private void construirInterfaz() {
        add(EstiloUI.crearTitulo("Agenda"), BorderLayout.NORTH);

        JTabbedPane pestanas = new JTabbedPane();
        pestanas.setFont(EstiloUI.FUENTE_NORMAL);
        pestanas.addTab("Agenda de hoy", crearPestanaHoy());
        pestanas.addTab("Agenda por fecha", crearPestanaPorFecha());
        pestanas.addTab("Historico por rango", crearPestanaHistorico());
        add(pestanas, BorderLayout.CENTER);
    }

    private JPanel crearPestanaHoy() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(EstiloUI.COLOR_FONDO);
        panel.setBorder(BorderFactory.createEmptyBorder(14, 0, 0, 0));

        JButton boton = EstiloUI.crearBotonPrimario("Generar agenda de hoy");
        boton.addActionListener(e -> onGenerarAgendaHoy());

        areaAgendaHoy = crearAreaTexto();

        JPanel contenedorBoton = new JPanel(new FlowLayout(FlowLayout.LEFT));
        contenedorBoton.setBackground(EstiloUI.COLOR_FONDO);
        contenedorBoton.add(boton);

        panel.add(contenedorBoton, BorderLayout.NORTH);
        panel.add(envolverEnTarjeta(areaAgendaHoy), BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearPestanaPorFecha() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(EstiloUI.COLOR_FONDO);
        panel.setBorder(BorderFactory.createEmptyBorder(14, 0, 0, 0));

        JPanel panelSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelSuperior.setBackground(EstiloUI.COLOR_FONDO);
        panelSuperior.add(new JLabel("Fecha (DD/MM/YYYY):"));
        campoFechaConsulta = new JTextField(10);
        panelSuperior.add(campoFechaConsulta);
        JButton boton = EstiloUI.crearBotonSecundario("Consultar");
        boton.addActionListener(e -> onConsultarPorFecha());
        panelSuperior.add(boton);

        areaAgendaFecha = crearAreaTexto();

        panel.add(panelSuperior, BorderLayout.NORTH);
        panel.add(envolverEnTarjeta(areaAgendaFecha), BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearPestanaHistorico() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(EstiloUI.COLOR_FONDO);
        panel.setBorder(BorderFactory.createEmptyBorder(14, 0, 0, 0));

        JPanel panelSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelSuperior.setBackground(EstiloUI.COLOR_FONDO);
        panelSuperior.add(new JLabel("Desde:"));
        campoFechaInicio = new JTextField(10);
        panelSuperior.add(campoFechaInicio);
        panelSuperior.add(new JLabel("Hasta:"));
        campoFechaFin = new JTextField(10);
        panelSuperior.add(campoFechaFin);
        JButton boton = EstiloUI.crearBotonSecundario("Consultar");
        boton.addActionListener(e -> onConsultarHistorico());
        panelSuperior.add(boton);

        areaHistorico = crearAreaTexto();

        panel.add(panelSuperior, BorderLayout.NORTH);
        panel.add(envolverEnTarjeta(areaHistorico), BorderLayout.CENTER);
        return panel;
    }

    private JTextArea crearAreaTexto() {
        JTextArea area = new JTextArea();
        EstiloUI.estilizarAreaTexto(area);
        return area;
    }

    private JScrollPane envolverEnTarjeta(JTextArea area) {
        JScrollPane scroll = new JScrollPane(area);
        scroll.setBorder(BorderFactory.createLineBorder(EstiloUI.COLOR_BORDE, 1));
        return scroll;
    }

    // No recarga nada automaticamente al navegar: el usuario decide cuando
    // generar/consultar, igual que en la version de consola.
    public void refrescar() {
        // Intencionalmente vacio.
    }

    private void onGenerarAgendaHoy() {
        if (estudiante.getTareas().size() == 0) {
            areaAgendaHoy.setText("No tienes tareas registradas.");
            return;
        }

        PerfilUsuario perfil = estudiante.getPerfil();
        if (perfil == null) {
            areaAgendaHoy.setText("Configura tu perfil primero.");
            return;
        }

        AgendaDiaria agenda;

        if (!perfil.isTieneHorarioFijo()) {
            String horaInicio = JOptionPane.showInputDialog(this,
                    "Tu perfil tiene horario variable.\nHora de inicio de hoy (ej: 07):", "07");
            if (horaInicio == null) return;
            String horaFin = JOptionPane.showInputDialog(this,
                    "Hora en que puedes terminar hoy (ej: 22):", "22");
            if (horaFin == null) return;
            agenda = gestorAgenda.generarCronograma(estudiante, horaInicio.trim() + ":00", horaFin.trim() + ":00");
        } else {
            agenda = gestorAgenda.generarCronograma(estudiante);
        }

        AgendaDiaria agendaFinal = agenda;
        String texto = CapturadorConsola.capturar(() -> agendaFinal.mostrarAgenda());
        areaAgendaHoy.setText(texto);
    }

    private void onConsultarPorFecha() {
        String fecha = campoFechaConsulta.getText().trim();
        if (fecha.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingresa una fecha.",
                    "Dato requerido", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String texto = CapturadorConsola.capturar(() -> gestorAgenda.verAgendaPorFecha(estudiante, fecha));
        areaAgendaFecha.setText(texto);
    }

    private void onConsultarHistorico() {
        String inicio = campoFechaInicio.getText().trim();
        String fin = campoFechaFin.getText().trim();
        if (inicio.isEmpty() || fin.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingresa ambas fechas.",
                    "Dato requerido", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String texto = CapturadorConsola.capturar(() -> gestorAgenda.verHistoricoAgendas(estudiante, inicio, fin));
        areaHistorico.setText(texto);
    }
}