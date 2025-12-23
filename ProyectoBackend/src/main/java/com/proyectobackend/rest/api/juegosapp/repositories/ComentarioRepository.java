package com.proyectobackend.rest.api.juegosapp.repositories;

import com.proyectobackend.rest.api.juegosapp.dtos.comentario.ComentarioResponse;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ComentarioRepository {
    public void guardarComentario(Connection conn, int idUsuario, int idJuego, Integer idPadre, String texto) throws SQLException {
        String sql = "INSERT INTO comentario (id_usuario, id_juego, id_comentario_padre, comentario, estado_comentario) VALUES (?, ?, ?, ?, 'VISIBLE')";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            ps.setInt(2, idJuego);

            // Manejo de nulo para el padre
            if (idPadre != null) {
                ps.setInt(3, idPadre);
            } else {
                ps.setNull(3, java.sql.Types.INTEGER);
            }

            ps.setString(4, texto);
            ps.executeUpdate();
        }
    }

    public List<ComentarioResponse> obtenerComentariosPlanos(Connection conn, int idJuego) throws SQLException {
        List<ComentarioResponse> lista = new ArrayList<>();

        // Hacemos JOIN con Usuario para mostrar quién escribió
        String sql = "SELECT c.id, c.comentario, c.fecha, c.id_comentario_padre, u.nickname " +
                "FROM comentario c " +
                "INNER JOIN usuario u ON c.id_usuario = u.id " +
                "WHERE c.id_juego = ? AND c.estado_comentario = 'VISIBLE' " +
                "ORDER BY c.fecha ASC";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idJuego);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ComentarioResponse c = new ComentarioResponse();
                    c.setId(rs.getInt("id"));
                    c.setComentario(rs.getString("comentario"));
                    c.setFecha(rs.getString("fecha")); // O LocalDateTime
                    c.setUsuarioNickname(rs.getString("nickname"));

                    int idPadre = rs.getInt("id_comentario_padre");
                    if (!rs.wasNull()) {
                        c.setIdPadre(idPadre);
                    }
                    lista.add(c);
                }
            }
        }
        return lista;
    }

    public void cambiarEstadoComentario(Connection conn, int idComentario, String nuevoEstado) throws SQLException {
        String sql = "UPDATE comentario SET estado_comentario = ? WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nuevoEstado); // 'VISIBLE', 'OCULTO', 'ELIMINADO'
            ps.setInt(2, idComentario);
            ps.executeUpdate();
        }
    }
}
