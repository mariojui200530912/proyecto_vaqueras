package com.proyectobackend.rest.api.juegosapp.repositories;

import com.proyectobackend.rest.api.juegosapp.models.Transaccion;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TransaccionRepository {
    public boolean realizarRecarga(int idUsuario, BigDecimal monto) throws SQLException {
        Connection conn = DBConnection.getInstance().getConnection();
        String sqlUpdate = "UPDATE usuario SET cartera_saldo = cartera_saldo + ? WHERE id = ?";
        String sqlInsert = "INSERT INTO transaccion (id_usuario, monto, tipo, fecha) VALUES (?, ?, 'RECARGA', NOW())";

        try (PreparedStatement psUpdate = conn.prepareStatement(sqlUpdate);
             PreparedStatement psInsert = conn.prepareStatement(sqlInsert)) {

            conn.setAutoCommit(false); // INICIO TRANSACCIÓN

            // 1. Actualizar saldo del usuario (Sumar)
            psUpdate.setBigDecimal(1, monto);
            psUpdate.setInt(2, idUsuario);
            int filasUsuario = psUpdate.executeUpdate();

            if (filasUsuario == 0) {
                throw new SQLException("Usuario no encontrado, no se pudo recargar.");
            }

            // 2. Insertar registro en Transacción
            psInsert.setInt(1, idUsuario);
            psInsert.setBigDecimal(2, monto);
            psInsert.executeUpdate();

            conn.commit(); // CONFIRMAR CAMBIOS
            return true;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    System.err.println("Rollback en recarga: " + e.getMessage());
                    conn.rollback(); // DESHACER CAMBIOS
                } catch (SQLException ex) { ex.printStackTrace(); }
            }
            throw e;
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }

    public List<Transaccion> listarPorUsuario(int idUsuario) throws SQLException {
        List<Transaccion> lista = new ArrayList<>();
        String sql = "SELECT * FROM transaccion WHERE id_usuario = ? ORDER BY fecha DESC";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Transaccion t = new Transaccion();
                    t.setId(rs.getInt("id"));
                    t.setIdUsuario(rs.getInt("id_usuario"));
                    t.setMonto(rs.getBigDecimal("monto"));
                    t.setTipo(rs.getString("tipo"));
                    t.setFecha(rs.getTimestamp("fecha").toLocalDateTime());

                    int idVenta = rs.getInt("id_venta");
                    if (!rs.wasNull()) {
                        t.setIdVenta(idVenta);
                    }
                    lista.add(t);
                }
            }
        }
        return lista;
    }

    // Metodo para devolver saldo actual
    public BigDecimal obtenerSaldoActual(int idUsuario) throws SQLException {
        String sql = "SELECT cartera_saldo FROM usuario WHERE id = ?";
        try (Connection con = DBConnection.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getBigDecimal("cartera_saldo");
            }
        }
        return BigDecimal.ZERO;
    }
}
