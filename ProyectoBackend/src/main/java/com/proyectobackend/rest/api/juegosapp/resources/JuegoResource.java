package com.proyectobackend.rest.api.juegosapp.resources;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.proyectobackend.rest.api.juegosapp.dtos.MensajeResponse;
import com.proyectobackend.rest.api.juegosapp.dtos.juego.EstadoJuegoRequest;
import com.proyectobackend.rest.api.juegosapp.dtos.juego.JuegoRequest;
import com.proyectobackend.rest.api.juegosapp.dtos.juego.JuegoResponse;
import com.proyectobackend.rest.api.juegosapp.models.Categoria;
import com.proyectobackend.rest.api.juegosapp.models.Juego;
import com.proyectobackend.rest.api.juegosapp.services.JuegoService;
import com.proyectobackend.rest.api.juegosapp.utils.FileUploadUtil;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import java.io.InputStream;
import java.math.BigDecimal;
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
            JuegoResponse juegoCreado = juegoService.publicarJuego(
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

    @GET
    @Path("/buscar")
    @Produces(MediaType.APPLICATION_JSON)
    public Response buscarJuegos(
            @QueryParam("titulo") String titulo,
            @QueryParam("categoria") Integer idCategoria,
            @QueryParam("min") BigDecimal minPrecio,
            @QueryParam("max") BigDecimal maxPrecio
    ) {
        try {
            List<JuegoResponse> resultados = juegoService.buscarJuegos(titulo, idCategoria, minPrecio, maxPrecio);

            return Response.ok(resultados).build();

        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new MensajeResponse(e.getMessage()))
                    .build();
        }
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response juegoPorId(
            @PathParam("id") Integer idJuego
    ) {
        try {
            JuegoResponse juego = juegoService.obtenerJuegoPorId(idJuego);
            return Response.ok(juego).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new MensajeResponse("Error al obtener los detalles del juego : " + e.getMessage()))
                    .build();
        }
    }

    @GET
    @Path("/catalogo")
    @Produces(MediaType.APPLICATION_JSON)
    public Response listarJuegos() {
        try {
            List<JuegoResponse> juegos = juegoService.obtenerCatalogoCompleto();
            return Response.ok(juegos).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new MensajeResponse("Error al obtener el catalogo: " + e.getMessage()))
                    .build();
        }
    }

    @GET
    @Path("/catalogo/{id}/empresa")
    @Produces(MediaType.APPLICATION_JSON)
    public Response verCatalogoEmpresa(
            @PathParam("id") Integer idEmpresa,
            @QueryParam("rol") String rol
    ) {
        try {

            boolean esAdmin = "EMPRESA".equalsIgnoreCase(rol);

            List<JuegoResponse> catalogo = juegoService.obtenerCatalogoEmpresa(idEmpresa, esAdmin);

            return Response.ok(catalogo).build();

        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new MensajeResponse("Error al obtener catalogo de empresa: " + e.getMessage()))
                    .build();
        }
    }

    @GET
    @Path("/destacados")
    @Produces(MediaType.APPLICATION_JSON)
    public Response obtenerJuegosDestacados() {
        try {
            List<JuegoResponse> destacados = juegoService.obtenerDestacados();
            return Response.ok(destacados).build();
        } catch (Exception e) {
            return Response.status(500).entity(new MensajeResponse(e.getMessage())).build();
        }
    }

    @POST
    @Path("/{idJuego}/actualizar")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response actualizarJuego(
            @PathParam("idJuego") Integer idJuego,
            @FormDataParam("idUsuario") Integer idUsuario,
            @FormDataParam("datos") String jsonDatos
            ) {

        //Validar Usuario
        Integer idUsuarioLogueado = idUsuario;
        try {
            //Convertir JSON
            JuegoRequest request = objectMapper.readValue(jsonDatos, JuegoRequest.class);
            //Llamar al servicio
            juegoService.actualizarJuego(idJuego, request);
            return Response.status(Response.Status.CREATED).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new MensajeResponse("Error al publicar juego: " + e.getMessage()))
                    .build();
        }
    }

    @GET
    @Path("/{idJuego}/categorias")
    @Produces(MediaType.APPLICATION_JSON)
    public Response obtenerCategoriasDeJuego(@PathParam("idJuego") Integer idJuego) {
        try {
            List<Categoria> categorias = juegoService.obtenerCategoriasPorJuego(idJuego);
            return Response.ok(categorias).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new MensajeResponse("Error al obtener categorías: " + e.getMessage()))
                    .build();
        }
    }

    @POST
    @Path("/{idJuego}/categoria/{idCategoria}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response insertarCategoriaJuego(
            @PathParam("idJuego") Integer idJuego,
            @PathParam("idCategoria") Integer idCategoria
    ){
        try{
            juegoService.insertarCategoriaJuego(idJuego, idCategoria);
            return Response.ok(new MensajeResponse("Categoria ingresada a juego correctamente")).build();
        } catch (Exception e){
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new MensajeResponse("Error al ingresar categoria: " + e.getMessage()))
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }
    }


    @DELETE
    @Path("/{idJuego}/categoria")
    public Response eliminarCategoria(@PathParam("idJuego") Integer idJuego, @QueryParam("idCategoria") Integer idCategoria) {
        try{
            juegoService.eliminarCategoriaJuego(idJuego, idCategoria);
            return Response.ok(new MensajeResponse("Categoria eliminada del juego correctamente")).build();
        }catch(Exception e){
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new MensajeResponse("Error al eliminar la categoria del juego: " + e.getMessage()))
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }
    }

    @PUT
    @Path("/{idJuego}/actualizar/imagen")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response actualizarImagenJuegoPortada(@PathParam("idJuego") Integer idJuego, @FormDataParam("portada") InputStream imgPortada){
        try{
            juegoService.actualizarImagenPortada(idJuego, imgPortada);
            return Response.ok(new MensajeResponse("Imagen de portada de juego actualizada correctamente")).build();
        } catch (Exception e){
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new MensajeResponse("Error al actualizar imagen de portada: " + e.getMessage()))
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }
    }

    @POST
    @Path("/{idJuego}/galeria")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response agregarImagenesGaleria(
            @PathParam("idJuego") Integer idJuego,
            @FormDataParam("imagenes") List<FormDataBodyPart> bodyParts
    ) {
        try {
            if (bodyParts == null || bodyParts.isEmpty()) {
                throw new Exception("No se han enviado imágenes.");
            }
            juegoService.agregarImagenesGaleria(idJuego, bodyParts);
            return Response.ok(new MensajeResponse("Imágenes agregadas a la galería exitosamente")).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new MensajeResponse("Error al agregar imágenes: " + e.getMessage()))
                    .build();
        }
    }

    @DELETE
    @Path("/{idJuego}/galeria/{idImagen}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response eliminarImagenGaleria(
            @PathParam("idJuego") Integer idJuego,
            @PathParam("idImagen") Integer idImagen
    ) {
        try {
            juegoService.eliminarImagenGaleria(idJuego, idImagen);

            return Response.ok(new MensajeResponse("Imagen eliminada correctamente")).build();

        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new MensajeResponse("Error al eliminar imagen: " + e.getMessage()))
                    .build();
        }
    }

    @PUT
    @Path("/{id}/imagenes/banner")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response subirBanner(
            @PathParam("id") Integer idJuego,
            @FormDataParam("imagen") InputStream imagenStream,
            @FormDataParam("imagen") FormDataContentDisposition fileDetail
    ) {
        try {
            // Validar que venga el archivo
            if (imagenStream == null || fileDetail == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new MensajeResponse("Debe seleccionar un archivo de imagen."))
                        .build();
            }

            juegoService.subirImagenBanner(idJuego, imagenStream);

            return Response.ok(new MensajeResponse("Imagen de Banner actualizada correctamente."))
                    .build();

        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new MensajeResponse("Error: " + e.getMessage()))
                    .build();
        }
    }

    @PATCH
    @Path("/{idJuego}/estado")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response cambiarEstadoJuego(
            @PathParam("idJuego") Integer idJuego,
            EstadoJuegoRequest request // DTO simple { "estado": "ACTIVO" | "SUSPENDIDO" }
    ) {
        try {
            // Validamos
            if (request == null || request.getEstadoVenta() == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new MensajeResponse("Debe enviar el nuevo estado (ACTIVO/SUSPENDIDO)."))
                        .build();
            }

            // Llamamos al servicio (Debes implementar este método en JuegoService)
            juegoService.cambiarEstado(idJuego, request.getEstadoVenta());

            return Response.ok(new MensajeResponse("El estado del juego se actualizó a: " + request.getEstadoVenta()))
                    .build();

        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new MensajeResponse("Error: " + e.getMessage()))
                    .build();
        }
    }

    @PATCH
    @Path("/{idJuego}/configuracion/comentarios")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response configurarComentariosJuego(
            @PathParam("idJuego") Integer idJuego,
            @FormDataParam("permitir") Boolean permitir
    ) {
        try {
            if (permitir == null) throw new Exception("Debe especificar true o false.");

            juegoService.configurarComentarios(idJuego, permitir);

            String estado = permitir ? "HABILITADOS" : "DESHABILITADOS";
            return Response.ok(new MensajeResponse("Comentarios " + estado + " para este juego.")).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new MensajeResponse("Error: " + e.getMessage())).build();
        }
    }
}