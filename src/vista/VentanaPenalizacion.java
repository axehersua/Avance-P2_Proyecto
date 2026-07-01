package vista;

import modelo.AgendaDiaria;
import modelo.Estudiante;
import modelo.Penalizacion;
import negocio.GestorAgenda;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;

/**
 * RF04 - Ventana de registro de penalizacion.
 * Reemplaza interfaz.VistaAgenda.registrarPenalizacion(). Llama
 * exactamente a los mismos metodos de modelo.Penalizacion y
 * negocio.GestorAgenda que ya existen. No se modifica ninguna clase de
 * modelo ni de negocio.
 */
public class VentanaPenalizacion extends JFrame {

    private Estudiante estudiante;
    private GestorAgenda gestorAgenda;

    private JTextField campoMotivo;
    private JSpinner spinnerMinutos;
    private JTextArea areaResultado;

    public VentanaPenalizacion(Estudiante estudiante) {
        this.estudiante = estudiante;
        this.gestorAgenda = new GestorAgenda();
        configurarVentana();
        construirInterfaz();
    }

    private void configurarVentana() {
        setTitle("Smart Planner - Registrar penalizacion");
        setSize(500, 450);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void construirInterfaz() {
        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel panelFormulario = new JPanel(new GridBagLayout());
        panelFormulario.setBorder(BorderFactory.createTitledBorder("Registrar penalizacion"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        panelFormulario.add(new JLabel("Motivo de la distraccion:"), gbc);
        gbc.gridx = 1;
        campoMotivo = new JTextField(18);
        panelFormulario.add(campoMotivo, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panelFormulario.add(new JLabel("Minutos perdidos:"), gbc);
        gbc.gridx = 1;
        spinnerMinutos = new JSpinner(new SpinnerNumberModel(15, 1, 1440, 1));
        panelFormulario.add(spinnerMinutos, gbc);

        JButton botonRegistrar = new JButton("Registrar");
        botonRegistrar.addActionListener(e -> onRegistrarPenalizacion());
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        panelFormulario.add(botonRegistrar, gbc);

        areaResultado = new JTextArea();
        areaResultado.setEditable(false);
        areaResultado.setFont(new Font("Monospaced", Font.PLAIN, 12));
        areaResultado.setLineWrap(true);
        areaResultado.setWrapStyleWord(true);

        panelPrincipal.add(panelFormulario, BorderLayout.NORTH);
        panelPrincipal.add(new JScrollPane(areaResultado), BorderLayout.CENTER);

        add(panelPrincipal);
    }

    // RF04 - Registra la penalizacion y recalcula la agenda, exactamente
    // igual que interfaz.VistaAgenda.registrarPenalizacion().
    private void onRegistrarPenalizacion() {
        String motivo = campoMotivo.getText().trim();
        int minutos = (Integer) spinnerMinutos.getValue();

        if (motivo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingresa el motivo de la distraccion.",
                    "Dato requerido", JOptionPane.WARNING_MESSAGE);
            return;
        }

        LocalDate hoy = LocalDate.now();
        String fecha = String.format("%02d/%02d/%d",
                hoy.getDayOfMonth(), hoy.getMonthValue(), hoy.getYear());

        String id = "PEN" + String.format("%03d", estudiante.getPenalizaciones().size() + 1);
        Penalizacion pen = new Penalizacion(id, minutos, motivo, fecha);

        AgendaDiaria agenda = gestorAgenda.generarCronograma(estudiante);
        AgendaDiaria nueva = gestorAgenda.aplicarPenalizacion(pen, agenda, estudiante);

        String textoAgenda = CapturadorConsola.capturar(() -> nueva.mostrarAgenda());

        StringBuilder resultado = new StringBuilder();
        resultado.append(pen.generarAlerta());
        resultado.append("\n\n--- Agenda recalculada ---\n");
        resultado.append(textoAgenda);

        areaResultado.setText(resultado.toString());

        campoMotivo.setText("");
        spinnerMinutos.setValue(15);
    }
}