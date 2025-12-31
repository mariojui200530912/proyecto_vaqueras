package com.proyectobackend.rest.api.juegosapp.dtos.juego;

import java.math.BigDecimal;
import java.util.List;

public class JuegoRequest {
    private String titulo;
    private String descripcion;
    private BigDecimal precio;
    private String recursosMinimos;
    private String clasificacion; // E, T, M
    private boolean permiteComentariosJuegos;
    private List<Integer> categoriasIds;

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

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public String getRecursosMinimos() {
        return recursosMinimos;
    }

    public void setRecursosMinimos(String recursosMinimos) {
        this.recursosMinimos = recursosMinimos;
    }

    public String getClasificacion() {
        return clasificacion;
    }

    public void setClasificacion(String clasificacion) {
        this.clasificacion = clasificacion;
    }

    public List<Integer> getCategoriasIds() {
        return categoriasIds;
    }

    public void setCategoriasIds(List<Integer> categoriasIds) {
        this.categoriasIds = categoriasIds;
    }

    public boolean getPermiteComentariosJuegos() {
        return permiteComentariosJuegos;
    }

    public void setPermiteComentariosJuegos(boolean permiteComentariosJuegos) {
        this.permiteComentariosJuegos = permiteComentariosJuegos;
    }
}
