package com.proyectobackend.rest.api.juegosapp.repositories;

import com.proyectobackend.rest.api.juegosapp.dtos.configuracion.Comision;
import net.sf.jasperreports.engine.util.JRStyledText;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ConfiguracionRepository {
    public BigDecimal obtenerComisionGlobal() {
        // Valor por defecto (Backup) por si la tabla está vacía
        BigDecimal valorPorDefecto = new BigDecimal("15.00");

        // Consulta directa a la columna específica
        String sql = "SELECT comision_global FROM configuracion_sistema LIMIT 1";

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

    public void actualizarValorGlobal(Connection conn, BigDecimal comision) throws SQLException {
        String sql = "UPDATE configuracion_sistema SET comision_global = ? WHERE id = 1";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBigDecimal(1, comision);
            int filas = ps.executeUpdate();

            if (filas == 0) {
                // Opcional: Si no existe la fila, la insertamos
                insertarValorGlobal(conn, comision);
            }
        }
    }

    private void insertarValorGlobal(Connection conn, BigDecimal comision) throws SQLException {
        String sql = "INSERT INTO configuracion_sistema (comision_global) VALUES (?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBigDecimal(1, comision);
            ps.executeUpdate();
        }
    }

    // MAGIA SQL: "Baja la comisión a TODAS las empresas que la tengan más alta que el nuevo límite"
    public int recortarComisionesExcedidas(Connection conn, BigDecimal topeMaximo) throws SQLException {
        // Traducido: Poner la comisión = tope, DONDE la comisión actual sea > tope
        String sql = "UPDATE empresa SET comision_especifica = ? WHERE comision_especifica > ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBigDecimal(1, topeMaximo);
            ps.setBigDecimal(2, topeMaximo);

            return ps.executeUpdate(); // Retorna cuántas empresas fueron ajustadas
        }
    }
}
