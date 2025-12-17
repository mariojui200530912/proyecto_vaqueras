package com.proyectobackend.rest.api.juegosapp.dtos.empresa;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class EmpresaResponse {
    private int id;
    private String nombre;
    private String descripcion;
    private String logo;
    private BigDecimal comisionEspecifica;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime fechaCreacion;
    private boolean permiteComentarios;

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

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public BigDecimal getComisionEspecifica() {
        return comisionEspecifica;
    }

    public void setComisionEspecifica(BigDecimal comisionEspecifica) {
        this.comisionEspecifica = comisionEspecifica;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public boolean isPermiteComentarios() {
        return permiteComentarios;
    }

    public void setPermiteComentarios(boolean permiteComentarios) {
        this.permiteComentarios = permiteComentarios;
    }
}
