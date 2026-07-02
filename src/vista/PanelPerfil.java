package vista;

import excepciones.ValidacionException;
import modelo.Estudiante;
import modelo.EventoFijo;
import modelo.PerfilUsuario;
import util.Validador;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * RF06 - Panel de configuracion de perfil (version dashboard de
 * VentanaPerfil). Llama exactamente a los mismos metodos de
 * modelo.PerfilUsuario y modelo.EventoFijo. No se modifica ninguna clase
 * de modelo ni de negocio.
 */
public class PanelPerfil extends JPanel implements Refrescable {

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
    private JComboBox<String> comboDiaActividad;
    private JTextField campoHoraInicioActividad;
    private JTextField campoHoraFinActividad;
    private JSpinner spinnerDuracionActividad;

    private DefaultListModel<String> modeloListaActividades;
    private JList<String> listaActividades;

    private int contadorEventos;

    public PanelPerfil(Estudiante estudiante) {
        this.estudiante = estudiante;
        this.contadorEventos = estudiante.getEventosFijos().size() + 1;
        setLayout(new BorderLayout());
        setBackground(EstiloUI.COLOR_FONDO);
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        construirInterfaz();
    }

    private void construirInterfaz() {
        JPanel contenedor = new JPanel();
        contenedor.setLayout(new BoxLayout(contenedor, BoxLayout.Y_AXIS));
        contenedor.setBackground(EstiloUI.COLOR_FONDO);

        JLabel titulo = EstiloUI.crearTitulo("Mi Perfil");
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        contenedor.add(titulo);
        contenedor.add(Box.createVerticalStrut(20));

        JPanel tarjetaHorario = EstiloUI.crearTarjeta();
        tarjetaHorario.setLayout(new BoxLayout(tarjetaHorario, BoxLayout.Y_AXIS));
        tarjetaHorario.setAlignmentX(Component.LEFT_ALIGNMENT);
        tarjetaHorario.setMaximumSize(new Dimension(Integer.MAX_VALUE, 260));

        tarjetaHorario.add(crearPanelTipoHorario());
        tarjetaHorario.add(Box.createVerticalStrut(10));
        tarjetaHorario.add(crearPanelCamposHorario());
        tarjetaHorario.add(Box.createVerticalStrut(14));

        JButton botonGuardar = EstiloUI.crearBotonPrimario("Guardar perfil");
        botonGuardar.setAlignmentX(Component.LEFT_ALIGNMENT);
        botonGuardar.addActionListener(e -> onGuardarPerfil());
        tarjetaHorario.add(botonGuardar);

        contenedor.add(tarjetaHorario);
        contenedor.add(Box.createVerticalStrut(20));

        JPanel tarjetaActividades = EstiloUI.crearTarjeta();
        tarjetaActividades.setLayout(new BorderLayout(10, 10));
        tarjetaActividades.setAlignmentX(Component.LEFT_ALIGNMENT);
        tarjetaActividades.add(crearPanelActividadFija(), BorderLayout.NORTH);
        tarjetaActividades.add(crearPanelListaActividades(), BorderLayout.CENTER);

        contenedor.add(tarjetaActividades);

        add(new JScrollPane(contenedor), BorderLayout.CENTER);
    }

    private JPanel crearPanelTipoHorario() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBackground(EstiloUI.COLOR_TARJETA);

        JLabel etiqueta = EstiloUI.crearSubtitulo("Tipo de horario:");
        radioFijo = new JRadioButton("Fijo", true);
        radioVariable = new JRadioButton("Variable");
        radioFijo.setBackground(EstiloUI.COLOR_TARJETA);
        radioVariable.setBackground(EstiloUI.COLOR_TARJETA);

        ButtonGroup grupo = new ButtonGroup();
        grupo.add(radioFijo);
        grupo.add(radioVariable);

        radioFijo.addActionListener(e -> cardLayoutHorario.show(panelHorario, "FIJO"));
        radioVariable.addActionListener(e -> cardLayoutHorario.show(panelHorario, "VARIABLE"));

