package com.proyectobackend.rest.api.juegosapp.resources;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.proyectobackend.rest.api.juegosapp.dtos.MensajeResponse;
import com.proyectobackend.rest.api.juegosapp.dtos.juego.JuegoRequest;
import com.proyectobackend.rest.api.juegosapp.models.Juego;
import com.proyectobackend.rest.api.juegosapp.services.JuegoService;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import java.io.InputStream;
import java.util.List;

@Path("/juego")
public class JuegoResource {
    private final JuegoService juegoService;
    private final ObjectMapper objectMapper;

    public JuegoResource() {
        this.juegoService = new JuegoService();
        this.objectMapper = new ObjectMapper();
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response crearJuego(
            @FormDataParam("id_usuario") Integer id_usuario,
            @FormDataParam("datos") String jsonDatos,
            @FormDataParam("portada") InputStream portadaIS,
            @FormDataParam("portada") FormDataContentDisposition portadaDetalle,
            @FormDataParam("galeria") List<FormDataBodyPart> galeriaParts // Recibe múltiples archivos
    ) {
        try {
            // 1. Validar Usuario
            Integer idUsuarioLogueado = id_usuario;

            // 2. Convertir JSON
            JuegoRequest request = objectMapper.readValue(jsonDatos, JuegoRequest.class);

            // 3. Llamar al servicio
            Juego juegoCreado = juegoService.publicarJuego(
                    request,
                    idUsuarioLogueado,
                    portadaIS,
                    portadaDetalle != null ? portadaDetalle.getFileName() : null,
                    galeriaParts
            );

            return Response.status(Response.Status.CREATED).entity(juegoCreado).build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new MensajeResponse("Error al publicar juego: " + e.getMessage()))
                    .build();
        }
    }
}