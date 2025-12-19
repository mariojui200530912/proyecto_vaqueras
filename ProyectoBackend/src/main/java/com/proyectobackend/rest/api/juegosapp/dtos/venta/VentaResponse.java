package com.proyectobackend.rest.api.juegosapp.dtos.venta;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class VentaResponse {
    private int idVenta;
    private String tituloJuego;
    private BigDecimal montoPagado;
    private BigDecimal saldoRestante;
    private LocalDateTime fecha;

    public int getIdVenta() {
        return idVenta;
    }

    public void setIdVenta(int idVenta) {
        this.idVenta = idVenta;
    }

    public String getTituloJuego() {
        return tituloJuego;
    }

    public void setTituloJuego(String tituloJuego) {
        this.tituloJuego = tituloJuego;
    }

    public BigDecimal getMontoPagado() {
        return montoPagado;
    }

    public void setMontoPagado(BigDecimal montoPagado) {
        this.montoPagado = montoPagado;
    }

    public BigDecimal getSaldoRestante() {
        return saldoRestante;
    }

    public void setSaldoRestante(BigDecimal saldoRestante) {
        this.saldoRestante = saldoRestante;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }
}
