package vista;

import modelo.Estudiante;
import modelo.EventoFijo;
import modelo.PerfilUsuario;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * RF06 - Ventana de configuracion de perfil.
 * Reemplaza a interfaz.FormularioPerfil pero con componentes Swing en vez
 * de Scanner. Llama exactamente a los mismos metodos de modelo.PerfilUsuario
 * y modelo.EventoFijo que ya existen. No se modifica ninguna clase de
 * modelo ni de negocio.
 */
public class VentanaPerfil extends JFrame {

    private Estudiante estudiante;

    private JRadioButton radioFijo;
    private JRadioButton radioVariable;

    private CardLayout cardLayoutHorario;
    private JPanel panelHorario;

    private JTextField campoHoraInicio;
    private JTextField campoHoraLimite;
    private JSpinner spinnerHorasSuenoFijo;

    private JSpinner spinnerHorasSuenoVariable;

    private JTextField campoNombreActividad;
    private JTextField campoDiaActividad;
    private JTextField campoHoraInicioActividad;
    private JTextField campoHoraFinActividad;
    private JSpinner spinnerDuracionActividad;

    private DefaultListModel<String> modeloListaActividades;
    private JList<String> listaActividades;

    private int contadorEventos;

    public VentanaPerfil(Estudiante estudiante) {
        this.estudiante = estudiante;
        this.contadorEventos = estudiante.getEventosFijos().size() + 1;
        configurarVentana();
        construirInterfaz();
        cargarDatosExistentes();
    }

    private void configurarVentana() {
        setTitle("Smart Planner - Perfil");
        setSize(480, 560);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
    }

    private void construirInterfaz() {
        JPanel panelPrincipal = new JPanel();
        panelPrincipal.setLayout(new BoxLayout(panelPrincipal, BoxLayout.Y_AXIS));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        panelPrincipal.add(crearPanelTipoHorario());
        panelPrincipal.add(Box.createVerticalStrut(10));
        panelPrincipal.add(crearPanelCamposHorario());
        panelPrincipal.add(Box.createVerticalStrut(10));

        JButton botonGuardar = new JButton("Guardar perfil");
        botonGuardar.setAlignmentX(Component.CENTER_ALIGNMENT);
        botonGuardar.addActionListener(e -> onGuardarPerfil());
        panelPrincipal.add(botonGuardar);

        panelPrincipal.add(Box.createVerticalStrut(20));
        panelPrincipal.add(new JSeparator());
        panelPrincipal.add(Box.createVerticalStrut(10));

        panelPrincipal.add(crearPanelActividadFija());
        panelPrincipal.add(Box.createVerticalStrut(10));
        panelPrincipal.add(crearPanelListaActividades());

        add(new JScrollPane(panelPrincipal));
    }

    private JPanel crearPanelTipoHorario() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setBorder(BorderFactory.createTitledBorder("Tipo de horario"));

        radioFijo = new JRadioButton("Horario fijo", true);
        radioVariable = new JRadioButton("Horario variable");

        ButtonGroup grupo = new ButtonGroup();
        grupo.add(radioFijo);
        grupo.add(radioVariable);

        radioFijo.addActionListener(e -> cardLayoutHorario.show(panelHorario, "FIJO"));
        radioVariable.addActionListener(e -> cardLayoutHorario.show(panelHorario, "VARIABLE"));

