package vista;

import modelo.Estudiante;
import negocio.GestorRecomendaciones;

import javax.swing.*;
import java.awt.*;

/**
 * RF07 - Panel de recomendaciones (version dashboard de
 * VentanaRecomendaciones). Llama exactamente a
 * negocio.GestorRecomendaciones.generarRecomendaciones(), capturando lo
 * que imprime por consola. No se modifica ninguna clase de modelo ni de
 * negocio.
 */
public class PanelRecomendaciones extends JPanel implements Refrescable {

    private Estudiante estudiante;
    private GestorRecomendaciones gestorRecomendaciones;

    private JTextArea areaRecomendaciones;

    public PanelRecomendaciones(Estudiante estudiante) {
        this.estudiante = estudiante;
        this.gestorRecomendaciones = new GestorRecomendaciones();
        setLayout(new BorderLayout(0, 16));
        setBackground(EstiloUI.COLOR_FONDO);
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        construirInterfaz();
    }

    private void construirInterfaz() {
        JPanel encabezado = new JPanel(new BorderLayout());
        encabezado.setBackground(EstiloUI.COLOR_FONDO);
        encabezado.add(EstiloUI.crearTitulo("Recomendaciones"), BorderLayout.WEST);

        JButton botonActualizar = EstiloUI.crearBotonPrimario("Actualizar");
        botonActualizar.addActionListener(e -> generarYMostrar());
        JPanel contenedorBoton = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        contenedorBoton.setBackground(EstiloUI.COLOR_FONDO);
        contenedorBoton.add(botonActualizar);
        encabezado.add(contenedorBoton, BorderLayout.EAST);

        areaRecomendaciones = new JTextArea();
        EstiloUI.estilizarAreaTexto(areaRecomendaciones);

        JScrollPane scroll = new JScrollPane(areaRecomendaciones);
        scroll.setBorder(BorderFactory.createLineBorder(EstiloUI.COLOR_BORDE, 1));

        add(encabezado, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
    }

    // Regenera las recomendaciones cada vez que el usuario navega a esta
    // seccion, para reflejar cambios recientes en tareas/perfil.
    public void refrescar() {
        generarYMostrar();
    }

    private void generarYMostrar() {
        String texto = CapturadorConsola.capturar(
                () -> gestorRecomendaciones.generarRecomendaciones(estudiante));
        areaRecomendaciones.setText(texto);
    }
}