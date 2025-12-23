package com.proyectobackend.rest.api.juegosapp.repositories;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PrestamoRepository {
    // CREAR EL REGISTRO DE PRÉSTAMO
    public void crearPrestamo(Connection conn, int idBeneficiario, int idJuego, int idDueno) throws SQLException {
        String sql = "INSERT INTO prestamo (id_beneficiario, id_juego, id_dueno, estado, fecha) " +
                "VALUES (?, ?, ?, 'NO_INSTALADO', NOW())";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idBeneficiario);
            ps.setInt(2, idJuego);
            ps.setInt(3, idDueno);
            ps.executeUpdate();
        }
    }

    // ACTUALIZAR ESTADO (Cuando el usuario lo instala)
    public void actualizarEstado(Connection conn, int idPrestamo, String nuevoEstado) throws SQLException {
        String sql = "UPDATE prestamo SET estado = ? WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nuevoEstado); // 'INSTALADO'
            ps.setInt(2, idPrestamo);
            ps.executeUpdate();
        }
    }

    // FINALIZAR PRÉSTAMO (Borrar registro o mover a histórico)
    // Para este ejemplo, al devolver el juego, liberamos el registro borrándolo.
    public void eliminarPrestamo(Connection conn, int idPrestamo, int idBeneficiario) throws SQLException {
        String sql = "DELETE FROM prestamo WHERE id = ? AND id_beneficiario = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idPrestamo);
            ps.setInt(2, idBeneficiario);
            ps.executeUpdate();
        }
    }

    // VALIDACIONES DE REGLAS DE NEGOCIO (SQL)
    // Validar: ¿Beneficiario y Dueño comparten al menos UN grupo familiar?
    public boolean estanEnMismoGrupo(Connection conn, int idUsuarioA, int idUsuarioB) throws SQLException {
        String sql = "SELECT 1 FROM grupo_usuario gu1 " +
                "INNER JOIN grupo_usuario gu2 ON gu1.id_grupo = gu2.id_grupo " +
                "WHERE gu1.id_usuario = ? AND gu2.id_usuario = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUsuarioA);
            ps.setInt(2, idUsuarioB);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public boolean tieneJuegoInstalado(Connection conn, int idBeneficiario) throws SQLException {
        String sql = "SELECT id FROM prestamo WHERE id_beneficiario = ? AND estado = 'INSTALADO'";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idBeneficiario);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next(); // Si encuentra algo, es true
            }
        }
    }

    // Validar: ¿El juego ya está prestado a alguien más? (Regla de copia única)
    public boolean juegoEstaPrestado(Connection conn, int idJuego, int idDueno) throws SQLException {
        String sql = "SELECT id FROM prestamo WHERE id_juego = ? AND id_dueno = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idJuego);
            ps.setInt(2, idDueno);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    // Validar: ¿El beneficiario ya tiene ese préstamo activo?
    public boolean yaLoTienePrestado(Connection conn, int idBeneficiario, int idJuego) throws SQLException {
        String sql = "SELECT id FROM prestamo WHERE id_beneficiario = ? AND id_juego = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idBeneficiario);
            ps.setInt(2, idJuego);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }
}
