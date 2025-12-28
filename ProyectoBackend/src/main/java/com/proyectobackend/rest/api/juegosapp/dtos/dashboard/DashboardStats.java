package com.proyectobackend.rest.api.juegosapp.dtos.dashboard;

import java.math.BigDecimal;

public class DashboardStats {
    private int totalUsuarios;
    private int totalJuegos;
    private BigDecimal totalGanancias;
    private int ventasMesActual;

    public int getTotalUsuarios() {
        return totalUsuarios;
    }

    public void setTotalUsuarios(int totalUsuarios) {
        this.totalUsuarios = totalUsuarios;
    }

    public int getTotalJuegos() {
        return totalJuegos;
    }

    public void setTotalJuegos(int totalJuegos) {
        this.totalJuegos = totalJuegos;
    }

    public BigDecimal getTotalGanancias() {
        return totalGanancias;
    }

    public void setTotalGanancias(BigDecimal totalGanancias) {
        this.totalGanancias = totalGanancias;
    }

    public int getVentasMesActual() {
        return ventasMesActual;
    }

    public void setVentasMesActual(int ventasMesActual) {
        this.ventasMesActual = ventasMesActual;
    }
}
