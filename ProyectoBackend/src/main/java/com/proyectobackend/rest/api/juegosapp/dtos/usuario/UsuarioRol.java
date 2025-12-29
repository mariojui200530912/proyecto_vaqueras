package com.proyectobackend.rest.api.juegosapp.dtos.usuario;

public class UsuarioRol {
    private Integer idUsuario;
    private String rolEmpresa;

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getRolEmpresa() {
        return rolEmpresa;
    }

    public void setRolEmpresa(String rolEmpresa) {
        this.rolEmpresa = rolEmpresa;
    }
}
