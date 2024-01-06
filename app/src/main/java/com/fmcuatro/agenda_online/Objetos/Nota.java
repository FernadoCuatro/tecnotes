package com.fmcuatro.agenda_online.Objetos;

public class Nota {
    String idNota;
    String uidUsuario;
    String correoUsuario;
    String fechaHoraActual;
    String titulo;
    String descripcion;
    String fechaNota;
    String estado;

    // constructor vacio
    public Nota() {
    }

    // constructor con todos los atributos para inicalizar
    public Nota(String idNota, String uidUsuario, String correoUsuario, String fechaHoraActual, String titulo, String descripcion, String fechaNota, String estado) {
        this.idNota = idNota;
        this.uidUsuario = uidUsuario;
        this.correoUsuario = correoUsuario;
        this.fechaHoraActual = fechaHoraActual;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.fechaNota = fechaNota;
        this.estado = estado;
    }

    // get y set
    public String getIdNota() {
        return idNota;
    }

    public void setIdNota(String idNota) {
        this.idNota = idNota;
    }

    public String getUidUsuario() {
        return uidUsuario;
    }

    public void setUidUsuario(String uidUsuario) {
        this.uidUsuario = uidUsuario;
    }

    public String getCorreoUsuario() {
        return correoUsuario;
    }

    public void setCorreoUsuario(String correoUsuario) {
        this.correoUsuario = correoUsuario;
    }

    public String getFechaHoraActual() {
        return fechaHoraActual;
    }

    public void setFechaHoraActual(String fechaHoraActual) {
        this.fechaHoraActual = fechaHoraActual;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getFechaNota() {
        return fechaNota;
    }

    public void setFechaNota(String fechaNota) {
        this.fechaNota = fechaNota;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String nota) {
        this.estado = nota;
    }
}
