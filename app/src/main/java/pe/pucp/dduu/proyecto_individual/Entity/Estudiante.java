package pe.pucp.dduu.proyecto_individual.Entity;

public class Estudiante {
    private String nombreEstudiante;
    private String nombreApoderado;
    private String numeroTelefonicoApoderado;

    public String getNombreEstudiante() {
        return nombreEstudiante;
    }

    public void setNombreEstudiante(String nombreEstudiante) {
        this.nombreEstudiante = nombreEstudiante;
    }

    public String getNombreApoderado() {
        return nombreApoderado;
    }

    public void setNombreApoderado(String nombreApoderado) {
        this.nombreApoderado = nombreApoderado;
    }

    public String getNumeroTelefonicoApoderado() {
        return numeroTelefonicoApoderado;
    }

    public void setNumeroTelefonicoApoderado(String numeroTelefonicoApoderado) {
        this.numeroTelefonicoApoderado = numeroTelefonicoApoderado;
    }
}
