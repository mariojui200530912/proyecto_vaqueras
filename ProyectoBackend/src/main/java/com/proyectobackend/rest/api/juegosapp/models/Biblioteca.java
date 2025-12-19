package com.proyectobackend.rest.api.juegosapp.models;

import java.time.LocalDate;

public class Biblioteca {
    private Integer idBiblioteca;
    private Integer idUsuario;
    private Integer idJuego;
    private LocalDate fechaAdquisicion;
    private Integer jugadoHoras;

    public Integer getIdBiblioteca() {
        return idBiblioteca;
    }

    public void setIdBiblioteca(Integer idBiblioteca) {
        this.idBiblioteca = idBiblioteca;
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public Integer getIdJuego() {
        return idJuego;
    }

    public void setIdJuego(Integer idJuego) {
        this.idJuego = idJuego;
    }

    public LocalDate getFechaAdquisicion() {
        return fechaAdquisicion;
    }

    public void setFechaAdquisicion(LocalDate fechaAdquisicion) {
        this.fechaAdquisicion = fechaAdquisicion;
    }

    public Integer getJugadoHoras() {
        return jugadoHoras;
    }

    public void setJugadoHoras(Integer jugadoHoras) {
        this.jugadoHoras = jugadoHoras;
    }
}