        panel.add(etiqueta);
        panel.add(radioFijo);
        panel.add(radioVariable);
        return panel;
    }

    private JPanel crearPanelCamposHorario() {
        cardLayoutHorario = new CardLayout();
        panelHorario = new JPanel(cardLayoutHorario);
        panelHorario.setBackground(EstiloUI.COLOR_TARJETA);

        panelHorario.add(crearPanelHorarioFijo(), "FIJO");
        panelHorario.add(crearPanelHorarioVariable(), "VARIABLE");

        return panelHorario;
    }

    private JPanel crearPanelHorarioFijo() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(EstiloUI.COLOR_TARJETA);
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
        panel.setBackground(EstiloUI.COLOR_TARJETA);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel info = new JLabel("El horario del dia se pedira al generar la agenda.");
        info.setForeground(EstiloUI.COLOR_TEXTO_SECUNDARIO);
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
        panel.setBackground(EstiloUI.COLOR_TARJETA);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titulo = EstiloUI.crearSubtitulo("Agregar actividad fija");
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 6;
        panel.add(titulo, gbc);
        gbc.gridwidth = 1;

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Nombre:"), gbc);
        gbc.gridx = 1;
        campoNombreActividad = new JTextField(10);
        panel.add(campoNombreActividad, gbc);

        gbc.gridx = 2; gbc.gridy = 1;
        panel.add(new JLabel("Dia:"), gbc);
        gbc.gridx = 3;
        // JComboBox en vez de texto libre: elimina errores de tipeo o
        // acentos que antes causaban que la actividad nunca coincidiera
        // con el dia de la semana calculado por GestorAgenda, y por lo
        // tanto nunca apareciera en la agenda diaria.
        comboDiaActividad = new JComboBox<String>(Validador.getDiasValidos());
        panel.add(comboDiaActividad, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Hora inicio:"), gbc);
        gbc.gridx = 1;
        campoHoraInicioActividad = new JTextField("18:00", 8);
        panel.add(campoHoraInicioActividad, gbc);

        gbc.gridx = 2; gbc.gridy = 2;
        panel.add(new JLabel("Hora fin:"), gbc);
        gbc.gridx = 3;
        campoHoraFinActividad = new JTextField("20:00", 8);
        panel.add(campoHoraFinActividad, gbc);

        gbc.gridx = 4; gbc.gridy = 2;
        panel.add(new JLabel("Duracion (h):"), gbc);
        gbc.gridx = 5;
        spinnerDuracionActividad = new JSpinner(new SpinnerNumberModel(2, 1, 24, 1));
        panel.add(spinnerDuracionActividad, gbc);

        JButton botonAgregar = EstiloUI.crearBotonSecundario("Agregar actividad");
        botonAgregar.addActionListener(e -> onAgregarActividad());
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        panel.add(botonAgregar, gbc);

        return panel;
    }

    private JPanel crearPanelListaActividades() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(EstiloUI.COLOR_TARJETA);

        JLabel titulo = EstiloUI.crearSubtitulo("Actividades fijas registradas");
        panel.add(titulo, BorderLayout.NORTH);

        modeloListaActividades = new DefaultListModel<String>();
        listaActividades = new JList<String>(modeloListaActividades);
        listaActividades.setVisibleRowCount(5);
        listaActividades.setFont(EstiloUI.FUENTE_NORMAL);

        JScrollPane scroll = new JScrollPane(listaActividades);
        scroll.setPreferredSize(new Dimension(0, 140));
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    // Recarga los datos del perfil y las actividades cada vez que el
    // usuario navega a esta seccion del dashboard.
    public void refrescar() {
        modeloListaActividades.clear();

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

            try {
                Validador.validarHora(horaInicio, "Hora de inicio");
                Validador.validarHora(horaLimite, "Hora limite de sueno");
                Validador.validarHorasSueno(horasSueno);
            } catch (ValidacionException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(),
                        "Datos invalidos", JOptionPane.WARNING_MESSAGE);
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

            try {
                Validador.validarHorasSueno(horasSueno);
            } catch (ValidacionException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(),
                        "Datos invalidos", JOptionPane.WARNING_MESSAGE);
                return;
            }

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
        String dia = (String) comboDiaActividad.getSelectedItem();
        String horaInicio = campoHoraInicioActividad.getText().trim();
        String horaFin = campoHoraFinActividad.getText().trim();
        int duracion = (Integer) spinnerDuracionActividad.getValue();

        try {
            Validador.validarTextoNoVacio(nombre, "Nombre de la actividad");
            Validador.validarDiaSemana(dia);
            Validador.validarHora(horaInicio, "Hora de inicio");
            Validador.validarHora(horaFin, "Hora de fin");
            Validador.validarDuracionHoras(duracion);
        } catch (ValidacionException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Datos invalidos", JOptionPane.WARNING_MESSAGE);
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
    }
}