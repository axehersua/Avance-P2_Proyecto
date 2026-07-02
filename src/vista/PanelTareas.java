package vista;

import excepciones.ValidacionException;
import modelo.Estudiante;
import modelo.Tarea;
import negocio.GestorPrioridad;
import util.Validador;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

/**
 * RF01/RF02 - Panel de gestion de tareas (version dashboard de
 * VentanaTareas). Llama exactamente a los mismos metodos de modelo.Tarea,
 * modelo.Estudiante y negocio.GestorPrioridad. No se modifica ninguna
 * clase de modelo ni de negocio.
 */
public class PanelTareas extends JPanel implements Refrescable {

    private static final String[] COLUMNAS = {
            "ID", "Nombre", "Entrega", "Dificultad", "Prioridad", "Estado"
    };

    private Estudiante estudiante;
    private GestorPrioridad gestorPrioridad;

    private DefaultTableModel modeloTabla;
    private JTable tablaTareas;

    private JTextField campoNombre;
    private JTextField campoDeadline;
    private JSpinner spinnerDificultad;

    public PanelTareas(Estudiante estudiante) {
        this.estudiante = estudiante;
        this.gestorPrioridad = new GestorPrioridad();
        setLayout(new BorderLayout(0, 16));
        setBackground(EstiloUI.COLOR_FONDO);
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        construirInterfaz();
    }

    private void construirInterfaz() {
        add(EstiloUI.crearTitulo("Gestion de Tareas"), BorderLayout.NORTH);

        JPanel centro = new JPanel(new BorderLayout(0, 16));
        centro.setBackground(EstiloUI.COLOR_FONDO);
        centro.add(crearPanelTabla(), BorderLayout.CENTER);
        centro.add(crearPanelFormulario(), BorderLayout.SOUTH);

        add(centro, BorderLayout.CENTER);
    }

