package com.proyectobackend.rest.api.juegosapp.resources;

import com.proyectobackend.rest.api.juegosapp.dtos.MensajeResponse;
import com.proyectobackend.rest.api.juegosapp.dtos.biblioteca.BibliotecaResponse;
import com.proyectobackend.rest.api.juegosapp.dtos.juego.JuegoResponse;
import com.proyectobackend.rest.api.juegosapp.services.BibliotecaService;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/biblioteca")
public class BibliotecaResource {
    private final BibliotecaService bibliotecaService;
    public BibliotecaResource() {
        this.bibliotecaService = new BibliotecaService();
    }
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response verMiBiblioteca(@PathParam("id") Integer id) {
        try {

            int idUsuarioLogueado = id;

            List<BibliotecaResponse> misJuegos = bibliotecaService.obtenerMisJuegos(idUsuarioLogueado);
            return Response.ok(misJuegos).build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new MensajeResponse("Error: " + e.getMessage()))
                    .build();
        }
    }

    @PUT
    @Path("/{idJuego}/instalar/{idUsuario}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response instalarJuegoBiblioteca(
            @PathParam("idJuego") Integer idJuego,
            @PathParam("idUsuario") Integer idUsuario,
            @QueryParam("instalar") boolean instalar
    ){
        try {
            bibliotecaService.cambiarEstadoInstalacion(idUsuario, idJuego, instalar);
            return Response.ok(new MensajeResponse("Estado actualizado exitosamente")).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new MensajeResponse("Error al instalar juego: " + e.getMessage()))
                    .build();
        }
    }
}

