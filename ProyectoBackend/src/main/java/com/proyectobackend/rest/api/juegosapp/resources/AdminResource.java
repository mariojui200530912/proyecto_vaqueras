package com.proyectobackend.rest.api.juegosapp.resources;

import com.proyectobackend.rest.api.juegosapp.dtos.MensajeResponse;
import com.proyectobackend.rest.api.juegosapp.services.AdminService;
import com.proyectobackend.rest.api.juegosapp.services.ConfiguracionService;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.math.BigDecimal;

@Path("/admin")
public class AdminResource {
    private final ConfiguracionService configService;
    private final AdminService adminService;

    public AdminResource() {
        this.configService = new ConfiguracionService();
        this.adminService = new AdminService();
    }

    @POST
    @Path("/comision-global")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response actualizarComisionGlobal(@FormParam("comision") BigDecimal nuevaComision) {
        try {
            if (nuevaComision == null) {
                throw new Exception("El valor de la comisión es requerido.");
            }

            MensajeResponse respuesta = configService.cambiarComisionGlobal(nuevaComision);

            return Response.ok(respuesta).build();

        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new MensajeResponse(e.getMessage()))
                    .build();
        }
    }

    @GET
    @Path("/stats")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDashboardStats() {
        try {
            return Response.ok(adminService.getStats()).build();
        } catch (Exception e) {
            return Response.serverError().build();
        }
    }
}