package com.proyectobackend.rest.api.juegosapp.dtos.prestamo;

public class PrestamoRequest {
    private Integer idJuego;
    private Integer idDueno;

    public Integer getIdJuego() {
        return idJuego;
    }

    public void setIdJuego(Integer idJuego) {
        this.idJuego = idJuego;
    }

    public Integer getIdDueno() {
        return idDueno;
    }

    public void setIdDueno(Integer idDueno) {
        this.idDueno = idDueno;
    }
}
