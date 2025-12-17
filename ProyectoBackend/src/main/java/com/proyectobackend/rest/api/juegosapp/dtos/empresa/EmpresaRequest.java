package com.proyectobackend.rest.api.juegosapp.dtos.empresa;

import java.math.BigDecimal;

public class EmpresaRequest {
    private String nombre;
    private String descripcion;
    private BigDecimal comisionEspecifica; // Puede ser null
    private Boolean permiteComentarios;

    // Validaciones
    public boolean isValid() {
        return nombre != null && !nombre.trim().isEmpty()
                && descripcion != null && !descripcion.trim().isEmpty()
                && comisionEspecifica != null;
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

    public BigDecimal getComisionEspecifica() {
        return comisionEspecifica;
    }

    public void setComisionEspecifica(BigDecimal comisionEspecifica) {
        this.comisionEspecifica = comisionEspecifica;
    }

    public Boolean getPermiteComentarios() {
        return permiteComentarios;
    }

    public void setPermiteComentarios(Boolean permiteComentarios) {
        this.permiteComentarios = permiteComentarios;
    }
}
