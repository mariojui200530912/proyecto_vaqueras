package com.proyectobackend.rest.api.juegosapp.dtos.grupo;

import com.proyectobackend.rest.api.juegosapp.dtos.usuario.UsuarioResponse;

import java.util.List;

public class GrupoResponse {
    private int id;
    private String nombre;
    private int idCreador;
    private List<UsuarioResponse> miembros;

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

    public int getIdCreador() {
        return idCreador;
    }

    public void setIdCreador(int idCreador) {
        this.idCreador = idCreador;
    }

    public List<UsuarioResponse> getMiembros() {
        return miembros;
    }

    public void setMiembros(List<UsuarioResponse> miembros) {
        this.miembros = miembros;
    }
}
