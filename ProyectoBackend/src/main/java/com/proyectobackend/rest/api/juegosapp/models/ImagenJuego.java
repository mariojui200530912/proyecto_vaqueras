package com.proyectobackend.rest.api.juegosapp.models;

public class ImagenJuego {
    private int id;
    private int idJuego;
    private byte[] imagen;
    private String atributo; // 'PORTADA' o 'GAMEPLAY'

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdJuego() {
        return idJuego;
    }

    public void setIdJuego(int idJuego) {
        this.idJuego = idJuego;
    }

    public byte[] getImagen() {
        return imagen;
    }

    public void setImagen(byte[] imagen) {
        this.imagen = imagen;
    }

    public String getAtributo() {
        return atributo;
    }

    public void setAtributo(String atributo) {
        this.atributo = atributo;
    }
}
