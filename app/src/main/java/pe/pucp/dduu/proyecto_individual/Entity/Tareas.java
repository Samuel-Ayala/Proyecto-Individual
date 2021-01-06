package pe.pucp.dduu.proyecto_individual.Entity;

import java.io.Serializable;

public class Tareas implements Serializable {
    private String contenido;
    private String cursoTarea;
    private String materiales;
    private String fechaLimite;
    private String urlFoto;
    private String tituloBase;

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public String getCursoTarea() {
        return cursoTarea;
    }

    public void setCursoTarea(String cursoTarea) {
        this.cursoTarea = cursoTarea;
    }

    public String getMateriales() {
        return materiales;
    }

    public void setMateriales(String materiales) {
        this.materiales = materiales;
    }

    public String getFechaLimite() {
        return fechaLimite;
    }

    public void setFechaLimite(String fechaLimite) {
        this.fechaLimite = fechaLimite;
    }

    public String getUrlFoto() {
        return urlFoto;
    }

    public void setUrlFoto(String urlFoto) {
        this.urlFoto = urlFoto;
    }

    public String getTituloBase() {
        return tituloBase;
    }

    public void setTituloBase(String tituloBase) {
        this.tituloBase = tituloBase;
    }
}
