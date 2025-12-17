package com.proyectobackend.rest.api.juegosapp.repositories;

import com.proyectobackend.rest.api.juegosapp.models.Empresa;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EmpresaRepository {
    public Empresa crear(Empresa empresa) throws SQLException {
        String sql = "INSERT INTO empresa (nombre, descripcion, logo, comision_especifica, permite_comentarios, fecha_creacion) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, empresa.getNombre());
            ps.setString(2, empresa.getDescripcion());
            ps.setString(3, empresa.getLogo());

            // Manejo de Decimal Nulo
            if (empresa.getComisionEspecifica() != null) {
                ps.setBigDecimal(4, empresa.getComisionEspecifica());
            } else {
                ps.setNull(4, Types.DECIMAL);
            }

            ps.setBoolean(5, empresa.getPermiteComentarios());
            ps.setTimestamp(6, Timestamp.valueOf(empresa.getFecha_creacion()));

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    empresa.setId(rs.getInt(1));
                }
            }
        }
        return empresa;
    }

    public List<Empresa> listar() throws SQLException {
        List<Empresa> lista = new ArrayList<>();
        String sql = "SELECT * FROM empresa";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearEmpresa(rs));
            }
        }
        return lista;
    }

    public boolean actualizar(Empresa empresa) throws SQLException {
        String sql = "UPDATE empresa SET nombre=?, descripcion=?, logo=?, comision_especifica=?, permite_comentarios=? WHERE id=?";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, empresa.getNombre());
            ps.setString(2, empresa.getDescripcion());
            ps.setString(3, empresa.getLogo());

            if (empresa.getComisionEspecifica() != null) {
                ps.setBigDecimal(4, empresa.getComisionEspecifica());
            } else {
                ps.setNull(4, Types.DECIMAL);
            }

            ps.setBoolean(5, empresa.getPermiteComentarios());
            ps.setInt(6, empresa.getId());

            return ps.executeUpdate() > 0;
        }
    }

    public boolean eliminar(int id) throws SQLException {
        String sql = "DELETE FROM empresa WHERE id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    public Empresa buscarPorNombre(String nombre) throws SQLException {
        String sql = "SELECT * FROM empresa WHERE nombre = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nombre);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearEmpresa(rs);
                }
            }
        }
        return null;
    }

    public Optional<Empresa> buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM empresa WHERE id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearEmpresa(rs));
                }
            }
        }
        return Optional.empty();
    }

    private Empresa mapearEmpresa(ResultSet rs) throws SQLException {
        Empresa e = new Empresa();
        e.setId(rs.getInt("id"));
        e.setNombre(rs.getString("nombre"));
        e.setDescripcion(rs.getString("descripcion"));
        e.setLogo(rs.getString("logo"));
        e.setComisionEspecifica(rs.getBigDecimal("comision_especifica"));
        e.setPermiteComentarios(rs.getBoolean("permite_comentarios"));
        if (rs.getTimestamp("fecha_creacion") != null) {
            e.setFecha_creacion(rs.getTimestamp("fecha_creacion").toLocalDateTime());
        }
        return e;
    }
}
