package com.proyectobackend.rest.api.juegosapp.repositories;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ConfiguracionRepository {
    public BigDecimal obtenerComisionGlobal() {
        // Valor por defecto (Backup) por si la tabla está vacía
        BigDecimal valorPorDefecto = new BigDecimal("0.15");

        // Consulta directa a la columna específica
        String sql = "SELECT comision FROM configuracion_sistema LIMIT 1";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                BigDecimal valorBD = rs.getBigDecimal("comision_global");
                if (valorBD != null) {
                    return valorBD;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return valorPorDefecto;
    }
}
