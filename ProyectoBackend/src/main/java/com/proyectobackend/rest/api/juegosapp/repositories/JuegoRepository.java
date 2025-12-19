package com.proyectobackend.rest.api.juegosapp.repositories;

import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.proyectobackend.rest.api.juegosapp.models.Categoria;
import com.proyectobackend.rest.api.juegosapp.models.ImagenJuego;
import com.proyectobackend.rest.api.juegosapp.models.Juego;

public class JuegoRepository {
    private static final String SQL_CREATE_GAME = "INSERT INTO juego (id_empresa, titulo, descripcion, precio, recursos_minimos, clasificacion, fecha_lanzamiento, estado_venta) VALUES (?, ?, ?, ?, ?, ?, CURRENT_DATE, 'ACTIVO')";
    private static final String SQL_CREATE_GAME_CATEGORY = "INSERT INTO juego_categoria (id_juego, id_categoria) VALUES (?, ?)";
    private static final String SQL_CREATE_GAME_IMAGES = "INSERT INTO imagen_juego (id_juego, url, atributo) VALUES (?, ?, ?)";

    public Juego crearJuegoCompleto(Juego juego, List<Integer> categoriasIds, byte[] portadaBytes, List<byte[]> galeriaBytes) throws SQLException {
        String sql = SQL_CREATE_GAME;
        String sqlCat = SQL_CREATE_GAME_CATEGORY;
        String sqlImg = SQL_CREATE_GAME_IMAGES;
        Integer idJuego;

        try (Connection conn = DBConnection.getInstance().getConnection()) {
            // 1. INICIAR TRANSACCIÓN (Desactivar guardado automático)
            conn.setAutoCommit(false);
            try (PreparedStatement psJuego = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                 PreparedStatement psCategoria = conn.prepareStatement(sqlCat);
                 PreparedStatement psImg = conn.prepareStatement(sqlImg)) {
                // PASO A: Insertar Juego
                psJuego.setInt(1, juego.getIdEmpresa()); // ID de la empresa del usuario logueado
                psJuego.setString(2, juego.getTitulo());
                psJuego.setString(3, juego.getDescripcion());
                psJuego.setBigDecimal(4, juego.getPrecio());
                psJuego.setString(5, juego.getRecursosMinimos());
                psJuego.setString(6, juego.getClasificacion());
                psJuego.executeUpdate();

                // Obtener ID generado
                try (ResultSet generatedKeys = psJuego.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        idJuego = generatedKeys.getInt(1);
                        juego.setId(idJuego);
                    } else {
                        throw new SQLException("No se puede obtener el ID del juego.");
                    }
                }

                // PASO B: Insertar Categorías
                if (categoriasIds != null && !categoriasIds.isEmpty()) {
                    for (Integer idCat : categoriasIds) {
                        psCategoria.setInt(1, idJuego);
                        psCategoria.setInt(2, idCat);
                        psCategoria.addBatch(); // Agregamos al lote
                    }
                    psCategoria.executeBatch(); // Ejecutamos todas juntas
                }

                // PASO C: Insertar Imágenes
                // 1. Insertar Portada
                if (portadaBytes != null) {
                    psImg.setInt(1, idJuego);
                    psImg.setBytes(2, portadaBytes);
                    psImg.setString(3, "PORTADA");
                    psImg.executeUpdate();
                }

                // 2. Insertar Galería (Gameplay)
                if (galeriaBytes != null) {
                    for (byte[] imagen : galeriaBytes) {
                        psImg.setInt(1, idJuego);
                        psImg.setBytes(2, imagen);
                        psImg.setString(3, "GAMEPLAY");
                        psImg.addBatch();
                    }
                    psImg.executeBatch();
                }

                // 3. CONFIRMAR TRANSACCIÓN (COMMIT)
                conn.commit();
                return juego;

            } catch (SQLException e) {
                // SI ALGO FALLA, DESHACER TODO (ROLLBACK)
                if (conn != null) {
                    try {
                        System.err.println("Rollback por error: " + e.getMessage());
                        conn.rollback();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }
                throw e;
            }
        } catch (Exception e) {
            throw e;
        }
    }

    public Optional<Juego> buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM juego WHERE id = ?";

        try (Connection con = DBConnection.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearJuego(rs));
                }
            }
        }
        return Optional.empty();
    }

    public List<Categoria> obtenerCategoriasPorJuego(int idJuego) throws SQLException {
        List<Categoria> lista = new ArrayList<>();
        // Hacemos JOIN para obtener los nombres de las categorías directamente
        String sql = "SELECT c.id, c.nombre, c.descripcion " +
                "FROM categoria c " +
                "INNER JOIN juego_categoria jc ON c.id = jc.id_categoria " +
                "WHERE jc.id_juego = ?";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idJuego);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Categoria c = new Categoria();
                    c.setId(rs.getInt("id"));
                    c.setNombre(rs.getString("nombre"));
                    c.setDescripcion(rs.getString("descripcion"));
                    lista.add(c);
                }
            }
        }
        return lista;
    }

    public List<ImagenJuego> obtenerImagenesPorJuego(int idJuego) throws SQLException {
        List<ImagenJuego> lista = new ArrayList<>();
        String sql = "SELECT * FROM imagen_juego WHERE id_juego = ?";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idJuego);
            try (ResultSet rs = ps.executeQuery()) {
                while(rs.next()) {
                    ImagenJuego img = new ImagenJuego();
                    img.setId(rs.getInt("id"));
                    img.setImagen(rs.getBytes("imagen")); // <--- getBytes
                    img.setAtributo(rs.getString("atributo"));
                    lista.add(img);
                }
            }
        }
        return lista;
    }

    private Juego mapearJuego(ResultSet rs) throws SQLException {
        Juego j = new Juego();
        j.setId(rs.getInt("id"));
        j.setIdEmpresa(rs.getInt("id_empresa"));
        j.setTitulo(rs.getString("titulo"));
        j.setDescripcion(rs.getString("descripcion"));
        j.setPrecio(rs.getBigDecimal("precio"));
        j.setRecursosMinimos(rs.getString("recursos_minimos"));
        j.setClasificacion(rs.getString("clasificacion"));
        j.setEstadoVenta(rs.getString("estado_venta"));
        j.setCalificacionPromedio(rs.getBigDecimal("calificacion_promedio"));

        if (rs.getDate("fecha_lanzamiento") != null) {
            j.setFechaLanzamiento(rs.getDate("fecha_lanzamiento").toLocalDate());
        }
        return j;
    }

}
