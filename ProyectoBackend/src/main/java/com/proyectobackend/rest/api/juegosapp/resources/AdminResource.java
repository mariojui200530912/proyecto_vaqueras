package com.proyectobackend.rest.api.juegosapp.resources;

import com.proyectobackend.rest.api.juegosapp.dtos.MensajeResponse;
import com.proyectobackend.rest.api.juegosapp.dtos.configuracion.Comision;
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

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response obtenerComision(){
        try{
            Comision comision = configService.obtenerComision();
            return Response.status(Response.Status.OK).entity(comision).build();
        }catch(Exception ex){
            return Response.status(Response.Status.BAD_REQUEST).entity(new MensajeResponse(ex.getMessage())).build();
        }
    }

    @POST
    @Path("/comision-global")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response actualizarComisionGlobal(Comision comision) {
        try {
            MensajeResponse respuesta = configService.cambiarComisionGlobal(comision);
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