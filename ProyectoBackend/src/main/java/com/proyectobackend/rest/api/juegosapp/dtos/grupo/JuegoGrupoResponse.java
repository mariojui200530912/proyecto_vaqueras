package com.proyectobackend.rest.api.juegosapp.dtos.grupo;

public class JuegoGrupoResponse {
    private int idJuego;
    private String titulo;
    private String portada; // Base64
    private int idDueno;
    private String nombreDueno;
    private String avatarDueno;

    public int getIdJuego() {
        return idJuego;
    }

    public void setIdJuego(int idJuego) {
        this.idJuego = idJuego;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getPortada() {
        return portada;
    }

    public void setPortada(String portada) {
        this.portada = portada;
    }

    public int getIdDueno() {
        return idDueno;
    }

    public void setIdDueno(int idDueno) {
        this.idDueno = idDueno;
    }

    public String getNombreDueno() {
        return nombreDueno;
    }

    public void setNombreDueno(String nombreDueno) {
        this.nombreDueno = nombreDueno;
    }

    public String getAvatarDueno() {
        return avatarDueno;
    }

    public void setAvatarDueno(String avatarDueno) {
        this.avatarDueno = avatarDueno;
    }
}
