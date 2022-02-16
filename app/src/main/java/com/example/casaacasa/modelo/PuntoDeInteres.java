package com.example.casaacasa.modelo;

import java.util.ArrayList;
import java.util.Date;

public class PuntoDeInteres {
    private String titulo;
    private String direccion;
    private Usuario usuario;
    private ArrayList<Valoracion> valoraciones;
    private String descripcion;
    private Date fechaCreacion;

    public PuntoDeInteres(String titulo, String direccion, Usuario usuario, ArrayList<Valoracion> valoraciones, String descripcion, Date fechaCreacion) {
        this.titulo = titulo;
        this.direccion = direccion;
        this.usuario = usuario;
        this.valoraciones = valoraciones;
        this.descripcion = descripcion;
        this.fechaCreacion = fechaCreacion;
    }

    public void calcularDistancia(){

    }

    public void calcularValoracionMedia(){

    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public ArrayList<Valoracion> getValoraciones() {
        return valoraciones;
    }

    public void setValoraciones(ArrayList<Valoracion> valoraciones) {
        this.valoraciones = valoraciones;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Date getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    @Override
    public String toString() {
        return "PuntoDeInteres{" +
                "titulo='" + titulo + '\'' +
                ", direccion='" + direccion + '\'' +
                ", usuario=" + usuario +
                ", valoraciones=" + valoraciones +
                ", descripcion='" + descripcion + '\'' +
                ", fechaCreacion=" + fechaCreacion +
                '}';
    }
}
