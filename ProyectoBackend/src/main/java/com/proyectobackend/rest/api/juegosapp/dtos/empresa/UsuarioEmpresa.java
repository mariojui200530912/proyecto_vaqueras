package com.proyectobackend.rest.api.juegosapp.dtos.empresa;

public class UsuarioEmpresa {
    private int idUsuario;
    private String nickname;
    private String email;
    private String rolEnEmpresa;

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRolEnEmpresa() {
        return rolEnEmpresa;
    }

    public void setRolEnEmpresa(String rolEnEmpresa) {
        this.rolEnEmpresa = rolEnEmpresa;
    }
}
