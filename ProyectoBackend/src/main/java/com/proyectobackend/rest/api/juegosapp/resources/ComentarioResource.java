package com.proyectobackend.rest.api.juegosapp.resources;

import com.proyectobackend.rest.api.juegosapp.dtos.MensajeResponse;
import com.proyectobackend.rest.api.juegosapp.dtos.comentario.ComentarioRequest;
import com.proyectobackend.rest.api.juegosapp.dtos.comentario.ComentarioResponse;
import com.proyectobackend.rest.api.juegosapp.services.ComentarioService;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/comentario")
public class ComentarioResource {
    private final ComentarioService comentarioService = new ComentarioService();

    // COMENTAR (POST)
    @POST
    @Path("/{idJuego}/comentar")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response agregarComentario(
            @PathParam("idJuego") Integer idJuego,
            @QueryParam("idUsuario") Integer idUsuario, // O sacado del Token
            ComentarioRequest request
    ) {
        try {
            comentarioService.publicarComentario(idUsuario, idJuego, request);
            return Response.ok(new MensajeResponse("Comentario publicado")).build();
        } catch (Exception e) {
            return Response.serverError().entity(new MensajeResponse(e.getMessage())).build();
        }
    }

    // OBTENER COMENTARIOS (GET)
    @GET
    @Path("/{idJuego}/comentarios")
    @Produces(MediaType.APPLICATION_JSON)
    public Response verComentarios(@PathParam("idJuego") Integer idJuego) {
        try {
            List<ComentarioResponse> arbol = comentarioService.obtenerArbolDeComentarios(idJuego);
            return Response.ok(arbol).build();
        } catch (Exception e) {
            return Response.serverError().entity(new MensajeResponse(e.getMessage())).build();
        }
    }

    @PATCH
    @Path("/{idComentario}/estado")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response moderarComentario(
            @PathParam("idComentario") Integer idComentario,
            @FormParam("estado") String estado // "OCULTO" o "VISIBLE"
    ) {

        try {
            comentarioService.moderarComentario(idComentario, estado);
            return Response.ok(new MensajeResponse("Estado del comentario actualizado.")).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(new MensajeResponse(e.getMessage())).build();
        }
    }
}