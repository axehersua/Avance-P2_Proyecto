package modelo;

public class Penalizacion {

    private String id;
    private int    minutosPerdidos;
    private String motivo;
    private String fecha;

    public Penalizacion(String id, int minutosPerdidos, String motivo, String fecha) {
        this.id              = id;
        this.minutosPerdidos = minutosPerdidos;
        this.motivo          = motivo;
        this.fecha           = fecha;
    }

    public int calcularImpacto() {
        if (minutosPerdidos > 30) {
            return (int)(minutosPerdidos * 1.2);
        }
        return minutosPerdidos;
    }

    public String generarAlerta() {
        int impacto = calcularImpacto();
        if (impacto >= 60) {
            return "! TIEMPO CRITICO: Perdiste " + minutosPerdidos +
                    " min por: " + motivo +
                    ". Impacto real: " + impacto + " min." +
                    " Elimina tareas de baja prioridad de hoy.";
        } else {
            return "! Penalizacion: " + minutosPerdidos +
                    " min por: " + motivo +
                    ". Impacto: " + impacto + " min en tu agenda.";
        }
    }

    public String toString() {
        return "[" + id + "] " + motivo +
                " | Minutos perdidos: " + minutosPerdidos +
                " | Impacto: " + calcularImpacto() + " min" +
                " | Fecha: " + fecha;
    }

    public String getId() { return id; }
    public int getMinutosPerdidos() { return minutosPerdidos; }
    public String getMotivo() { return motivo; }
    public String getFecha() { return fecha; }
}