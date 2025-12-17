package com.proyectobackend.rest.api.juegosapp.resources;

import com.proyectobackend.rest.api.juegosapp.dtos.MensajeResponse;
import com.proyectobackend.rest.api.juegosapp.dtos.categoria.CategoriaRequest;
import com.proyectobackend.rest.api.juegosapp.dtos.categoria.CategoriaResponse;
import com.proyectobackend.rest.api.juegosapp.services.CategoriaService;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/categoria")
public class CategoriaResource {
    private final CategoriaService categoriaService;

    public CategoriaResource() {
        this.categoriaService = new CategoriaService();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response listar() {
        try {
            List<CategoriaResponse> lista = categoriaService.listarCategorias();
            return Response.ok(lista).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new MensajeResponse("Error: " + e.getMessage())).build();
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response crear(CategoriaRequest request) {
        try {
            CategoriaResponse creada = categoriaService.crearCategoria(request);
            return Response.status(Response.Status.CREATED).entity(creada).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new MensajeResponse("Error: " + e.getMessage())).build();
        }
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response actualizar(@PathParam("id") int id, CategoriaRequest request) {
        try {
            CategoriaResponse actualizada = categoriaService.actualizarCategoria(id, request);
            return Response.ok(actualizada).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new MensajeResponse("Error: " + e.getMessage())).build();
        }
    }

    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response eliminar(@PathParam("id") int id) {
        try {
            categoriaService.eliminarCategoria(id);
            return Response.ok(new MensajeResponse("Categoría eliminada correctamente.")).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new MensajeResponse("Error: " + e.getMessage() + " (Probablemente esté asignada a un juego)")).build();
        }
    }
}