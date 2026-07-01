package modelo;

import java.util.ArrayList;

public class AgendaDiaria {

    private String fecha;                        // Formato DD/MM/YYYY
    private ArrayList<BloqueHorario> bloques;    // Bloques con informacion combinada
    private int horasLibres;

    public AgendaDiaria(String fecha) {
        this.fecha       = fecha;
        this.bloques     = new ArrayList<BloqueHorario>();
        this.horasLibres = 0;
    }

    // Agrega un bloque completo con horario y tipo
    public void agregarBloque(BloqueHorario bloque) {
        bloques.add(bloque);
    }

    // Agrega un bloque de aviso simple (sin horario)
    public void agregarAviso(String descripcion) {
        bloques.add(new BloqueHorario(descripcion, BloqueHorario.TIPO_AVISO));
    }

    public void limpiar() {
        bloques.clear();
        horasLibres = 0;
    }

    // Muestra la agenda completa en consola
    public void mostrarAgenda() {
        System.out.println("  Fecha            : " + fecha);
        System.out.println("  Horas disponibles: " + horasLibres + "h");
        System.out.println("  " + "-".repeat(52));

        if (bloques.size() == 0) {
            System.out.println("  No hay bloques programados.");
        } else {
            for (int i = 0; i < bloques.size(); i++) {
                bloques.get(i).mostrar();
            }
        }
    }

    // Filtra y retorna solo los bloques de un tipo especifico
    public ArrayList<BloqueHorario> getBloquePorTipo(String tipo) {
        ArrayList<BloqueHorario> resultado = new ArrayList<BloqueHorario>();
        for (int i = 0; i < bloques.size(); i++) {
            if (bloques.get(i).getTipo().equals(tipo)) {
                resultado.add(bloques.get(i));
            }
        }
        return resultado;
    }

    public String toString() {
        return "Agenda del " + fecha + " | Horas libres: " + horasLibres + "h" +
               " | Bloques: " + bloques.size();
    }

    // Getters y Setters
    public String getFecha()                          { return fecha; }
    public ArrayList<BloqueHorario> getBloques()      { return bloques; }
    public int getHorasLibres()                       { return horasLibres; }
    public void setHorasLibres(int horasLibres)       { this.horasLibres = horasLibres; }
}
