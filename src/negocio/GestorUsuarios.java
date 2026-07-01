package negocio;

import modelo.Estudiante;
import java.util.ArrayList;

public class GestorUsuarios {

    private ArrayList<Estudiante> estudiantes;
    private int contadorId;

    public GestorUsuarios() {
        this.estudiantes = new ArrayList<Estudiante>();
        this.contadorId  = 1;
    }

    // Registra un nuevo estudiante si el nombre no existe ya
    public boolean registrar(String nombre, String correo, String contrasena) {
        for (int i = 0; i < estudiantes.size(); i++) {
            if (estudiantes.get(i).getNombre().equalsIgnoreCase(nombre)) {
                System.out.println("  ! Ya existe un usuario con ese nombre.");
                return false;
            }
        }
        String id = "EST" + String.format("%03d", contadorId);
        contadorId++;
        Estudiante nuevo = new Estudiante(id, nombre, correo, contrasena);
        estudiantes.add(nuevo);
        System.out.println("  Usuario registrado correctamente.");
        return true;
    }

    // Busca un estudiante por nombre y contrasena
    public Estudiante iniciarSesion(String nombre, String contrasena) {
        for (int i = 0; i < estudiantes.size(); i++) {
            Estudiante e = estudiantes.get(i);
            if (e.getNombre().equalsIgnoreCase(nombre) &&
                e.getContrasena().equals(contrasena)) {
                return e;
            }
        }
        return null;
    }

    // Muestra la lista de usuarios registrados (solo nombres)
    public void mostrarUsuarios() {
        System.out.println("  Usuarios registrados: " + estudiantes.size());
        for (int i = 0; i < estudiantes.size(); i++) {
            System.out.println("  - " + estudiantes.get(i).getNombre());
        }
    }

    public ArrayList<Estudiante> getEstudiantes() { return estudiantes; }
}
