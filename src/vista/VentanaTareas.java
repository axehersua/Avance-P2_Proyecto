package vista;

import modelo.Estudiante;
import modelo.Tarea;
import negocio.GestorPrioridad;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

/**
 * RF01/RF02 - Ventana de gestion de tareas (CRUD).
 * Reemplaza el submenu "Gestionar tareas" de interfaz.MenuPrincipal, pero
 * con una tabla Swing en vez de imprimir en consola. Llama exactamente a
 * los mismos metodos de modelo.Tarea, modelo.Estudiante y
 * negocio.GestorPrioridad que ya existen. No se modifica ninguna clase de
 * modelo ni de negocio.
 */
public class VentanaTareas extends JFrame {

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

    public VentanaTareas(Estudiante estudiante) {
        this.estudiante = estudiante;
        this.gestorPrioridad = new GestorPrioridad();
        configurarVentana();
        construirInterfaz();
        refrescarTabla();
    }

    private void configurarVentana() {
        setTitle("Smart Planner - Tareas");
        setSize(650, 520);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void construirInterfaz() {
        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panelPrincipal.add(crearPanelTabla(), BorderLayout.CENTER);
        panelPrincipal.add(crearPanelFormulario(), BorderLayout.SOUTH);

        add(panelPrincipal);
    }

    private JScrollPane crearPanelTabla() {
        modeloTabla = new DefaultTableModel(COLUMNAS, 0) {
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
        tablaTareas = new JTable(modeloTabla);
        tablaTareas.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        return new JScrollPane(tablaTareas);
    }

    private JPanel crearPanelFormulario() {
        JPanel panelExterior = new JPanel(new BorderLayout(5, 5));

        // --- Formulario de registro ---
        JPanel panelRegistro = new JPanel(new GridBagLayout());
        panelRegistro.setBorder(BorderFactory.createTitledBorder("Registrar nueva tarea"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        panelRegistro.add(new JLabel("Nombre:"), gbc);
        gbc.gridx = 1;
        campoNombre = new JTextField(14);
        panelRegistro.add(campoNombre, gbc);

        gbc.gridx = 2; gbc.gridy = 0;
        panelRegistro.add(new JLabel("Entrega (DD/MM/YYYY):"), gbc);
        gbc.gridx = 3;
        campoDeadline = new JTextField(10);
        panelRegistro.add(campoDeadline, gbc);

        gbc.gridx = 4; gbc.gridy = 0;
        panelRegistro.add(new JLabel("Dificultad:"), gbc);
        gbc.gridx = 5;
        spinnerDificultad = new JSpinner(new SpinnerNumberModel(1, 1, 5, 1));
        panelRegistro.add(spinnerDificultad, gbc);

        JButton botonRegistrar = new JButton("Registrar tarea");
        botonRegistrar.addActionListener(e -> onRegistrarTarea());
        gbc.gridx = 6; gbc.gridy = 0;
        panelRegistro.add(botonRegistrar, gbc);

        // --- Botones de accion sobre la seleccion ---
        JPanel panelAcciones = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton botonCompletar = new JButton("Completar seleccionada");
        botonCompletar.addActionListener(e -> onCompletarTarea());
        JButton botonEliminar = new JButton("Eliminar seleccionada");
        botonEliminar.addActionListener(e -> onEliminarTarea());
        JButton botonRefrescar = new JButton("Refrescar lista");
        botonRefrescar.addActionListener(e -> refrescarTabla());

        panelAcciones.add(botonCompletar);
        panelAcciones.add(botonEliminar);
        panelAcciones.add(botonRefrescar);

        panelExterior.add(panelRegistro, BorderLayout.NORTH);
        panelExterior.add(panelAcciones, BorderLayout.SOUTH);
        return panelExterior;
    }

    // Recalcula prioridades, ordena y vuelve a llenar la tabla.
    // Mismo flujo que interfaz.MenuPrincipal.listarTareas().
    private void refrescarTabla() {
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

    // RF01 - Registrar tarea con validacion de fecha, igual que
    // interfaz.MenuPrincipal.registrarTarea().
    private void onRegistrarTarea() {
        String nombre = campoNombre.getText().trim();
        String deadline = campoDeadline.getText().trim();
        int dificultad = (Integer) spinnerDificultad.getValue();

        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingresa el nombre de la tarea.",
                    "Datos incompletos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!fechaValida(deadline)) {
            JOptionPane.showMessageDialog(this,
                    "Formato de fecha invalido. Usa DD/MM/YYYY (ej: 15/07/2026).",
                    "Fecha invalida", JOptionPane.ERROR_MESSAGE);
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

        refrescarTabla();
    }

    // Pide una fecha DD/MM/YYYY valida. Corrige el bug donde una fecha mal
    // escrita se interpretaba silenciosamente como "HOY".
    private boolean fechaValida(String fecha) {
        try {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate.parse(fecha, fmt);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // Marca como completada la tarea seleccionada en la tabla.
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
        refrescarTabla();
    }

    // Elimina la tarea seleccionada en la tabla.
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
            refrescarTabla();
        }
    }

    // Obtiene el ID (columna 0) de la fila seleccionada en la tabla.
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