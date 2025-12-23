package com.proyectobackend.rest.api.juegosapp.repositories;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CalificacionRepository {
    public void guardarCalificacion(Connection conn, int idUsuario, int idJuego, double puntaje) throws SQLException {
        String sql = "INSERT INTO calificacion (id_usuario, id_juego, calificacion, fecha) VALUES (?, ?, ?, NOW()) " +
                "ON DUPLICATE KEY UPDATE calificacion = VALUES(calificacion), fecha = NOW()";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            ps.setInt(2, idJuego);
            ps.setDouble(3, puntaje);
            ps.executeUpdate();
        }
    }

    public BigDecimal obtenerPromedioJuego(Connection conn, int idJuego) throws SQLException {
        String sql = "SELECT AVG(calificacion) as promedio FROM calificacion WHERE id_juego = ?";
        try(PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idJuego);
            try(ResultSet rs = ps.executeQuery()) {
                if(rs.next()) return rs.getBigDecimal("promedio");
            }
        }
        return BigDecimal.ZERO;
    }
}
