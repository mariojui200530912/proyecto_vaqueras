package com.proyectobackend.rest.api.juegosapp.repositories;

import com.proyectobackend.rest.api.juegosapp.dtos.grupo.GrupoResponse;
import com.proyectobackend.rest.api.juegosapp.dtos.grupo.JuegoGrupoResponse;
import com.proyectobackend.rest.api.juegosapp.dtos.prestamo.PrestamoResponse;
import com.proyectobackend.rest.api.juegosapp.dtos.usuario.UsuarioResponse;

import java.sql.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class GrupoRepository {
    // CREAR GRUPO
    public int crear(Connection conn, int idCreador, String nombre) throws SQLException {
        String sql = "INSERT INTO grupo_familiar (nombre, id_creador) VALUES (?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, nombre);
            ps.setInt(2, idCreador);
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        throw new SQLException("Error al obtener ID del grupo.");
    }

    // BUSCAR GRUPO POR ID
    public GrupoResponse buscarPorId(Connection conn, int idGrupo) throws SQLException {
        String sql = "SELECT * FROM grupo_familiar WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idGrupo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    GrupoResponse g = new GrupoResponse();
                    g.setId(rs.getInt("id"));
                    g.setNombre(rs.getString("nombre"));
                    g.setIdCreador(rs.getInt("id_creador"));
                    return g;
                }
            }
        }
        return null;
    }

    // AGREGAR MIEMBRO
    public void agregarMiembro(Connection conn, int idGrupo, int idUsuario) throws SQLException {
        String sql = "INSERT INTO grupo_usuario (id_grupo, id_usuario) VALUES (?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idGrupo);
            ps.setInt(2, idUsuario);
            ps.executeUpdate();
        }
    }

    // ELIMINAR MIEMBRO
    public void eliminarMiembro(Connection conn, int idGrupo, int idUsuario) throws SQLException {
        String sql = "DELETE FROM grupo_usuario WHERE id_grupo = ? AND id_usuario = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idGrupo);
            ps.setInt(2, idUsuario);
            ps.executeUpdate();
        }
    }

    // ELIMINAR GRUPO COMPLETO
    public void eliminarGrupo(Connection conn, int idGrupo) throws SQLException {
        String sql = "DELETE FROM grupo_familiar WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idGrupo);
            ps.executeUpdate();
        }
    }

    // LISTAR MIEMBROS DEL GRUPO
    public List<UsuarioResponse> obtenerMiembros(Connection conn, int idGrupo) throws SQLException {
        List<UsuarioResponse> lista = new ArrayList<>();
        String sql = "SELECT u.id, u.nickname, u.email " +
                "FROM grupo_usuario gu " +
                "INNER JOIN usuario u ON gu.id_usuario = u.id " +
                "WHERE gu.id_grupo = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idGrupo);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    UsuarioResponse u = new UsuarioResponse();
                    u.setId(rs.getInt("id"));
                    u.setNickname(rs.getString("nickname"));
                    u.setEmail(rs.getString("email"));
                    lista.add(u);
                }
            }
        }
        return lista;
    }

    // Validación: Verificar si ya está en el grupo
    public boolean esMiembro(Connection conn, int idGrupo, int idUsuario) throws SQLException {
        String sql = "SELECT id FROM grupo_usuario WHERE id_grupo = ? AND id_usuario = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idGrupo);
            ps.setInt(2, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public Integer obtenerIdGrupoDeUsuario(Connection conn, int idUsuario) throws SQLException {
        // Revisar si es miembro (tabla grupo_usuario)
        String sqlMiembro = "SELECT id_grupo FROM grupo_usuario WHERE id_usuario = ?";
        try (PreparedStatement ps = conn.prepareStatement(sqlMiembro)) {
            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("id_grupo");
            }
        }
        return null; // No tiene grupo
    }

    public List<JuegoGrupoResponse> obtenerJuegosDelGrupo(Connection conn, int idGrupo, int idSolicitante) throws SQLException {
        List<JuegoGrupoResponse> lista = new ArrayList<>();

        String sql = "SELECT j.id AS id_juego, j.titulo, " +
                "u.id AS id_dueno, u.nickname, u.avatar, " +
                "img.imagen AS portada_blob " +
                "FROM grupo_usuario gu " +
                "JOIN biblioteca b ON gu.id_usuario = b.id_usuario " +
                "JOIN juego j ON b.id_juego = j.id " +
                "JOIN usuario u ON b.id_usuario = u.id " +
                "LEFT JOIN imagen_juego img ON j.id = img.id_juego AND img.atributo = 'PORTADA' " +
                "WHERE gu.id_grupo = ? " +
                "AND b.id_usuario != ?"; // Excluir al que consulta

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idGrupo);
            ps.setInt(2, idSolicitante);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    JuegoGrupoResponse dto = new JuegoGrupoResponse();
                    dto.setIdJuego(rs.getInt("id_juego"));
                    dto.setTitulo(rs.getString("titulo"));
                    dto.setIdDueno(rs.getInt("id_dueno"));
                    dto.setNombreDueno(rs.getString("nickname"));

                    // Imagen Portada
                    byte[] imgBytes = rs.getBytes("portada_blob");
                    if (imgBytes != null) dto.setPortada("data:image/jpeg;base64," + Base64.getEncoder().encodeToString(imgBytes));

                    // Avatar Dueño (Opcional)
                    byte[] avatarBytes = rs.getBytes("avatar");
                    if (avatarBytes != null) dto.setAvatarDueno("data:image/jpeg;base64," + Base64.getEncoder().encodeToString(avatarBytes));

                    lista.add(dto);
                }
            }
        }
        return lista;
    }
}
