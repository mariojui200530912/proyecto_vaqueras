package com.proyectobackend.rest.api.juegosapp.repositories;

import java.sql.*;
import java.util.List;
import com.proyectobackend.rest.api.juegosapp.models.Juego;

public class JuegoRepository {
    private static final String SQL_CREATE_GAME = "INSERT INTO juego (id_empresa, titulo, descripcion, precio, recursos_minimos, clasificacion, fecha_lanzamiento, estado_venta) VALUES (?, ?, ?, ?, ?, ?, CURRENT_DATE, 'ACTIVO')";
    private static final String SQL_CREATE_GAME_CATEGORY = "INSERT INTO juego_categoria (id_juego, id_categoria) VALUES (?, ?)";
    private static final String SQL_CREATE_GAME_IMAGES = "INSERT INTO imagen_juego (id_juego, url, atributo) VALUES (?, ?, ?)";

    public Juego crearJuegoCompleto(Juego juego, List<Integer> categoriasIds, String urlPortada, List<String> urlsGaleria) throws SQLException {
        String sql = SQL_CREATE_GAME;
        String sqlCat = SQL_CREATE_GAME_CATEGORY;
        String sqlImg = SQL_CREATE_GAME_IMAGES;
        Connection conn = DBConnection.getInstance().getConnection();
        Integer idJuego;

        try (
             PreparedStatement psJuego = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
             PreparedStatement psCategoria = conn.prepareStatement(sqlCat);
             PreparedStatement psImg = conn.prepareStatement(sqlImg)) {
            // 1. INICIAR TRANSACCIÓN (Desactivar guardado automático)
            conn.setAutoCommit(false);

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
            if (urlPortada != null) {
                psImg.setInt(1, idJuego);
                psImg.setString(2, urlPortada);
                psImg.setString(3, "PORTADA");
                psImg.executeUpdate();
            }

            // 2. Insertar Galería (Gameplay)
            if (urlsGaleria != null) {
                for (String url : urlsGaleria) {
                    psImg.setInt(1, idJuego);
                    psImg.setString(2, url);
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
                } catch (SQLException ex) { ex.printStackTrace(); }
            }
            throw e;
        } finally {
            // Restaurar estado y cerrar
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }
}
