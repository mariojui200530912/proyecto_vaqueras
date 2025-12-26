package com.proyectobackend.rest.api.juegosapp.repositories;

import com.proyectobackend.rest.api.juegosapp.dtos.BannerResponse;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class BannerRepository {
    // Banner Configurado Manualmente
    public List<BannerResponse> obtenerBannerConfigurado(Connection conn) throws SQLException {
        List<BannerResponse> lista = new ArrayList<>();

        // Filtramos: Solo juegos activos y ordenados por 'orden'
        String sql = "SELECT j.id, j.titulo, j.descripcion, i.imagen " +
                "FROM banner b " +
                "INNER JOIN juego j ON b.id_juego = j.id " +
                "LEFT JOIN imagen_juego i ON j.id = i.id_juego AND i.atributo = 'BANNER' " +
                "WHERE b.estado = TRUE AND j.estado_venta = 'ACTIVO' " +
                "ORDER BY b.orden ASC";

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearResultSet(rs));
            }
        }
        return lista;
    }

    // Respaldo (Últimos lanzamientos)
    public List<BannerResponse> obtenerUltimosLanzamientos(Connection conn, int limite) throws SQLException {
        List<BannerResponse> lista = new ArrayList<>();

        // Buscamos los juegos más nuevos que tengan imagen
        String sql = "SELECT j.id, j.titulo, j.descripcion, i.imagen " +
                "FROM juego j " +
                "LEFT JOIN imagen_juego i ON j.id = i.id_juego AND i.atributo = 'PORTADA' " +
                "WHERE j.estado_venta = 'ACTIVO' " +
                "ORDER BY j.fecha_lanzamiento DESC " +
                "LIMIT ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limite);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearResultSet(rs));
                }
            }
        }
        return lista;
    }

    // AGREGAR AL BANNER
    public void agregarConfiguracion(Connection conn, int idJuego, int orden) throws SQLException {
        String sql = "INSERT INTO banner (id_juego, orden, estado) VALUES (?, ?, TRUE)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idJuego);
            ps.setInt(2, orden);
            ps.executeUpdate();
        }
    }

    // ELIMINAR DEL BANNER
    public void eliminarConfiguracion(Connection conn, int idJuego) throws SQLException {
        String sql = "DELETE FROM banner WHERE id_juego = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idJuego);
            int filas = ps.executeUpdate();
            if (filas == 0) {
                throw new SQLException("El juego no estaba en el banner.");
            }
        }
    }

    // VALIDAR SI YA ESTÁ (Para no duplicar)
    public boolean existeEnBanner(Connection conn, int idJuego) throws SQLException {
        String sql = "SELECT id FROM banner WHERE id_juego = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idJuego);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    // CALCULAR SIGUIENTE ORDEN (Para agregarlo al final)
    public int obtenerSiguienteOrden(Connection conn) throws SQLException {
        String sql = "SELECT MAX(orden) FROM banner";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1) + 1; // El máximo + 1
            }
        }
        return 1; // Si está vacío, empieza en 1
    }

    // (Opcional) ACTUALIZAR ORDEN
    public void actualizarOrden(Connection conn, int idJuego, int nuevoOrden) throws SQLException {
        String sql = "UPDATE banner SET orden = ? WHERE id_juego = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, nuevoOrden);
            ps.setInt(2, idJuego);
            ps.executeUpdate();
        }
    }

    // Helper para no repetir código de mapeo
    private BannerResponse mapearResultSet(ResultSet rs) throws SQLException {
        BannerResponse dto = new BannerResponse();
        dto.setIdJuego(rs.getInt("id"));
        dto.setTitulo(rs.getString("titulo"));

        // Recortar descripción si es muy larga para el banner
        String desc = rs.getString("descripcion");
        if (desc != null && desc.length() > 100) {
            desc = desc.substring(0, 100) + "...";
        }
        dto.setDescripcion(desc);

        // Convertir BLOB a Base64
        byte[] imgBytes = rs.getBytes("imagen");
        if (imgBytes != null && imgBytes.length > 0) {
            String base64 = Base64.getEncoder().encodeToString(imgBytes);
            // Detectar tipo (asumiendo jpeg por defecto, o puedes guardar el mime type en BD)
            dto.setImagenBase64("data:image/jpeg;base64," + base64);
        }
        return dto;
    }
}
