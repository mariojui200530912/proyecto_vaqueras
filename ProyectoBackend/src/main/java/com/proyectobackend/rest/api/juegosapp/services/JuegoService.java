package com.proyectobackend.rest.api.juegosapp.services;

import com.proyectobackend.rest.api.juegosapp.dtos.juego.JuegoRequest;
import com.proyectobackend.rest.api.juegosapp.models.Juego;
import com.proyectobackend.rest.api.juegosapp.repositories.JuegoRepository;
import com.proyectobackend.rest.api.juegosapp.repositories.UsuarioRepository;
import com.proyectobackend.rest.api.juegosapp.utils.FileUploadUtil;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

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

        // 1. Obtener ID de la empresa del usuario

        int idEmpresa = usuarioRepository.obtenerIdEmpresaPorUsuario(idUsuarioLogueado);
        if (idEmpresa == 0) throw new Exception("El usuario no pertenece a ninguna empresa.");

        // 2. Guardar Portada (Obligatoria)
        String urlPortada = null;
        if (portadaInput != null && nombrePortada != null) {
            urlPortada = FileUploadUtil.saveFile(portadaInput, nombrePortada, "juegos/portadas");
        } else {
            throw new Exception("La portada es obligatoria.");
        }

        // 3. Guardar Galería (Opcional)
        List<String> urlsGaleria = new ArrayList<>();
        if (galeriaParts != null) {
            for (FormDataBodyPart part : galeriaParts) {
                InputStream is = part.getValueAs(InputStream.class);
                String fileName = part.getFormDataContentDisposition().getFileName();
                if (fileName != null && !fileName.isEmpty()) {
                    String url = FileUploadUtil.saveFile(is, fileName, "juegos/galeria");
                    urlsGaleria.add(url);
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
        try {
            return juegoRepository.crearJuegoCompleto(juego, request.getCategoriasIds(), urlPortada, urlsGaleria);
        } catch (Exception e) {
            // ROLLBACK MANUAL DE ARCHIVOS: Si falla la BD, borrar las fotos subidas
            FileUploadUtil.deleteFile(urlPortada);
            for (String url : urlsGaleria) FileUploadUtil.deleteFile(url);
            throw new Exception("Error al guardar juego en BD: " + e.getMessage());
        }
    }
}
