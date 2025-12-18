package com.proyectobackend.rest.api.juegosapp.models;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Empresa {
    private int id;
    private String nombre;
    private String descripcion;
    private byte[] logo;
    private BigDecimal comisionEspecifica;
    private LocalDateTime fecha_creacion;
    private Boolean permiteComentarios;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public byte[] getLogo() {
        return logo;
    }

    public void setLogo(byte[] logo) {
        this.logo = logo;
    }

    public BigDecimal getComisionEspecifica() {
        return comisionEspecifica;
    }

    public void setComisionEspecifica(BigDecimal comisionEspecifica) {
        this.comisionEspecifica = comisionEspecifica;
    }

    public LocalDateTime getFecha_creacion() {
        return fecha_creacion;
    }

    public void setFecha_creacion(LocalDateTime fecha_creacion) {
        this.fecha_creacion = fecha_creacion;
    }

    public Boolean getPermiteComentarios() {
        return permiteComentarios;
    }

    public void setPermiteComentarios(Boolean permiteComentarios) {
        this.permiteComentarios = permiteComentarios;
    }
}
