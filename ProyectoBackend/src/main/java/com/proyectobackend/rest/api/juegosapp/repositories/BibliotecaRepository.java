package com.proyectobackend.rest.api.juegosapp.repositories;

import com.proyectobackend.rest.api.juegosapp.dtos.biblioteca.BibliotecaResponse;
import com.proyectobackend.rest.api.juegosapp.dtos.juego.JuegoResponse;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class BibliotecaRepository {

    public boolean usuarioTieneJuego(Connection conn, int idUsuario, int idJuego) throws SQLException {
        String sql = "SELECT COUNT(*) FROM biblioteca WHERE id_usuario = ? AND id_juego = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            ps.setInt(2, idJuego);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        }
        return false;
    }

    public List<BibliotecaResponse> obtenerBibliotecaUsuario(int idUsuario) throws SQLException {
        List<BibliotecaResponse> lista = new ArrayList<>();

        // JOIN Triple: Juego -> Biblioteca -> Imagen (Solo Portada)
        String sql = "SELECT j.id, j.titulo, j.descripcion, j.clasificacion, img.imagen as portada_blob, b.id as id_biblioteca, b.fecha_adquisicion, b.jugado_horas, b.estado " +
                "FROM juego j " +
                "INNER JOIN biblioteca b ON j.id = b.id_juego " +
                "LEFT JOIN imagen_juego img ON j.id = img.id_juego AND img.atributo = 'PORTADA' " +
                "WHERE b.id_usuario = ?";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idUsuario);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    BibliotecaResponse br = new BibliotecaResponse();
                    br.setIdBiblioteca(rs.getInt("id_biblioteca"));
                    br.setIdJuego(rs.getInt("id"));
                    br.setTitulo(rs.getString("titulo"));
                    br.setDescripcion(rs.getString("descripcion"));
                    br.setClasificacion(rs.getString("clasificacion"));
                    br.setFechaAdquisicion(rs.getTimestamp("fecha_adquisicion").toLocalDateTime());
                    br.setJugadoHoras(rs.getInt("jugado_horas"));
                    br.setEstado(rs.getString("estado"));

                    // Conversión directa a Base64 para el Frontend
                    byte[] bytes = rs.getBytes("portada_blob");
                    if (bytes != null && bytes.length > 0) {
                        br.setPortada("data:image/jpeg;base64," + Base64.getEncoder().encodeToString(bytes));
                    }

                    lista.add(br);
                }
            }
        }
        return lista;
    }

    public boolean esBibliotecaPublica(int idUsuario) throws SQLException {
        String sql = "SELECT biblioteca_publica FROM usuario WHERE id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getBoolean("biblioteca_publica");
                }
            }
        }
        return false; // Por defecto cerrada si no existe usuario
    }

    public boolean instalarJuego(int idUsuario, int idJuego) throws SQLException {
        String sql = "UPDATE biblioteca SET estado = 'INSTALADO' WHERE id_usuario = ? AND id_juego = ?";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idUsuario);
            ps.setInt(2, idJuego);

            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0;
        }
    }

    public boolean desinstalarJuego(int idUsuario, int idJuego) throws SQLException {
        String sql = "UPDATE biblioteca SET estado = 'NO INSTALADO' WHERE id_usuario = ? AND id_juego = ?";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idUsuario);
            ps.setInt(2, idJuego);

            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0;
        }
    }

    public String obtenerEstadoJuego(int idUsuario, int idJuego) throws SQLException {
        String sql = "SELECT estado FROM biblioteca WHERE id_usuario = ? AND id_juego = ?";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idUsuario);
            ps.setInt(2, idJuego);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("estado");
                }
            }
        }
        return null; // Retorna null si no tiene el juego
    }

    public void actualizarVisibilidadBiblioteca(Connection conn, int idUsuario, boolean esPublica) throws SQLException {
        String sql = "UPDATE usuario SET biblioteca_publica = ? WHERE id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBoolean(1, esPublica);
            ps.setInt(2, idUsuario);
            ps.executeUpdate();
        }
    }
}
