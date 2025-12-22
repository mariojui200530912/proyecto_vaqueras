package com.proyectobackend.rest.api.juegosapp.services;

import com.proyectobackend.rest.api.juegosapp.dtos.juego.JuegoRequest;
import com.proyectobackend.rest.api.juegosapp.dtos.juego.JuegoResponse;
import com.proyectobackend.rest.api.juegosapp.models.Categoria;
import com.proyectobackend.rest.api.juegosapp.models.ImagenJuego;
import com.proyectobackend.rest.api.juegosapp.models.Juego;
import com.proyectobackend.rest.api.juegosapp.repositories.DBConnection;
import com.proyectobackend.rest.api.juegosapp.repositories.JuegoRepository;
import com.proyectobackend.rest.api.juegosapp.repositories.UsuarioRepository;
import com.proyectobackend.rest.api.juegosapp.utils.FileUploadUtil;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;

import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

public class JuegoService {
    private final JuegoRepository juegoRepository;
    private final UsuarioRepository usuarioRepository;

    public JuegoService() {
        this.juegoRepository = new JuegoRepository();
        this.usuarioRepository = new UsuarioRepository();
    }

    public Juego publicarJuego(JuegoRequest request,
                               Integer idUsuarioLogueado,
                               InputStream portadaInput, String nombrePortada,
                               List<FormDataBodyPart> galeriaParts) throws Exception {
        try {
            // Obtener ID de la empresa del usuario
            int idEmpresa = usuarioRepository.obtenerIdEmpresaPorUsuario(idUsuarioLogueado);
            if (idEmpresa == 0) throw new Exception("El usuario no pertenece a ninguna empresa.");

            // Guardar Portada (Obligatoria)

            byte[] portadaBytes = FileUploadUtil.leerBytesDeInput(portadaInput);
            if (portadaBytes == null || portadaBytes.length == 0) throw new Exception("Portada obligatoria");

            // Guardar Galería (Opcional)
            List<byte[]> galeriaBytes = new ArrayList<>();
            if (galeriaParts != null) {
                for (FormDataBodyPart part : galeriaParts) {
                    byte[] bytes = FileUploadUtil.leerBytesDeInput(part.getValueAs(InputStream.class));
                    if (bytes != null && bytes.length > 0) {
                        galeriaBytes.add(bytes);
                    }
                }
            }

            // Preparar Objeto Juego
            Juego juego = new Juego();
            juego.setIdEmpresa(idEmpresa);
            juego.setTitulo(request.getTitulo());
            juego.setDescripcion(request.getDescripcion());
            juego.setPrecio(request.getPrecio());
            juego.setRecursosMinimos(request.getRecursosMinimos());
            juego.setClasificacion(request.getClasificacion());

            // Llamar al Repo Transaccional
            return juegoRepository.crearJuegoCompleto(juego, request.getCategoriasIds(), portadaBytes, galeriaBytes);
        } catch (Exception e) {
            throw new Exception("Error al guardar juego en BD: " + e.getMessage());
        }
    }

    public JuegoResponse obtenerJuegoPorId(int id) throws Exception {
        Juego juego = juegoRepository.buscarPorId(id).orElseThrow(() -> new Exception("Juego no existe"));
        List<Categoria> categorias = juegoRepository.obtenerCategoriasPorJuego(juego.getId());
        // Recuperar imágenes de la BD
        List<ImagenJuego> imagenes = juegoRepository.obtenerImagenesPorJuego(id);

        return construirResponse(juego, categorias, imagenes);
    }

    public List<JuegoResponse> buscarJuegos(String titulo, Integer idCategoria, BigDecimal min, BigDecimal max) {
        List<JuegoResponse> respuesta = new ArrayList<>();
        try {
            // Obtener los juegos base
            List<Juego> juegosEntidad = juegoRepository.buscarConFiltros(titulo, idCategoria, min, max);


            // Iterar para convertir cada juego
            for (Juego j : juegosEntidad) {

                // Buscar sus categorías
                List<Categoria> categorias = juegoRepository.obtenerCategoriasPorJuego(j.getId());

                List<ImagenJuego> imagenes = juegoRepository.obtenerImagenesPorJuego(j.getId());

                JuegoResponse dto = construirResponse(j, categorias, imagenes);

                respuesta.add(dto);
            }
            return respuesta;
        } catch (Exception e){
            throw new RuntimeException("Error al mapear juegos en BD: " + e.getMessage());
        }
    }

