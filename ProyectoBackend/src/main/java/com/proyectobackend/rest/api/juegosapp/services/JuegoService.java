package com.proyectobackend.rest.api.juegosapp.services;

import com.proyectobackend.rest.api.juegosapp.dtos.juego.JuegoRequest;
import com.proyectobackend.rest.api.juegosapp.dtos.juego.JuegoResponse;
import com.proyectobackend.rest.api.juegosapp.models.Categoria;
import com.proyectobackend.rest.api.juegosapp.models.ImagenJuego;
import com.proyectobackend.rest.api.juegosapp.models.Juego;
import com.proyectobackend.rest.api.juegosapp.repositories.JuegoRepository;
import com.proyectobackend.rest.api.juegosapp.repositories.UsuarioRepository;
import com.proyectobackend.rest.api.juegosapp.utils.FileUploadUtil;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;

import java.io.InputStream;
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
            // 1. Obtener ID de la empresa del usuario
            int idEmpresa = usuarioRepository.obtenerIdEmpresaPorUsuario(idUsuarioLogueado);
            if (idEmpresa == 0) throw new Exception("El usuario no pertenece a ninguna empresa.");

            // 2. Guardar Portada (Obligatoria)

            byte[] portadaBytes = FileUploadUtil.leerBytesDeInput(portadaInput);
            if (portadaBytes == null || portadaBytes.length == 0) throw new Exception("Portada obligatoria");

            // 3. Guardar Galería (Opcional)
            List<byte[]> galeriaBytes = new ArrayList<>();
            if (galeriaParts != null) {
                for (FormDataBodyPart part : galeriaParts) {
                    byte[] bytes = FileUploadUtil.leerBytesDeInput(part.getValueAs(InputStream.class));
                    if (bytes != null && bytes.length > 0) {
                        galeriaBytes.add(bytes);
                    }
                }
            }

            // 4. Preparar Objeto Juego
            Juego juego = new Juego();
            juego.setIdEmpresa(idEmpresa);
            juego.setTitulo(request.getTitulo());
            juego.setDescripcion(request.getDescripcion());
            juego.setPrecio(request.getPrecio());
            juego.setRecursosMinimos(request.getRecursosMinimos());
            juego.setClasificacion(request.getClasificacion());

            // 5. Llamar al Repo Transaccional
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

    private JuegoResponse construirResponse(Juego j, List<Categoria> categorias, List<ImagenJuego> imagenesBlob) {
        JuegoResponse resp = new JuegoResponse();

        // 1. Mapeo de Datos Básicos
        resp.setId(j.getId());
        resp.setTitulo(j.getTitulo());
        resp.setDescripcion(j.getDescripcion());
        resp.setPrecio(j.getPrecio());
        resp.setRecursosMinimos(j.getRecursosMinimos());
        resp.setClasificacion(j.getClasificacion());
        resp.setFecha_lanzamiento(j.getFechaLanzamiento());
        resp.setEstado_venta(j.getEstadoVenta());
        resp.setCalificacion_promedio(j.getCalificacionPromedio());

        // 2. Mapeo de Categorías (De Objetos a Lista de Nombres String)
        if (categorias != null) {
            List<String> nombresCategorias = categorias.stream()
                    .map(Categoria::getNombre) // Extraemos solo el nombre
                    .collect(Collectors.toList());
            resp.setCategorias(nombresCategorias);
        }

        // 3. Conversión de Imágenes (Bytes -> Base64)
        List<String> galeriaB64 = new ArrayList<>();

        if (imagenesBlob != null) {
            for (ImagenJuego img : imagenesBlob) {
                if (img.getImagen() != null && img.getImagen().length > 0) {
                    // Conversión nativa de Java
                    String b64 = Base64.getEncoder().encodeToString(img.getImagen());

                    if ("PORTADA".equals(img.getAtributo())) {
                        // Si es portada, va al campo único
                        resp.setPortada(b64);
                    } else {
                        // Si es gameplay, se agrega a la lista
                        galeriaB64.add(b64);
                    }
                }
            }
        }

        resp.setGaleria(galeriaB64);

        return resp;
    }
}
