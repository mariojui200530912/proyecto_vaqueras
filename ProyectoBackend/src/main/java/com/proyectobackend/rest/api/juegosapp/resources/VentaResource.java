package com.proyectobackend.rest.api.juegosapp.resources;

import com.proyectobackend.rest.api.juegosapp.dtos.MensajeResponse;
import com.proyectobackend.rest.api.juegosapp.dtos.juego.JuegoResponse;
import com.proyectobackend.rest.api.juegosapp.dtos.venta.VentaRequest;
import com.proyectobackend.rest.api.juegosapp.services.VentaService;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/venta")
public class VentaResource {
    private final VentaService ventaService;

    public VentaResource() {
        this.ventaService = new VentaService();
    }
    // Para comprar juego
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response procesarCompra(VentaRequest request) {
        try {
            MensajeResponse respuesta = ventaService.comprarJuego(request.getIdUsuario(), request.getIdJuego());
            return Response.ok(respuesta).build();

        } catch (Exception e) {
            // Devolver error 400 (Bad Request) si falla validación
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new MensajeResponse("Error: " + e.getMessage()))
                    .build();
        }
    }
}