package com.proyectobackend.rest.api.juegosapp.repositories;

import com.proyectobackend.rest.api.juegosapp.dtos.venta.VentaResponse;

import java.math.BigDecimal;
import java.sql.*;

public class VentaRepository {

    // TRANSACCIÓN DE COMPRA (ACID)
    public boolean procesarCompra(int idUsuario, int idJuego, BigDecimal precio, BigDecimal porcentajeAplicado, BigDecimal comision, BigDecimal gananciaEmpresa) throws SQLException {
        String sqlSaldo = "UPDATE usuario SET cartera_saldo = cartera_saldo - ? WHERE id = ?";
        String sqlVenta = "INSERT INTO venta (id_usuario, id_juego, fecha_compra, precio, comision_aplicada, monto_plataforma, monto_empresa) VALUES (?, ?, NOW(), ?, ?, ?, ?)";
        String sqlTrans = "INSERT INTO transaccion (id_usuario, monto, tipo, fecha, id_venta) VALUES (?, ?, 'COMPRA', NOW(), ?)";
        String sqlLib = "INSERT INTO biblioteca (id_usuario, id_juego, fecha_adquisicion) VALUES (?, ?, CURRENT_DATE)";

        try (Connection conn = DBConnection.getInstance().getConnection()) {
            conn.setAutoCommit(false); // INICIO DE TRANSACCIÓN
            try (PreparedStatement psSaldo = conn.prepareStatement(sqlSaldo);
                 PreparedStatement psVenta = conn.prepareStatement(sqlVenta, Statement.RETURN_GENERATED_KEYS);
                 PreparedStatement psTrans = conn.prepareStatement(sqlTrans);
                 PreparedStatement psLib = conn.prepareStatement(sqlLib)) {
                // Descontar saldo al Usuario
                psSaldo.setBigDecimal(1, precio);
                psSaldo.setInt(2, idUsuario);
                if (psSaldo.executeUpdate() == 0) throw new SQLException("Error al descontar saldo.");

                // Registrar la Venta (Histórico)
                psVenta.setInt(1, idUsuario);
                psVenta.setInt(2, idJuego);
                psVenta.setBigDecimal(3, precio);
                psVenta.setBigDecimal(4, porcentajeAplicado);
                psVenta.setBigDecimal(5, comision);
                psVenta.setBigDecimal(6, gananciaEmpresa);
                psVenta.executeUpdate();

                int idVenta = 0;
                try (ResultSet rs = psVenta.getGeneratedKeys()) {
                    if (rs.next()) idVenta = rs.getInt(1);
                }

                // Registrar Transacción (Para el historial de billetera)
                psTrans.setInt(1, idUsuario);
                psTrans.setBigDecimal(2, precio); // Se registra como positivo, el tipo define si resta
                psTrans.setInt(3, idVenta);
                psTrans.executeUpdate();

                // Agregar a Biblioteca (Entregar el producto)
                psLib.setInt(1, idUsuario);
                psLib.setInt(2, idJuego);
                psLib.executeUpdate();

                conn.commit(); // ✅ CONFIRMAR CAMBIOS
                return true;
            } catch (Exception e) {
                if (conn != null) {
                    try {
                        System.err.println("ROLLBACK COMPRA: " + e.getMessage());
                        conn.rollback(); // ↩️ DESHACER TODO SI FALLA
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }
                throw e;
            }

        } catch (SQLException e) {
            throw e;
        }
    }
}
