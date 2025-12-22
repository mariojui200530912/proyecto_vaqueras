package com.proyectobackend.rest.api.juegosapp.resources;

import com.proyectobackend.rest.api.juegosapp.dtos.MensajeResponse;
import com.proyectobackend.rest.api.juegosapp.dtos.billetera.RecargarRequest;
import com.proyectobackend.rest.api.juegosapp.dtos.billetera.TransaccionResponse;
import com.proyectobackend.rest.api.juegosapp.models.Transaccion;
import com.proyectobackend.rest.api.juegosapp.services.TransaccionService;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/transaccion")
public class TransaccionResource {
    private final TransaccionService transaccionService;

    public TransaccionResource() {
        this.transaccionService = new TransaccionService();
    }

    @POST
    @Path("/recarga")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response recargar(RecargarRequest request) {
        try {

            int idUsuarioLogueado = request.getIdUsuario();

            MensajeResponse respuesta = transaccionService.recargarSaldo(idUsuarioLogueado, request);
            return Response.ok(respuesta).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new MensajeResponse("Error: " + e.getMessage())).build();
        }
    }

    @GET
    @Path("/{id}/historial")
    @Produces(MediaType.APPLICATION_JSON)
    public Response verHistorial(@PathParam("id") Integer idUsuario) {
        try {
            int idUsuarioLogueado = idUsuario;

            List<TransaccionResponse> historial = transaccionService.obtenerHistorial(idUsuarioLogueado);
            return Response.ok(historial).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new MensajeResponse("Error: " + e.getMessage())).build();
        }
    }
}