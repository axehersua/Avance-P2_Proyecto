package vista;

import modelo.Estudiante;

import javax.swing.*;
import java.awt.*;

public class VentanaPrincipal extends JFrame {

    private Estudiante estudiante;

    public VentanaPrincipal(Estudiante estudiante) {
        this.estudiante = estudiante;
        configurarVentana();
        construirInterfaz();
    }

    private void configurarVentana() {
        setTitle("Smart Planner - " + estudiante.getNombre());
        setSize(500, 420);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void construirInterfaz() {
        JPanel panel = new JPanel(new GridLayout(0, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel bienvenida = new JLabel("Bienvenido, " + estudiante.getNombre() + "!", SwingConstants.CENTER);
        bienvenida.setFont(new Font("SansSerif", Font.BOLD, 16));
        panel.add(bienvenida);

        JButton botonPerfil = new JButton("Ver / Actualizar perfil");
        botonPerfil.addActionListener(e -> new VentanaPerfil(estudiante).setVisible(true));

        JButton botonTareas = new JButton("Gestionar tareas");
        botonTareas.addActionListener(e -> new VentanaTareas(estudiante).setVisible(true));

        JButton botonAgenda = new JButton("Ver agenda del dia");
        botonAgenda.addActionListener(e -> new VentanaAgenda(estudiante).setVisible(true));

        JButton botonPenalizacion = new JButton("Registrar penalizacion");
        botonPenalizacion.addActionListener(e -> new VentanaPenalizacion(estudiante).setVisible(true));

        JButton botonRecomendaciones = new JButton("Ver recomendaciones");
        botonRecomendaciones.addActionListener(e -> new VentanaRecomendaciones(estudiante).setVisible(true));

        panel.add(botonPerfil);
        panel.add(botonTareas);
        panel.add(botonAgenda);
        panel.add(botonPenalizacion);
        panel.add(botonRecomendaciones);

        JButton botonCerrarSesion = new JButton("Cerrar sesion");
        botonCerrarSesion.addActionListener(e -> {
            dispose();
            new VentanaInicio().setVisible(true);
        });
        panel.add(botonCerrarSesion);

        add(panel);
    }
}