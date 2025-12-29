/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.proyectobackend.rest.api.juegosapp.repositories;

import com.proyectobackend.rest.api.juegosapp.models.Usuario;
import com.proyectobackend.rest.api.juegosapp.models.UsuarioEmpresa;
import com.proyectobackend.rest.api.juegosapp.models.enums.EstadoUsuario;
import com.proyectobackend.rest.api.juegosapp.models.enums.Rol;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Hp
 */
public class UsuarioRepository {
    private static final String SQL_LIST = "SELECT * FROM usuario";
    private static final String SQL_FIND_BY_EMAIL = "SELECT * FROM usuario WHERE email = ?";
    private static final String SQL_FIND_BY_ID = "SELECT * FROM usuario WHERE id = ?";
    private static final String SQL_SAVE = "INSERT INTO usuario (nickname, password, email, fecha_nacimiento, telefono, pais, avatar, rol, estado, cartera_saldo, fecha_creacion) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String SQL_UPDATE = "UPDATE usuario SET nickname = ?, email = ?, password = ?, fecha_nacimiento = ?, telefono = ?, pais = ?, avatar = ?, rol = ?, estado = ? WHERE id = ?";
    private static final String SQL_UPDATE_PASSWORD = "UPDATE usuario SET password = ? WHERE id = ?";
    private static final String SQL_UPDATE_STATE = "UPDATE usuario SET estado = ? WHERE id = ?";
    private static final String SQL_DELETE = "DELETE FROM usuario WHERE id = ?";

    public List<Usuario> list() {
        List<Usuario> usuarios = new ArrayList<>();
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_LIST)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                usuarios.add(mapResultSetToUsuario(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar usuarios", e);
        }
        return usuarios;
    }

    public Usuario findByEmail(String email) {
        String sql = SQL_FIND_BY_EMAIL;
        try (Connection conn = DBConnection.getInstance().getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToUsuario(rs);
            }
            return null;

        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar por email: " + email, e);
        }
    }

    public Usuario findById(Integer id_usuario) {
        String sql = SQL_FIND_BY_ID;
        try (Connection conn = DBConnection.getInstance().getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id_usuario);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToUsuario(rs);
            }
            return null;

        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar por id: " + id_usuario, e);
        }
    }

    public List<Usuario> findByRol(Rol rol) {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT * FROM usuario WHERE rol = ?";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, rol.getValor());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                usuarios.add(mapResultSetToUsuario(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar usuarios por tipo", e);
        }

        return usuarios;
    }

    public Usuario save(Connection conn, Usuario usuario) {
        String sql = SQL_SAVE;
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, usuario.getNickname());
            stmt.setString(2, usuario.getPassword());
            stmt.setString(3, usuario.getEmail());
            stmt.setDate(4, Date.valueOf(usuario.getFechaNacimiento()));
            stmt.setString(5, usuario.getTelefono());
            stmt.setString(6, usuario.getPais());
            stmt.setBytes(7, usuario.getAvatar());
            stmt.setString(8, usuario.getRol().getValor());
            stmt.setString(9, usuario.getEstado().getValor());
            stmt.setBigDecimal(10, usuario.getCartera_saldo());
            stmt.setTimestamp(11, Timestamp.valueOf(usuario.getFechaCreacion()));

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creacion fallida, ninguna columna afectada.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    usuario.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creacion fallida, error al obtener id.");
                }
            }

            return usuario;

        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar usuario", e);
        }
    }

    public Usuario update(Usuario usuario) {
        String sql = SQL_UPDATE;
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, usuario.getNickname());
            stmt.setString(2, usuario.getPassword());
            stmt.setString(3, usuario.getEmail());
            stmt.setDate(4, Date.valueOf(usuario.getFechaNacimiento()));
            stmt.setString(5, usuario.getTelefono());
            stmt.setString(6, usuario.getPais());
            stmt.setBytes(7, usuario.getAvatar());
            stmt.setString(8, usuario.getRol().getValor());
            stmt.setString(9, usuario.getEstado().getValor());
            stmt.setInt(10, usuario.getId());

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar usuario: " + usuario.getNickname() + e.getMessage(), e);
        }
        return usuario;
    }

    public void updatePassword(Integer userId, String newPasswordHash) {
        String sql = SQL_UPDATE_PASSWORD;
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, newPasswordHash);
            stmt.setLong(2, userId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error updating password for user: " + userId, e);
        }
    }

    public boolean updateEstado(Integer id, String nuevoEstado) throws Exception {
        String sql = SQL_UPDATE_STATE;
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, nuevoEstado); // "BLOQUEADO" o "ACTIVO"
            ps.setInt(2, id);

            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            throw new Exception("Error al actualizar estado del usuario: " + e.getMessage());
        }
    }

    public void delete(Integer id_usuario) {
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_DELETE)) {
            stmt.setInt(1, id_usuario);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("No se pudo eliminar el usuario, ID no encontrado: " + id_usuario);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting user with ID: " + id_usuario, e);
        }
    }

    public Integer obtenerIdEmpresaPorUsuario(Integer id_usuario){
        String sql = "SELECT * FROM usuario_empresa WHERE id_usuario = ?";
        try(Connection conn = DBConnection.getInstance().getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setInt(1, id_usuario);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("id_empresa");
            }
            return null;

        }catch (SQLException e){
            throw new RuntimeException("Error al obtener ID Empresa por ID de usuario: " + id_usuario, e);
        }
    }

    public UsuarioEmpresa obtenerUsuarioEmpresaPorUsuario(Integer id_usuario){
        String sql = "SELECT * FROM usuario_empresa WHERE id_usuario = ?";
        UsuarioEmpresa usuarioEmpresa = new UsuarioEmpresa();
        try(Connection conn = DBConnection.getInstance().getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setInt(1, id_usuario);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                usuarioEmpresa.setId(rs.getInt("id"));
                usuarioEmpresa.setId_usuario(rs.getInt("id_usuario"));
                usuarioEmpresa.setId_empresa(rs.getInt("id_empresa"));
                usuarioEmpresa.setRol_empresa(rs.getString("rol_empresa"));
                return usuarioEmpresa;
            }
            return null;
        }catch (SQLException e){
            throw new RuntimeException("Error al obtener Usuario Empresa por ID de usuario: " + id_usuario, e);
        }
    }

    public void cambiarRol(Connection conn, Integer id_usuario, Rol rol) {
        String sql = "UPDATE usuario SET rol = ? WHERE id = ?";
        try(PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setString(1, rol.getValor());
            ps.setInt(2, id_usuario);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al cambiar rol del usuario: " + e.getMessage());
        }
    }

    public Usuario mapResultSetToUsuario(ResultSet rs) throws SQLException {
        Usuario usuario = new Usuario();
        usuario.setId(rs.getInt("id"));
        usuario.setNickname(rs.getString("nickname"));
        usuario.setPassword(rs.getString("password"));
        usuario.setEmail(rs.getString("email"));
        usuario.setFechaNacimiento(rs.getDate("fecha_nacimiento").toLocalDate());
        usuario.setTelefono(rs.getString("telefono"));
        usuario.setPais(rs.getString("pais"));
        usuario.setAvatar(rs.getBytes("avatar"));
        usuario.setRol(Rol.valueOf(rs.getString("rol")));
        usuario.setEstado(EstadoUsuario.valueOf(rs.getString("estado")));
        usuario.setCartera_saldo(rs.getBigDecimal("cartera_saldo"));
        usuario.setFechaCreacion(rs.getTimestamp("fecha_creacion").toLocalDateTime());

        return usuario;
    }

}


