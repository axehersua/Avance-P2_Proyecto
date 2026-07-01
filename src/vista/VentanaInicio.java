package vista;

import modelo.Estudiante;
import negocio.GestorUsuarios;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * RF08 - Ventana de inicio (login / registro).
 * Reemplaza a interfaz.MenuInicio pero llamando exactamente a los mismos
 * metodos de negocio.GestorUsuarios (iniciarSesion, registrar).
 * No se modifica ninguna clase de modelo ni de negocio.
 */
public class VentanaInicio extends JFrame {

    private GestorUsuarios gestorUsuarios;

    private CardLayout cardLayout;
    private JPanel panelContenedor;

    // Campos del panel de login
    private JTextField campoUsuarioLogin;
    private JPasswordField campoContrasenaLogin;

    // Campos del panel de registro
    private JTextField campoUsuarioRegistro;
    private JTextField campoCorreoRegistro;
    private JPasswordField campoContrasenaRegistro;

    public VentanaInicio() {
        this.gestorUsuarios = new GestorUsuarios();
        configurarVentana();
        construirInterfaz();
    }

    private void configurarVentana() {
        setTitle("Smart Planner - Inicio");
        setSize(420, 320);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
    }

    private void construirInterfaz() {
        cardLayout = new CardLayout();
        panelContenedor = new JPanel(cardLayout);

        panelContenedor.add(crearPanelLogin(), "LOGIN");
        panelContenedor.add(crearPanelRegistro(), "REGISTRO");

        add(panelContenedor);
        cardLayout.show(panelContenedor, "LOGIN");
    }

    private JPanel crearPanelLogin() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titulo = new JLabel("SMART PLANNER", SwingConstants.CENTER);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 18));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(titulo, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 1; gbc.gridx = 0;
        panel.add(new JLabel("Usuario:"), gbc);
        gbc.gridx = 1;
        campoUsuarioLogin = new JTextField(15);
        panel.add(campoUsuarioLogin, gbc);

        gbc.gridy = 2; gbc.gridx = 0;
        panel.add(new JLabel("Contrasena:"), gbc);
        gbc.gridx = 1;
        campoContrasenaLogin = new JPasswordField(15);
        panel.add(campoContrasenaLogin, gbc);

        JButton botonLogin = new JButton("Iniciar sesion");
        botonLogin.addActionListener(this::onIniciarSesion);
        gbc.gridy = 3; gbc.gridx = 0; gbc.gridwidth = 2;
        panel.add(botonLogin, gbc);

        JButton botonIrARegistro = new JButton("No tengo cuenta, registrarme");
        botonIrARegistro.addActionListener(e -> cardLayout.show(panelContenedor, "REGISTRO"));
        gbc.gridy = 4;
        panel.add(botonIrARegistro, gbc);

        return panel;
    }

    private JPanel crearPanelRegistro() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titulo = new JLabel("REGISTRO DE USUARIO", SwingConstants.CENTER);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 16));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(titulo, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 1; gbc.gridx = 0;
        panel.add(new JLabel("Usuario:"), gbc);
        gbc.gridx = 1;
        campoUsuarioRegistro = new JTextField(15);
        panel.add(campoUsuarioRegistro, gbc);

        gbc.gridy = 2; gbc.gridx = 0;
        panel.add(new JLabel("Correo:"), gbc);
        gbc.gridx = 1;
        campoCorreoRegistro = new JTextField(15);
        panel.add(campoCorreoRegistro, gbc);

        gbc.gridy = 3; gbc.gridx = 0;
        panel.add(new JLabel("Contrasena:"), gbc);
        gbc.gridx = 1;
        campoContrasenaRegistro = new JPasswordField(15);
        panel.add(campoContrasenaRegistro, gbc);

        JButton botonRegistrar = new JButton("Registrarse");
        botonRegistrar.addActionListener(this::onRegistrar);
        gbc.gridy = 4; gbc.gridx = 0; gbc.gridwidth = 2;
        panel.add(botonRegistrar, gbc);

        JButton botonVolver = new JButton("Volver a inicio de sesion");
        botonVolver.addActionListener(e -> cardLayout.show(panelContenedor, "LOGIN"));
        gbc.gridy = 5;
        panel.add(botonVolver, gbc);

        return panel;
    }

    // Llama a GestorUsuarios.iniciarSesion() exactamente igual que lo hacia
    // interfaz.MenuInicio.login(), solo que la entrada viene de JTextField.
    private void onIniciarSesion(ActionEvent evento) {
        String usuario = campoUsuarioLogin.getText().trim();
        String contrasena = new String(campoContrasenaLogin.getPassword());

        if (usuario.isEmpty() || contrasena.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Completa usuario y contrasena.",
                    "Datos incompletos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Estudiante estudiante = gestorUsuarios.iniciarSesion(usuario, contrasena);

        if (estudiante != null) {
            dispose();
            new VentanaPrincipal(estudiante).setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Usuario o contrasena incorrectos.",
                    "Error de inicio de sesion", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Llama a GestorUsuarios.registrar() exactamente igual que lo hacia
    // interfaz.MenuInicio.registrarUsuario().
    private void onRegistrar(ActionEvent evento) {
        String usuario = campoUsuarioRegistro.getText().trim();
        String correo = campoCorreoRegistro.getText().trim();
        String contrasena = new String(campoContrasenaRegistro.getPassword());

        if (usuario.isEmpty() || correo.isEmpty() || contrasena.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Completa todos los campos.",
                    "Datos incompletos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean exito = gestorUsuarios.registrar(usuario, correo, contrasena);

        if (exito) {
            JOptionPane.showMessageDialog(this,
                    "Usuario registrado correctamente. Ya puedes iniciar sesion.",
                    "Registro exitoso", JOptionPane.INFORMATION_MESSAGE);
            campoUsuarioRegistro.setText("");
            campoCorreoRegistro.setText("");
            campoContrasenaRegistro.setText("");
            cardLayout.show(panelContenedor, "LOGIN");
        } else {
            JOptionPane.showMessageDialog(this, "Ya existe un usuario con ese nombre.",
                    "Error de registro", JOptionPane.ERROR_MESSAGE);
        }
    }
}