package com.proyectobackend.rest.api.juegosapp.dtos.grupo;

public class AgregarMiembro {
    private int idUsuario;
    private int idGrupo;

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public int getIdGrupo() {
        return idGrupo;
    }

    public void setIdGrupo(int idGrupo) {
        this.idGrupo = idGrupo;
    }
}