    public void actualizarJuego(int idJuego, JuegoRequest request) throws Exception {
        try (Connection conn = DBConnection.getInstance().getConnection()) {
            Juego juegoActual = juegoRepository.buscarPorId(idJuego).orElseThrow(() -> new Exception("Juego no existe"));

            // Actualizar Datos Básicos
            Juego juegoUpdate = new Juego();
            juegoUpdate.setId(idJuego);
            juegoUpdate.setTitulo(request.getTitulo() != null && !request.getTitulo().isEmpty() ? request.getTitulo() : juegoActual.getTitulo());
            juegoUpdate.setDescripcion(request.getDescripcion() != null && !request.getDescripcion().isEmpty() ? request.getDescripcion() : juegoActual.getDescripcion());
            juegoUpdate.setPrecio(request.getPrecio().equals(0.00) ? request.getPrecio() : juegoActual.getPrecio());
            juegoUpdate.setRecursosMinimos(request.getRecursosMinimos() != null && !request.getRecursosMinimos().isEmpty() ? request.getRecursosMinimos() : juegoActual.getRecursosMinimos());
            juegoUpdate.setClasificacion(request.getClasificacion() != null && !request.getClasificacion().isEmpty() ? request.getClasificacion() : juegoActual.getClasificacion());

            juegoRepository.actualizarDatosBasicosJuego(conn, juegoUpdate);
        } catch (Exception e) {
            throw new Exception("Error al actualizar juego en BD: " + e.getMessage());
        }
    }

    public void eliminarCategoriaJuego(int idJuego, int idCategoria) throws Exception {
        try (Connection conn = DBConnection.getInstance().getConnection()) {
            juegoRepository.elimnarCategoriasJuego(conn, idJuego, idCategoria);
        }
    }

    public void insertarCategoriaJuego(int idJuego, int idCategoria) throws Exception {
        try (Connection conn = DBConnection.getInstance().getConnection()) {
            juegoRepository.insertarCategoriaJuego(conn, idJuego, idCategoria);
        }
    }

    public void actualizarImagenPortada(int idJuego, InputStream nuevaPortada) throws Exception{
        try (Connection conn = DBConnection.getInstance().getConnection()){
            byte[] nuevaPortadaBytes = Files.readAllBytes(Paths.get(nuevaPortada.toString()));
            juegoRepository.actualizarPortada(conn, idJuego, nuevaPortadaBytes);
        }
    }

    public void agregarImagenesGaleria(int idJuego, List<FormDataBodyPart> bodyParts) throws Exception {
        try (Connection conn = DBConnection.getInstance().getConnection()) {
            conn.setAutoCommit(false); // Transacción
            try {
                for (FormDataBodyPart part : bodyParts) {
                    // Convertir InputStream a byte[]
                    InputStream is = part.getEntityAs(InputStream.class);
                    byte[] imagenBytes = FileUploadUtil.leerBytesDeInput(is);

                    if (imagenBytes != null && imagenBytes.length > 0) {
                        juegoRepository.agregarImagenGaleria(conn, idJuego, imagenBytes);
                    }
                }
                conn.commit();
            } catch (Exception e) {
                if (conn != null) conn.rollback();
                throw new Exception("Error al guardar galería: " + e.getMessage());
            }
        }
    }

    public void eliminarImagenGaleria(Integer idJuego, Integer idImagen) throws Exception {
        try (Connection conn = DBConnection.getInstance().getConnection()) {
            juegoRepository.eliminarImagenPorId(conn, idJuego, idImagen);
        } catch (Exception e) {
            throw new Exception("Error al borrar la imagen: " + e.getMessage());
        }
    }

    private JuegoResponse construirResponse(Juego j, List<Categoria> categorias, List<ImagenJuego> imagenesBlob) {
        JuegoResponse resp = new JuegoResponse();

        // Mapeo de Datos Básicos
        resp.setId(j.getId());
        resp.setTitulo(j.getTitulo());
        resp.setDescripcion(j.getDescripcion());
        resp.setPrecio(j.getPrecio());
        resp.setRecursosMinimos(j.getRecursosMinimos());
        resp.setClasificacion(j.getClasificacion());
        resp.setFecha_lanzamiento(j.getFechaLanzamiento());
        resp.setEstado_venta(j.getEstadoVenta());
        resp.setCalificacion_promedio(j.getCalificacionPromedio());

        // Mapeo de Categorías (De Objetos a Lista de Nombres String)
        if (categorias != null) {
            List<String> nombresCategorias = categorias.stream()
                    .map(Categoria::getNombre) // Extraemos solo el nombre
                    .collect(Collectors.toList());
            resp.setCategorias(nombresCategorias);
        }

        // Conversión de Imágenes (Bytes -> Base64)
        List<String> galeriaB64 = new ArrayList<>();

        if (imagenesBlob != null) {
            for (ImagenJuego img : imagenesBlob) {
                if (img.getImagen() != null && img.getImagen().length > 0) {
                    // Conversión nativa de Java
                    String b64 = Base64.getEncoder().encodeToString(img.getImagen());

                    if ("PORTADA".equals(img.getAtributo())) {
                        // Si es portada, va al campo único
                        resp.setPortada("data:image/jpeg;base64," + b64);
                    } else {
                        // Si es gameplay, se agrega a la lista
                        galeriaB64.add("data:image/jpeg;base64," + b64);
                    }
                }
            }
        }

        resp.setGaleria(galeriaB64);
        return resp;
    }
}
