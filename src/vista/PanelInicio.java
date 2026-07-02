package vista;

import modelo.Estudiante;
import modelo.Tarea;
import negocio.GestorPrioridad;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * Pantalla de inicio del dashboard: resumen visual del estado del
 * estudiante (tareas pendientes, criticas, atrasadas, completadas) usando
 * solo los datos que ya expone modelo.Estudiante y modelo.Tarea. No agrega
 * logica de negocio nueva, solo cuenta y muestra.
 */
public class PanelInicio extends JPanel implements Refrescable {

    private Estudiante estudiante;
    private GestorPrioridad gestorPrioridad;

    private JLabel labelBienvenida;
    private JPanel panelTarjetas;

    public PanelInicio(Estudiante estudiante) {
        this.estudiante = estudiante;
        this.gestorPrioridad = new GestorPrioridad();
        setLayout(new BorderLayout(0, 20));
        setBackground(EstiloUI.COLOR_FONDO);
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        construirInterfaz();
        refrescar();
    }

    private void construirInterfaz() {
        labelBienvenida = new JLabel();
        labelBienvenida.setFont(EstiloUI.FUENTE_TITULO);
        labelBienvenida.setForeground(EstiloUI.COLOR_TEXTO);
        add(labelBienvenida, BorderLayout.NORTH);

        panelTarjetas = new JPanel(new GridLayout(1, 4, 16, 0));
        panelTarjetas.setBackground(EstiloUI.COLOR_FONDO);
        add(panelTarjetas, BorderLayout.CENTER);
    }

    public void refrescar() {
        labelBienvenida.setText("Bienvenido, " + estudiante.getNombre() + "!");

        ArrayList<Tarea> tareas = estudiante.getTareas();
        gestorPrioridad.recalcularTodas(tareas);

        int pendientes = 0;
        int criticas = 0;
        int atrasadas = 0;
        int completadas = 0;

        for (int i = 0; i < tareas.size(); i++) {
            Tarea t = tareas.get(i);
            if (t.getEstado().equals(Tarea.COMPLETADA)) {
                completadas++;
            } else if (t.getEstado().equals(Tarea.ATRASADA)) {
                atrasadas++;
            } else {
                pendientes++;
                if (t.getPrioridad().equals(Tarea.PRIORIDAD_CRITICA)) {
                    criticas++;
                }
            }
        }

        panelTarjetas.removeAll();
        panelTarjetas.add(crearTarjeta("Tareas pendientes", String.valueOf(pendientes), EstiloUI.COLOR_ACENTO));
        panelTarjetas.add(crearTarjeta("Criticas hoy", String.valueOf(criticas), EstiloUI.COLOR_PELIGRO));
        panelTarjetas.add(crearTarjeta("Atrasadas", String.valueOf(atrasadas), EstiloUI.COLOR_ADVERTENCIA));
        panelTarjetas.add(crearTarjeta("Completadas", String.valueOf(completadas), EstiloUI.COLOR_EXITO));
        panelTarjetas.revalidate();
        panelTarjetas.repaint();
    }

    private JPanel crearTarjeta(String titulo, String valor, Color colorAcento) {
        JPanel tarjeta = EstiloUI.crearTarjeta();
        tarjeta.setLayout(new BorderLayout(0, 8));

        JPanel barraAcento = new JPanel();
        barraAcento.setBackground(colorAcento);
        barraAcento.setPreferredSize(new Dimension(0, 4));

        JLabel labelValor = new JLabel(valor);
        labelValor.setFont(EstiloUI.FUENTE_TARJETA_VALOR);
        labelValor.setForeground(EstiloUI.COLOR_TEXTO);

        JLabel labelTitulo = new JLabel(titulo);
        labelTitulo.setFont(EstiloUI.FUENTE_TARJETA_TITULO);
        labelTitulo.setForeground(EstiloUI.COLOR_TEXTO_SECUNDARIO);

        JPanel contenido = new JPanel();
        contenido.setBackground(EstiloUI.COLOR_TARJETA);
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
        contenido.add(labelValor);
        contenido.add(Box.createVerticalStrut(4));
        contenido.add(labelTitulo);

        tarjeta.add(barraAcento, BorderLayout.NORTH);
        tarjeta.add(contenido, BorderLayout.CENTER);
        return tarjeta;
    }
}