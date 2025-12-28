package com.proyectobackend.rest.api.juegosapp.resources;

import com.proyectobackend.rest.api.juegosapp.dtos.MensajeResponse;
import com.proyectobackend.rest.api.juegosapp.services.CalificacionService;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/calificacion")
public class CalificacionResource {
    private CalificacionService calificacionService;
    public CalificacionResource() {
        this.calificacionService = new CalificacionService();
    }
    //CALIFICAR (POST)
    @POST
    @Path("/{idJuego}/calificar")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response calificarJuego(
            @PathParam("idJuego") Integer idJuego,
            @FormParam("idUsuario") Integer idUsuario,
            @FormParam("puntaje") Double puntaje
    ) {
        try {
            calificacionService.calificarJuego(idUsuario, idJuego, puntaje);
            return Response.ok(new MensajeResponse("Calificación guardada")).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(new MensajeResponse(e.getMessage())).build();
        }
    }
}