    private JPanel crearPanelTabla() {
        JPanel contenedor = EstiloUI.crearTarjeta();
        contenedor.setLayout(new BorderLayout());

        modeloTabla = new DefaultTableModel(COLUMNAS, 0) {
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
        tablaTareas = new JTable(modeloTabla);
        tablaTareas.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        EstiloUI.estilizarTabla(tablaTareas);

        contenedor.add(new JScrollPane(tablaTareas), BorderLayout.CENTER);
        return contenedor;
    }

    private JPanel crearPanelFormulario() {
        JPanel panelExterior = new JPanel(new BorderLayout(0, 10));
        panelExterior.setBackground(EstiloUI.COLOR_FONDO);

        JPanel tarjetaRegistro = EstiloUI.crearTarjeta();
        tarjetaRegistro.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel subtitulo = EstiloUI.crearSubtitulo("Registrar nueva tarea");
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 6;
        tarjetaRegistro.add(subtitulo, gbc);
        gbc.gridwidth = 1;

        gbc.gridx = 0; gbc.gridy = 1;
        tarjetaRegistro.add(new JLabel("Nombre:"), gbc);
        gbc.gridx = 1;
        campoNombre = new JTextField(14);
        tarjetaRegistro.add(campoNombre, gbc);

        gbc.gridx = 2; gbc.gridy = 1;
        tarjetaRegistro.add(new JLabel("Entrega (DD/MM/YYYY):"), gbc);
        gbc.gridx = 3;
        campoDeadline = new JTextField(10);
        tarjetaRegistro.add(campoDeadline, gbc);

        gbc.gridx = 4; gbc.gridy = 1;
        tarjetaRegistro.add(new JLabel("Dificultad:"), gbc);
        gbc.gridx = 5;
        spinnerDificultad = new JSpinner(new SpinnerNumberModel(1, 1, 5, 1));
        tarjetaRegistro.add(spinnerDificultad, gbc);

        JButton botonRegistrar = EstiloUI.crearBotonPrimario("Registrar tarea");
        botonRegistrar.addActionListener(e -> onRegistrarTarea());
        gbc.gridx = 6; gbc.gridy = 1;
        tarjetaRegistro.add(botonRegistrar, gbc);

        JPanel panelAcciones = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panelAcciones.setBackground(EstiloUI.COLOR_FONDO);
        JButton botonCompletar = EstiloUI.crearBotonSecundario("Completar seleccionada");
        botonCompletar.addActionListener(e -> onCompletarTarea());
        JButton botonEliminar = EstiloUI.crearBotonSecundario("Eliminar seleccionada");
        botonEliminar.addActionListener(e -> onEliminarTarea());
        JButton botonRefrescar = EstiloUI.crearBotonSecundario("Refrescar lista");
        botonRefrescar.addActionListener(e -> refrescar());

        panelAcciones.add(botonCompletar);
        panelAcciones.add(botonEliminar);
        panelAcciones.add(botonRefrescar);

        panelExterior.add(tarjetaRegistro, BorderLayout.NORTH);
        panelExterior.add(panelAcciones, BorderLayout.SOUTH);
        return panelExterior;
    }

    // Recalcula prioridades, ordena y vuelve a llenar la tabla. Se llama
    // automaticamente cada vez que el usuario navega a esta seccion.
    public void refrescar() {
        ArrayList<Tarea> tareas = estudiante.getTareas();
        gestorPrioridad.recalcularTodas(tareas);
        gestorPrioridad.ordenarTareas(tareas);

        modeloTabla.setRowCount(0);
        for (int i = 0; i < tareas.size(); i++) {
            Tarea t = tareas.get(i);
            modeloTabla.addRow(new Object[]{
                    t.getId(), t.getNombre(), t.getDeadline(),
                    t.getDificultad() + "/5", t.getPrioridad(), t.getEstado()
            });
        }
    }

    private void onRegistrarTarea() {
        String nombre = campoNombre.getText().trim();
        String deadline = campoDeadline.getText().trim();
        int dificultad = (Integer) spinnerDificultad.getValue();

        try {
            Validador.validarTextoNoVacio(nombre, "Nombre de la tarea");
            Validador.validarFecha(deadline);
            Validador.validarDificultad(dificultad);
        } catch (ValidacionException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Datos invalidos", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String id = estudiante.generarNuevoIdTarea();
        Tarea tarea = new Tarea(id, nombre, deadline, dificultad);
        int dias = tarea.calcularDiasRestantes();

        if (dias < 0) {
            int opcion = JOptionPane.showConfirmDialog(this,
                    "La fecha " + deadline + " ya paso hace " + Math.abs(dias) +
                            " dia(s).\nDeseas registrarla igual como ATRASADA?",
                    "Fecha vencida", JOptionPane.YES_NO_OPTION);
            if (opcion != JOptionPane.YES_OPTION) {
                return;
            }
        }

        gestorPrioridad.calcularPrioridad(tarea);
        estudiante.registrarTarea(tarea);

        campoNombre.setText("");
        campoDeadline.setText("");
        spinnerDificultad.setValue(1);

        refrescar();
    }

    private void onCompletarTarea() {
        String id = obtenerIdSeleccionado();
        if (id == null) return;

        Tarea tarea = estudiante.buscarTareaPorId(id);
        if (tarea == null) return;

        if (tarea.getEstado().equals(Tarea.COMPLETADA)) {
            JOptionPane.showMessageDialog(this,
                    "Esta tarea ya fue completada el " + tarea.getFechaCompletada() +
                            " a las " + tarea.getHoraCompletada() + ".",
                    "Tarea ya completada", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        tarea.completar();
        JOptionPane.showMessageDialog(this,
                "Tarea \"" + tarea.getNombre() + "\" completada el " +
                        tarea.getFechaCompletada() + " a las " + tarea.getHoraCompletada() + ".",
                "Tarea completada", JOptionPane.INFORMATION_MESSAGE);
        refrescar();
    }

    private void onEliminarTarea() {
        String id = obtenerIdSeleccionado();
        if (id == null) return;

        Tarea tarea = estudiante.buscarTareaPorId(id);
        if (tarea == null) return;

        int confirmacion = JOptionPane.showConfirmDialog(this,
                "Eliminar la tarea \"" + tarea.getNombre() + "\"?",
                "Confirmar eliminacion", JOptionPane.YES_NO_OPTION);
        if (confirmacion == JOptionPane.YES_OPTION) {
            estudiante.eliminarTarea(id);
            refrescar();
        }
    }

    private String obtenerIdSeleccionado() {
        int fila = tablaTareas.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona una tarea de la tabla primero.",
                    "Ninguna tarea seleccionada", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        return (String) modeloTabla.getValueAt(fila, 0);
    }
}