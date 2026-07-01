package vista;

import modelo.AgendaDiaria;
import modelo.Estudiante;
import modelo.PerfilUsuario;
import negocio.GestorAgenda;

import javax.swing.*;
import java.awt.*;

/**
 * RF05 - Ventana de agenda.
 * Reemplaza interfaz.VistaAgenda (mostrarAgendaDiaria, verAgendaPorFecha,
 * verHistoricoAgendas) con tres pestanas. Llama exactamente a los mismos
 * metodos de negocio.GestorAgenda que ya existen; usa CapturadorConsola
 * para mostrar en pantalla lo que esos metodos imprimen por consola.
 * No se modifica ninguna clase de modelo ni de negocio.
 */
public class VentanaAgenda extends JFrame {

    private Estudiante estudiante;
    private GestorAgenda gestorAgenda;

    private JTextArea areaAgendaHoy;
    private JTextArea areaAgendaFecha;
    private JTextArea areaHistorico;

    private JTextField campoFechaConsulta;
    private JTextField campoFechaInicio;
    private JTextField campoFechaFin;

    public VentanaAgenda(Estudiante estudiante) {
        this.estudiante = estudiante;
        this.gestorAgenda = new GestorAgenda();
        configurarVentana();
        construirInterfaz();
    }

    private void configurarVentana() {
        setTitle("Smart Planner - Agenda");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void construirInterfaz() {
        JTabbedPane pestanas = new JTabbedPane();
        pestanas.addTab("Agenda de hoy", crearPestanaHoy());
        pestanas.addTab("Agenda por fecha", crearPestanaPorFecha());
        pestanas.addTab("Historico por rango", crearPestanaHistorico());
        add(pestanas);
    }

    private JPanel crearPestanaHoy() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton boton = new JButton("Generar agenda de hoy");
        boton.addActionListener(e -> onGenerarAgendaHoy());

        areaAgendaHoy = crearAreaTexto();

        panel.add(boton, BorderLayout.NORTH);
        panel.add(new JScrollPane(areaAgendaHoy), BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearPestanaPorFecha() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel panelSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelSuperior.add(new JLabel("Fecha (DD/MM/YYYY):"));
        campoFechaConsulta = new JTextField(10);
        panelSuperior.add(campoFechaConsulta);
        JButton boton = new JButton("Consultar");
        boton.addActionListener(e -> onConsultarPorFecha());
        panelSuperior.add(boton);

        areaAgendaFecha = crearAreaTexto();

        panel.add(panelSuperior, BorderLayout.NORTH);
        panel.add(new JScrollPane(areaAgendaFecha), BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearPestanaHistorico() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel panelSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelSuperior.add(new JLabel("Desde:"));
        campoFechaInicio = new JTextField(10);
        panelSuperior.add(campoFechaInicio);
        panelSuperior.add(new JLabel("Hasta:"));
        campoFechaFin = new JTextField(10);
        panelSuperior.add(campoFechaFin);
        JButton boton = new JButton("Consultar");
        boton.addActionListener(e -> onConsultarHistorico());
        panelSuperior.add(boton);

        areaHistorico = crearAreaTexto();

        panel.add(panelSuperior, BorderLayout.NORTH);
        panel.add(new JScrollPane(areaHistorico), BorderLayout.CENTER);
        return panel;
    }

    private JTextArea crearAreaTexto() {
        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setFont(new Font("Monospaced", Font.PLAIN, 12));
        return area;
    }

    // RF03 - Genera el cronograma del dia, igual que
    // interfaz.VistaAgenda.mostrarAgendaDiaria().
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

    // Igual que interfaz.VistaAgenda.verAgendaPorFecha().
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

    // Igual que interfaz.VistaAgenda.verHistoricoAgendas().
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