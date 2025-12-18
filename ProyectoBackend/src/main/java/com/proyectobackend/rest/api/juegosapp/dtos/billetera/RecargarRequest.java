package com.proyectobackend.rest.api.juegosapp.dtos.billetera;

import java.math.BigDecimal;

public class RecargarRequest {
    private BigDecimal monto;

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }
}
