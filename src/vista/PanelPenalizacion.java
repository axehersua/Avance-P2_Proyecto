package vista;

import excepciones.ValidacionException;
import modelo.AgendaDiaria;
import modelo.Estudiante;
import modelo.Penalizacion;
import negocio.GestorAgenda;
import util.Validador;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;

/**
 * RF04 - Panel de registro de penalizacion (version dashboard de
 * VentanaPenalizacion). Llama exactamente a los mismos metodos de
 * modelo.Penalizacion y negocio.GestorAgenda. No se modifica ninguna
 * clase de modelo ni de negocio.
 */
public class PanelPenalizacion extends JPanel implements Refrescable {

    private Estudiante estudiante;
    private GestorAgenda gestorAgenda;

    private JTextField campoMotivo;
    private JSpinner spinnerMinutos;
    private JTextArea areaResultado;

    public PanelPenalizacion(Estudiante estudiante) {
        this.estudiante = estudiante;
        this.gestorAgenda = new GestorAgenda();
        setLayout(new BorderLayout(0, 16));
        setBackground(EstiloUI.COLOR_FONDO);
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        construirInterfaz();
    }

    private void construirInterfaz() {
        add(EstiloUI.crearTitulo("Registrar Penalizacion"), BorderLayout.NORTH);

        JPanel centro = new JPanel(new BorderLayout(0, 16));
        centro.setBackground(EstiloUI.COLOR_FONDO);

        JPanel tarjetaFormulario = EstiloUI.crearTarjeta();
        tarjetaFormulario.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        tarjetaFormulario.add(new JLabel("Motivo de la distraccion:"), gbc);
        gbc.gridx = 1;
        campoMotivo = new JTextField(18);
        tarjetaFormulario.add(campoMotivo, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        tarjetaFormulario.add(new JLabel("Minutos perdidos:"), gbc);
        gbc.gridx = 1;
        spinnerMinutos = new JSpinner(new SpinnerNumberModel(15, 1, 1440, 1));
        tarjetaFormulario.add(spinnerMinutos, gbc);

        JButton botonRegistrar = EstiloUI.crearBotonPrimario("Registrar");
        botonRegistrar.addActionListener(e -> onRegistrarPenalizacion());
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        tarjetaFormulario.add(botonRegistrar, gbc);

        areaResultado = new JTextArea();
        EstiloUI.estilizarAreaTexto(areaResultado);
        areaResultado.setLineWrap(true);
        areaResultado.setWrapStyleWord(true);

        JScrollPane scroll = new JScrollPane(areaResultado);
        scroll.setBorder(BorderFactory.createLineBorder(EstiloUI.COLOR_BORDE, 1));

        centro.add(tarjetaFormulario, BorderLayout.NORTH);
        centro.add(scroll, BorderLayout.CENTER);

        add(centro, BorderLayout.CENTER);
    }

    public void refrescar() {
        // Intencionalmente vacio: es un formulario de accion, no de lectura.
    }

    private void onRegistrarPenalizacion() {
        String motivo = campoMotivo.getText().trim();
        int minutos = (Integer) spinnerMinutos.getValue();

        try {
            Validador.validarTextoNoVacio(motivo, "Motivo");
            Validador.validarMinutosPerdidos(minutos);
        } catch (ValidacionException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Datos invalidos", JOptionPane.WARNING_MESSAGE);
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