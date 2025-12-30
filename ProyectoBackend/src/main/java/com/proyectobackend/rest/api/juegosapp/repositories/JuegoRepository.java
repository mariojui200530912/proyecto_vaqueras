package com.proyectobackend.rest.api.juegosapp.repositories;

import java.io.InputStream;
import java.math.BigDecimal;
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
            conn.setAutoCommit(false);
            try (PreparedStatement psJuego = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                 PreparedStatement psCategoria = conn.prepareStatement(sqlCat);
                 PreparedStatement psImg = conn.prepareStatement(sqlImg)) {
                // Insertar Juego
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

                // Insertar Categorías
                if (categoriasIds != null && !categoriasIds.isEmpty()) {
                    for (Integer idCat : categoriasIds) {
                        psCategoria.setInt(1, idJuego);
                        psCategoria.setInt(2, idCat);
                        psCategoria.addBatch(); // Agregamos al lote
                    }
                    psCategoria.executeBatch(); // Ejecutamos todas juntas
                }

                // Insertar Imágenes
                // Insertar Portada
                if (portadaBytes != null) {
                    psImg.setInt(1, idJuego);
                    psImg.setBytes(2, portadaBytes);
                    psImg.setString(3, "PORTADA");
                    psImg.executeUpdate();
                }

                // Insertar Galería (Gameplay)
                if (galeriaBytes != null) {
                    for (byte[] imagen : galeriaBytes) {
                        psImg.setInt(1, idJuego);
                        psImg.setBytes(2, imagen);
                        psImg.setString(3, "GAMEPLAY");
                        psImg.addBatch();
                    }
                    psImg.executeBatch();
                }

                //CONFIRMAR TRANSACCIÓN (COMMIT)
                conn.commit();
                return juego;

            } catch (SQLException e) {
                // SI ALGO FALLA DESHACER
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

    public Optional<Juego> buscarPorId(Connection conn, int id) throws SQLException {
        String sql = "SELECT * FROM juego WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
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
        } catch (Exception e){
            throw e;
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
                    img.setIdJuego(rs.getInt("id_juego"));
                    img.setImagen(rs.getBytes("imagen")); // <--- getBytes
                    img.setAtributo(rs.getString("atributo"));
                    lista.add(img);
                }
            }
        }
        return lista;
    }

    public List<Juego> buscarConFiltros(String titulo, Integer idCategoria, BigDecimal minPrecio, BigDecimal maxPrecio) {
        List<Juego> lista = new ArrayList<>();
        List<Object> parametros = new ArrayList<>();

        StringBuilder sql = new StringBuilder("SELECT j.* FROM juego j LEFT JOIN juego_categoria jc ON j.id = jc.id_juego WHERE 1=1 ");

        if (titulo != null && !titulo.trim().isEmpty()) {
            sql.append("AND j.titulo LIKE ? ");
            parametros.add("%" + titulo + "%"); // Búsqueda parcial
        }

        if (idCategoria != null && idCategoria > 0) {
            sql.append("AND jc.id_categoria = ? ");
            parametros.add(idCategoria);
        }

        if (minPrecio != null) {
            sql.append("AND j.precio >= ? ");
            parametros.add(minPrecio);
        }

        if (maxPrecio != null) {
            sql.append("AND j.precio <= ? ");
            parametros.add(maxPrecio);
        }

        // Evitar duplicados por el JOIN de categorías
        sql.append("GROUP BY j.id ");

        try (Connection con = DBConnection.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {

            for (int i = 0; i < parametros.size(); i++) {
                ps.setObject(i + 1, parametros.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearJuego(rs));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }

    //Actualiza los datos basicos del juego
    public void actualizarDatosBasicosJuego(Connection conn, Juego juego) throws SQLException {
        String sql = "UPDATE juego SET titulo=?, descripcion=?, precio=?, recursos_minimos=?, clasificacion=?, estado_venta=? WHERE id=?";
        try (Connection con = DBConnection.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, juego.getTitulo());
            ps.setString(2, juego.getDescripcion());
            ps.setBigDecimal(3, juego.getPrecio());
            ps.setString(4, juego.getRecursosMinimos());
            ps.setString(5, juego.getClasificacion());
            ps.setString(6, juego.getEstadoVenta());
            ps.setInt(7, juego.getId());
            ps.executeUpdate();
        }
    }

    //Gestionar Categorias de Juego
    public void elimnarCategoriasJuego(Connection conn, Integer idJuego, Integer idCategoria) throws SQLException {
        String sql = "DELETE FROM juego_categoria WHERE id_juego = ? AND id_categoria = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setInt(1, idJuego);
            ps.setInt(2, idCategoria);
            ps.executeUpdate();
        }
    }

    public void insertarCategoriaJuego(Connection conn, Integer idJuego, Integer idCategoria) throws SQLException {
        String sql = "INSERT INTO juego_categoria (id_juego, id_categoria) VALUES (?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setInt(1, idJuego);
            ps.setInt(2, idCategoria);
            ps.executeUpdate();
        }
    }

    public boolean existeCategoriaEnJuego(Connection conn, int idJuego, int idCategoria) throws SQLException {
        String sql = "SELECT 1 FROM juego_categoria WHERE id_juego = ? AND id_categoria = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idJuego);
            ps.setInt(2, idCategoria);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    //Gestionar Imagenes
    public void actualizarPortada(Connection conn, Integer idJuego, byte[] nuevaPortada) throws SQLException {
        String sql = "UPDATE imagen_juego SET imagen = ? WHERE id_juego = ? AND atributo = 'PORTADA'";
        try (PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setBytes(1, nuevaPortada);
            ps.setInt(2, idJuego);
            ps.executeUpdate();
        }
    }

    public void agregarImagenGaleria(Connection conn, Integer idJuego, byte[] imagen) throws SQLException {
        String sql = "INSERT INTO imagen_juego (id_juego, imagen, atributo) VALUES (?, ?, 'GAMEPLAY')";
        try (PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setInt(1, idJuego);
            ps.setBytes(2, imagen);
            ps.executeUpdate();
        }
    }

    public void eliminarImagenPorId(Connection conn, int idJuego, int idImagen) throws SQLException {
        String sql = "DELETE FROM imagen_juego WHERE id = ? AND id_juego = ? AND atributo = 'GAMEPLAY'";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idImagen);
            ps.setInt(2, idJuego);

            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas == 0) {
                throw new SQLException("No se encontró la imagen o no pertenece a este juego.");
            }
        }
    }

    public void actualizarPromedioCalificacion(Connection conn, int idJuego, BigDecimal calificacionPromedio) throws SQLException {
        String sql = "UPDATE juego SET calificacion_promedio = ? WHERE id_juego = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setBigDecimal(1, calificacionPromedio);
            ps.setInt(2, idJuego);
            ps.executeUpdate();
        }
    }

    public int obtenerIdEmpresaDelJuego(Connection conn, int idJuego) throws SQLException {
        String sql = "SELECT id_empresa_creadora FROM juego WHERE id = ?"; // Ajusta el nombre de tu columna FK
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idJuego);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return 0;
    }

    public List<Juego> listarPorEmpresa(Connection conn, int idEmpresa, boolean soloActivos) throws SQLException {
        List<Juego> lista = new ArrayList<>();

        StringBuilder sql = new StringBuilder("SELECT * FROM juego WHERE id_empresa = ?");

        if (soloActivos) {
            sql.append(" AND estado_venta = 'ACTIVO'"); // O el estado que uses para 'visible'
        }

        // Ordenar por fecha de lanzamiento, los más nuevos primero
        sql.append(" ORDER BY fecha_lanzamiento DESC");

        try (PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            ps.setInt(1, idEmpresa);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Juego j = new Juego();
                    j.setId(rs.getInt("id"));
                    j.setTitulo(rs.getString("titulo"));
                    j.setDescripcion(rs.getString("descripcion"));
                    j.setPrecio(rs.getBigDecimal("precio"));
                    j.setClasificacion(rs.getString("clasificacion"));
                    j.setEstadoVenta(rs.getString("estado_venta")); // Importante para el admin
                    j.setFechaLanzamiento(rs.getDate("fecha_lanzamiento").toLocalDate());
                    // ... mapear resto de campos ...

                    lista.add(j);
                }
            }
        }
        return lista;
    }

    public List<Juego> listarJuegosPublicos(Connection conn) throws SQLException {
        List<Juego> lista = new ArrayList<>();
        String sql = "SELECT * FROM juego WHERE estado_venta = 'ACTIVO' ORDER BY fecha_lanzamiento DESC";

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearJuego(rs));
            }
        }
        return lista;
    }

    public List<Juego> obtenerJuegosMejorBalance(Connection conn, int limit) throws SQLException {
        List<Juego> lista = new ArrayList<>();

        // SQL
        // m = 10 (constante de amortiguación)
        // C = 3.0 (promedio global estimado)
        String sql =
                "SELECT j.*, " +
                        "       COUNT(v.id) as total_ventas, " +
                        "       ( (COUNT(v.id) / (COUNT(v.id) + 10.0)) * j.calificacion_promedio ) + " +
                        "       ( (10.0 / (COUNT(v.id) + 10.0)) * 3.0 ) AS score_balance " +
                        "FROM juego j " +
                        "LEFT JOIN venta v ON j.id = v.id_juego WHERE j.estado_venta = 'ACTIVO' " +
                        "GROUP BY j.id ORDER BY score_balance DESC " + // Ordenamos por la fórmula
                        "LIMIT ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearJuego(rs));
                }
            }
        }
        return lista;
    }

    public void guardarImagenBanner(Connection conn, int idJuego, byte[] imagenBytes) throws SQLException {
        // Limpieza: Borrar si ya existía un banner previo para este juego
        String sqlDelete = "DELETE FROM imagen_juego WHERE id_juego = ? AND atributo = 'BANNER'";
        try (PreparedStatement ps = conn.prepareStatement(sqlDelete)) {
            ps.setInt(1, idJuego);
            ps.executeUpdate();
        }

        // Inserción: Guardar la nueva imagen con la etiqueta correcta
        String sqlInsert = "INSERT INTO imagen_juego (id_juego, imagen, atributo) VALUES (?, ?, 'BANNER')";
        try (PreparedStatement ps = conn.prepareStatement(sqlInsert)) {
            ps.setInt(1, idJuego);
            ps.setBytes(2, imagenBytes);
            ps.executeUpdate();
        }
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

    public void cambiarEstadoVenta(Connection conn, int idJuego, String estado) throws SQLException {
        String sql = "UPDATE juego SET estado_venta = ? WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, estado);
            ps.setInt(2, idJuego);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar en base de datos estado de venta: " + e);
        }
    }

}
