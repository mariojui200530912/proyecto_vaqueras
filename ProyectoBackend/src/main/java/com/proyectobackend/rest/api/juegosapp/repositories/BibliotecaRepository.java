package com.proyectobackend.rest.api.juegosapp.repositories;

import com.proyectobackend.rest.api.juegosapp.dtos.juego.JuegoResponse;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class BibliotecaRepository {

    public boolean usuarioTieneJuego(int idUsuario, int idJuego) throws SQLException {
        String sql = "SELECT COUNT(*) FROM biblioteca WHERE id_usuario = ? AND id_juego = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            ps.setInt(2, idJuego);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        }
        return false;
    }

    public List<JuegoResponse> obtenerBibliotecaUsuario(int idUsuario) throws SQLException {
        List<JuegoResponse> lista = new ArrayList<>();

        // JOIN Triple: Juego -> Biblioteca -> Imagen (Solo Portada)
        String sql = "SELECT j.id, j.titulo, j.descripcion, j.clasificacion, img.imagen as portada_blob " +
                "FROM juego j " +
                "INNER JOIN biblioteca b ON j.id = b.id_juego " +
                "LEFT JOIN imagen_juego img ON j.id = img.id_juego AND img.atributo = 'PORTADA' " +
                "WHERE b.id_usuario = ?";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idUsuario);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    JuegoResponse jr = new JuegoResponse();
                    jr.setId(rs.getInt("id"));
                    jr.setTitulo(rs.getString("titulo"));
                    jr.setDescripcion(rs.getString("descripcion"));
                    jr.setClasificacion(rs.getString("clasificacion"));

                    // Conversión directa a Base64 para el Frontend
                    byte[] bytes = rs.getBytes("portada_blob");
                    if (bytes != null && bytes.length > 0) {
                        jr.setPortada(Base64.getEncoder().encodeToString(bytes));
                    }

                    lista.add(jr);
                }
            }
        }
        return lista;
    }
}
