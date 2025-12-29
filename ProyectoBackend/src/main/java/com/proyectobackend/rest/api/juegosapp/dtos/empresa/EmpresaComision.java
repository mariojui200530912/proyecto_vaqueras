package com.proyectobackend.rest.api.juegosapp.dtos.empresa;

import java.math.BigDecimal;

public class EmpresaComision {
    private BigDecimal comision;

    public BigDecimal getComision() {
        return comision;
    }

    public void setComision(BigDecimal comision) {
        this.comision = comision;
    }
}
