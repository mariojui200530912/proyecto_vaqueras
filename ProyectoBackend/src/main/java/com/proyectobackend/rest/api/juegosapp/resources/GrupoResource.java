package com.proyectobackend.rest.api.juegosapp.resources;

import com.proyectobackend.rest.api.juegosapp.dtos.MensajeResponse;
import com.proyectobackend.rest.api.juegosapp.dtos.grupo.GrupoRequest;
import com.proyectobackend.rest.api.juegosapp.dtos.grupo.GrupoResponse;
import com.proyectobackend.rest.api.juegosapp.services.GrupoService;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/grupo")
public class GrupoResource {
    private final GrupoService grupoService = new GrupoService();

    // CREAR GRUPO
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response crearGrupo(@QueryParam("idUsuario") Integer idUsuario, GrupoRequest req) {
        try {
            GrupoResponse nuevoGrupo = grupoService.crearGrupo(idUsuario, req.getNombre());
            return Response.status(Response.Status.CREATED).entity(nuevoGrupo).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new MensajeResponse(e.getMessage())).build();
        }
    }

    // VER GRUPO Y MIEMBROS
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response verGrupo(@PathParam("id") Integer idGrupo) {
        try {
            GrupoResponse grupo = grupoService.obtenerDetalleGrupo(idGrupo);
            return Response.ok(grupo).build();
        } catch (Exception e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new MensajeResponse(e.getMessage())).build();
        }
    }

    //AGREGAR MIEMBRO
    @POST
    @Path("/{id}/miembros")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response agregarMiembro(@PathParam("id") Integer idGrupo, @FormParam("idUsuario") Integer idUsuarioNuevo) {
        try {
            grupoService.invitarUsuario(idGrupo, idUsuarioNuevo);
            return Response.ok(new MensajeResponse("Usuario agregado al grupo.")).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new MensajeResponse(e.getMessage())).build();
        }
    }

    // ELIMINAR MIEMBRO (O SALIRSE)
    @DELETE
    @Path("/{id}/miembros/{idUsuario}")
    public Response eliminarMiembro(
            @PathParam("id") Integer idGrupo,
            @PathParam("idUsuario") Integer idUsuarioAExpulsar,
            @QueryParam("idSolicitante") Integer idSolicitante // Quien hace la peticion
    ) {
        try {
            grupoService.expulsarOSalir(idSolicitante, idGrupo, idUsuarioAExpulsar);
            return Response.ok(new MensajeResponse("Miembro eliminado del grupo.")).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new MensajeResponse(e.getMessage())).build();
        }
    }

    // ELIMINAR GRUPO COMPLETO
    @DELETE
    @Path("/{id}")
    public Response borrarGrupo(@PathParam("id") Integer idGrupo, @QueryParam("idUsuario") Integer idUsuario) {
        try {
            grupoService.eliminarGrupo(idUsuario, idGrupo);
            return Response.ok(new MensajeResponse("Grupo familiar eliminado.")).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new MensajeResponse(e.getMessage())).build();
        }
    }
}