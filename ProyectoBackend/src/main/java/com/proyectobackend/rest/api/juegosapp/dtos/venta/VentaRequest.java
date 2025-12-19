package com.proyectobackend.rest.api.juegosapp.dtos.venta;

public class VentaRequest {
    private Integer idUsuario;
    private int idJuego;

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public int getIdJuego() {
        return idJuego;
    }

    public void setIdJuego(int idJuego) {
        this.idJuego = idJuego;
    }
}