        panel.add(radioFijo);
        panel.add(radioVariable);
        return panel;
    }

    private JPanel crearPanelCamposHorario() {
        cardLayoutHorario = new CardLayout();
        panelHorario = new JPanel(cardLayoutHorario);

        panelHorario.add(crearPanelHorarioFijo(), "FIJO");
        panelHorario.add(crearPanelHorarioVariable(), "VARIABLE");

        return panelHorario;
    }

    private JPanel crearPanelHorarioFijo() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Hora de inicio (HH:mm):"), gbc);
        gbc.gridx = 1;
        campoHoraInicio = new JTextField("07:00", 8);
        panel.add(campoHoraInicio, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Hora limite sueno (HH:mm):"), gbc);
        gbc.gridx = 1;
        campoHoraLimite = new JTextField("23:00", 8);
        panel.add(campoHoraLimite, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Horas minimas de sueno:"), gbc);
        gbc.gridx = 1;
        spinnerHorasSuenoFijo = new JSpinner(new SpinnerNumberModel(7, 0, 24, 1));
        panel.add(spinnerHorasSuenoFijo, gbc);

        return panel;
    }

    private JPanel crearPanelHorarioVariable() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel info = new JLabel("El horario del dia se pedira al generar la agenda.");
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(info, gbc);

        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Horas minimas de sueno:"), gbc);
        gbc.gridx = 1;
        spinnerHorasSuenoVariable = new JSpinner(new SpinnerNumberModel(7, 0, 24, 1));
        panel.add(spinnerHorasSuenoVariable, gbc);

        return panel;
    }

    private JPanel crearPanelActividadFija() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Agregar actividad fija"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Nombre:"), gbc);
        gbc.gridx = 1;
        campoNombreActividad = new JTextField(12);
        panel.add(campoNombreActividad, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Dia (ej: LUNES):"), gbc);
        gbc.gridx = 1;
        campoDiaActividad = new JTextField(12);
        panel.add(campoDiaActividad, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Hora inicio (HH:mm):"), gbc);
        gbc.gridx = 1;
        campoHoraInicioActividad = new JTextField("18:00", 12);
        panel.add(campoHoraInicioActividad, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Hora fin (HH:mm):"), gbc);
        gbc.gridx = 1;
        campoHoraFinActividad = new JTextField("20:00", 12);
        panel.add(campoHoraFinActividad, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("Duracion (horas):"), gbc);
        gbc.gridx = 1;
        spinnerDuracionActividad = new JSpinner(new SpinnerNumberModel(2, 1, 24, 1));
        panel.add(spinnerDuracionActividad, gbc);

        JButton botonAgregar = new JButton("Agregar actividad");
        botonAgregar.addActionListener(e -> onAgregarActividad());
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        panel.add(botonAgregar, gbc);

        return panel;
    }

    private JPanel crearPanelListaActividades() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Actividades fijas registradas"));

        modeloListaActividades = new DefaultListModel<String>();
        listaActividades = new JList<String>(modeloListaActividades);
        listaActividades.setVisibleRowCount(5);

        panel.add(new JScrollPane(listaActividades), BorderLayout.CENTER);
        return panel;
    }

    private void cargarDatosExistentes() {
        PerfilUsuario perfil = estudiante.getPerfil();
        if (perfil != null) {
            if (perfil.isTieneHorarioFijo()) {
                radioFijo.setSelected(true);
                cardLayoutHorario.show(panelHorario, "FIJO");
                if (perfil.getHoraInicioDia() != null) {
                    campoHoraInicio.setText(perfil.getHoraInicioDia());
                }
                if (perfil.getHoraLimiteSueno() != null) {
                    campoHoraLimite.setText(perfil.getHoraLimiteSueno());
                }
                spinnerHorasSuenoFijo.setValue(perfil.getHorasMinSueno());
            } else {
                radioVariable.setSelected(true);
                cardLayoutHorario.show(panelHorario, "VARIABLE");
                spinnerHorasSuenoVariable.setValue(perfil.getHorasMinSueno());
            }
        }

        ArrayList<EventoFijo> eventos = estudiante.getEventosFijos();
        for (int i = 0; i < eventos.size(); i++) {
            modeloListaActividades.addElement(eventos.get(i).toString());
        }
    }

    private void onGuardarPerfil() {
        boolean horarioFijo = radioFijo.isSelected();
        PerfilUsuario perfil;

        if (horarioFijo) {
            String horaInicio = campoHoraInicio.getText().trim();
            String horaLimite = campoHoraLimite.getText().trim();
            int horasSueno = (Integer) spinnerHorasSuenoFijo.getValue();

            if (horaInicio.isEmpty() || horaLimite.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Completa hora de inicio y hora limite.",
                        "Datos incompletos", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (estudiante.getPerfil() != null) {
                perfil = estudiante.getPerfil();
                perfil.configurar(true, horaInicio, horaLimite, horasSueno);
            } else {
                perfil = new PerfilUsuario(true, horaInicio, horaLimite, horasSueno);
                estudiante.setPerfil(perfil);
            }
        } else {
            int horasSueno = (Integer) spinnerHorasSuenoVariable.getValue();

            if (estudiante.getPerfil() != null) {
                perfil = estudiante.getPerfil();
                perfil.configurar(false, null, null, horasSueno);
            } else {
                perfil = new PerfilUsuario(horasSueno);
                estudiante.setPerfil(perfil);
            }
        }

        JOptionPane.showMessageDialog(this, "Perfil guardado correctamente.",
                "Perfil actualizado", JOptionPane.INFORMATION_MESSAGE);
    }

    private void onAgregarActividad() {
        String nombre = campoNombreActividad.getText().trim();
        String dia = campoDiaActividad.getText().trim().toUpperCase();
        String horaInicio = campoHoraInicioActividad.getText().trim();
        String horaFin = campoHoraFinActividad.getText().trim();
        int duracion = (Integer) spinnerDuracionActividad.getValue();

        if (nombre.isEmpty() || dia.isEmpty() || horaInicio.isEmpty() || horaFin.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Completa todos los campos de la actividad.",
                    "Datos incompletos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String id = "EV" + String.format("%03d", contadorEventos);
        contadorEventos++;

        EventoFijo evento = new EventoFijo(id, nombre, horaInicio, horaFin, dia, duracion);
        estudiante.agregarEventoFijo(evento);

        if (estudiante.getPerfil() != null) {
            estudiante.getPerfil().agregarActividadFija(
                    nombre + " (" + dia + " " + horaInicio + "-" + horaFin + ")");
        }

        modeloListaActividades.addElement(evento.toString());

        campoNombreActividad.setText("");
        campoDiaActividad.setText("");
    }
}