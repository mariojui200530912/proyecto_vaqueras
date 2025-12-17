package com.proyectobackend.rest.api.juegosapp.resources;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.proyectobackend.rest.api.juegosapp.dtos.MensajeResponse;
import com.proyectobackend.rest.api.juegosapp.dtos.empresa.EmpresaRequest;
import com.proyectobackend.rest.api.juegosapp.dtos.empresa.EmpresaResponse;
import com.proyectobackend.rest.api.juegosapp.services.EmpresaService;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import java.io.InputStream;
import java.util.List;

@Path("/empresa")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class EmpresaResource {
    private final EmpresaService empresaService;
    private final ObjectMapper objectMapper;

    public EmpresaResource() {
        this.empresaService = new EmpresaService();
        this.objectMapper = new ObjectMapper();
    }

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response crearEmpresa(
            @FormDataParam("datos") String jsonDatos,
            @FormDataParam("logo") InputStream logoInput,
            @FormDataParam("logo") FormDataContentDisposition fileDetail
    ) {
        try {
            EmpresaRequest request = objectMapper.readValue(jsonDatos, EmpresaRequest.class);
            String fileName = (fileDetail != null) ? fileDetail.getFileName() : null;

            EmpresaResponse creada = empresaService.crearEmpresa(request, logoInput, fileName);
            return Response.status(Response.Status.CREATED).entity(creada).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new MensajeResponse("Error: " + e.getMessage())).build();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response listarEmpresas() {
        try {
            List<EmpresaResponse> lista = empresaService.listarEmpresas();
            return Response.ok(lista).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new MensajeResponse("Error: " + e.getMessage())).build();
        }
    }

    @GET
    @Path("/{id}")
    public Response obtenerEmpresa(@PathParam("id") int id) {
        try {
            EmpresaResponse emp = empresaService.obtenerPorId(id);
            return Response.ok(emp).build();
        } catch (Exception e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new MensajeResponse(e.getMessage())).build();
        }
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response actualizarEmpresa(
            @PathParam("id") int id,
            @FormDataParam("datos") String jsonDatos,
            @FormDataParam("logo") InputStream logoInput,
            @FormDataParam("logo") FormDataContentDisposition fileDetail
    ) {
        try {
            EmpresaRequest request = objectMapper.readValue(jsonDatos, EmpresaRequest.class);
            String fileName = (fileDetail != null) ? fileDetail.getFileName() : null;

            EmpresaResponse actualizada = empresaService.actualizarEmpresa(id, request, logoInput, fileName);
            return Response.ok(actualizada).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new MensajeResponse("Error: " + e.getMessage())).build();
        }
    }

    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response eliminarEmpresa(@PathParam("id") int id) {
        try {
            empresaService.eliminarEmpresa(id);
            return Response.ok(new MensajeResponse("Empresa eliminada correctamente")).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new MensajeResponse("Error: " + e.getMessage())).build();
        }
    }
}