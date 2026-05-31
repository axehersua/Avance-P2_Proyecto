package interfaz;

import modelo.Estudiante;
import negocio.GestorUsuarios;
import java.util.Scanner;

public class MenuInicio {

    private Scanner        scanner;
    private GestorUsuarios gestorUsuarios; // Administra todos los usuarios del sistema

    public MenuInicio() {
        this.scanner        = new Scanner(System.in);
        this.gestorUsuarios = new GestorUsuarios();
    }

    public void iniciar() {
        System.out.println("========================================");
        System.out.println("   SMART PLANNER - Gestion de Tiempo   ");
        System.out.println("========================================");

        boolean ejecutando = true;

        while (ejecutando) {
            System.out.println();
            System.out.println("========================================");
            System.out.println("           MENU DE INICIO              ");
            System.out.println("========================================");
            System.out.println("  1. Registrar nuevo usuario");
            System.out.println("  2. Iniciar sesion");
            System.out.println("  0. Salir");
            System.out.println("----------------------------------------");
            System.out.print("  Opcion: ");

            String opcion = scanner.nextLine().trim();

            if (opcion.equals("1")) {
                registrarUsuario();
            } else if (opcion.equals("2")) {
                // Intentar login y abrir MenuPrincipal si es exitoso
                Estudiante estudiante = login();
                if (estudiante != null) {
                    // Pasar el mismo scanner para no crear uno nuevo
                    MenuPrincipal menuPrincipal = new MenuPrincipal(scanner, estudiante);
                    menuPrincipal.iniciar();
                }
            } else if (opcion.equals("0")) {
                ejecutando = false;
                System.out.println("  Hasta pronto!");
            } else {
                System.out.println("  Opcion no valida.");
            }
        }

        scanner.close();
    }

    // Registra un nuevo usuario en el sistema
    // GestorUsuarios valida que no exista un usuario con el mismo nombre
    private void registrarUsuario() {
        System.out.println();
        System.out.println("========================================");
        System.out.println("       REGISTRO DE NUEVO USUARIO       ");
        System.out.println("========================================");

        System.out.print("  Nombre de usuario: ");
        String nombre = scanner.nextLine().trim();

        System.out.print("  Correo: ");
        String correo = scanner.nextLine().trim();

        System.out.print("  Contrasena: ");
        String contrasena = scanner.nextLine().trim();

        gestorUsuarios.registrar(nombre, correo, contrasena);
    }

    // Autentica al usuario por nombre y contrasena
    // Retorna el objeto Estudiante si las credenciales son correctas, null si no
    private Estudiante login() {
        System.out.println();
        System.out.println("========================================");
        System.out.println("           INICIAR SESION              ");
        System.out.println("========================================");

        System.out.print("  Nombre de usuario: ");
        String nombre = scanner.nextLine().trim();

        System.out.print("  Contrasena: ");
        String contrasena = scanner.nextLine().trim();

        Estudiante estudiante = gestorUsuarios.iniciarSesion(nombre, contrasena);

        if (estudiante != null) {
            System.out.println("  Sesion iniciada correctamente.");
        } else {
            System.out.println("  ! Nombre o contrasena incorrectos.");
        }

        return estudiante;
    }
}
