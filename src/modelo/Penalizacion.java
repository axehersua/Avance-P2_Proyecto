package modelo;

public class Penalizacion {

    private String id;
    private int    minutosPercidos;
    private String motivo;
    private String fecha;

    public Penalizacion(String id, int minutosPercidos, String motivo, String fecha) {
        this.id              = id;
        this.minutosPercidos = minutosPercidos;
        this.motivo          = motivo;
        this.fecha           = fecha;
    }

    public int calcularImpacto() {
        if (minutosPercidos > 30) {
            return (int)(minutosPercidos * 1.2);
        }
        return minutosPercidos;
    }

    public String generarAlerta() {
        int impacto = calcularImpacto();
        if (impacto >= 60) {
            return "! TIEMPO CRITICO: Perdiste " + minutosPercidos +
                   " min por: " + motivo +
                   ". Impacto real: " + impacto + " min." +
                   " Elimina tareas de baja prioridad de hoy.";
        } else {
            return "! Penalizacion: " + minutosPercidos +
                   " min por: " + motivo +
                   ". Impacto: " + impacto + " min en tu agenda.";
        }
    }

    public String toString() {
        return "[" + id + "] " + motivo +
               " | Minutos perdidos: " + minutosPercidos +
               " | Impacto: " + calcularImpacto() + " min" +
               " | Fecha: " + fecha;
    }

    public String getId() { return id; }
    public int getMinutosPercidos() { return minutosPercidos; }
    public String getMotivo() { return motivo; }
    public String getFecha() { return fecha; }
}
