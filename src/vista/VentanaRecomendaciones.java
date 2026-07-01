package vista;

import modelo.Estudiante;
import negocio.GestorRecomendaciones;

import javax.swing.*;
import java.awt.*;

/**
 * RF07 - Ventana de recomendaciones.
 * Reemplaza interfaz.VistaAgenda.mostrarRecomendaciones(). Llama
 * exactamente a negocio.GestorRecomendaciones.generarRecomendaciones(),
 * capturando lo que imprime por consola para mostrarlo en pantalla.
 * No se modifica ninguna clase de modelo ni de negocio.
 */
public class VentanaRecomendaciones extends JFrame {

    private Estudiante estudiante;
    private GestorRecomendaciones gestorRecomendaciones;

    private JTextArea areaRecomendaciones;

    public VentanaRecomendaciones(Estudiante estudiante) {
        this.estudiante = estudiante;
        this.gestorRecomendaciones = new GestorRecomendaciones();
        configurarVentana();
        construirInterfaz();
        generarYMostrar();
    }

    private void configurarVentana() {
        setTitle("Smart Planner - Recomendaciones");
        setSize(550, 550);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void construirInterfaz() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton botonActualizar = new JButton("Actualizar recomendaciones");
        botonActualizar.addActionListener(e -> generarYMostrar());

        areaRecomendaciones = new JTextArea();
        areaRecomendaciones.setEditable(false);
        areaRecomendaciones.setFont(new Font("Monospaced", Font.PLAIN, 12));

        panel.add(botonActualizar, BorderLayout.NORTH);
        panel.add(new JScrollPane(areaRecomendaciones), BorderLayout.CENTER);

        add(panel);
    }

    private void generarYMostrar() {
        String texto = CapturadorConsola.capturar(
                () -> gestorRecomendaciones.generarRecomendaciones(estudiante));
        areaRecomendaciones.setText(texto);
    }
}