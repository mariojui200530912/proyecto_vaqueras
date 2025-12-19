package com.proyectobackend.rest.api.juegosapp.dtos.billetera;

import java.math.BigDecimal;

public class RecargarRequest {
    private Integer idUsuario;
    private BigDecimal monto;

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }
}
