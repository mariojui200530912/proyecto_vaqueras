package com.proyectobackend.rest.api.juegosapp.models;

public class UsuarioEmpresa {
    private int id;
    private int id_usuario;
    private int id_empresa;
    private String rol_empresa;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId_usuario() {
        return id_usuario;
    }

    public void setId_usuario(int id_usuario) {
        this.id_usuario = id_usuario;
    }

    public int getId_empresa() {
        return id_empresa;
    }

    public void setId_empresa(int id_empresa) {
        this.id_empresa = id_empresa;
    }

    public String getRol_empresa() {
        return rol_empresa;
    }

    public void setRol_empresa(String rol_empresa) {
        this.rol_empresa = rol_empresa;
    }
}
