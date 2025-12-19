package com.proyectobackend.rest.api.juegosapp.resources;

import com.proyectobackend.rest.api.juegosapp.dtos.MensajeResponse;
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
    @Produces(MediaType.APPLICATION_JSON)
    public Response verMiBiblioteca() {
        try {
            // TODO: Obtener ID real del Token
            int idUsuarioLogueado = 4;

            List<JuegoResponse> misJuegos = bibliotecaService.obtenerMisJuegos(idUsuarioLogueado);
            return Response.ok(misJuegos).build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new MensajeResponse("Error: " + e.getMessage()))
                    .build();
        }
    }
}