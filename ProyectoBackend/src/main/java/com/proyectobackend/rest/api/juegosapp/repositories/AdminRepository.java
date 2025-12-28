package com.proyectobackend.rest.api.juegosapp.repositories;

import com.proyectobackend.rest.api.juegosapp.dtos.dashboard.DashboardStats;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminRepository {
    public DashboardStats obtenerEstadisticas(Connection conn) throws SQLException {
        DashboardStats stats = new DashboardStats();

        // Consultas individuales o subconsultas
        String sql = "SELECT " +
                "(SELECT COUNT(*) FROM usuario WHERE rol = 'GAMER') as users, " +
                "(SELECT COUNT(*) FROM juego WHERE estado_venta = 'ACTIVO') as juegos, " +
                "(SELECT COALESCE(SUM(monto_empresa), 0) FROM venta) as ganancias, " +
                "(SELECT COUNT(*) FROM venta WHERE MONTH(fecha_compra) = MONTH(CURRENT_DATE())) as ventas_mes";

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                stats.setTotalUsuarios(rs.getInt("users"));
                stats.setTotalJuegos(rs.getInt("juegos"));
                stats.setTotalGanancias(rs.getBigDecimal("ganancias"));
                stats.setVentasMesActual(rs.getInt("ventas_mes"));
            }
        }
        return stats;
    }
}
