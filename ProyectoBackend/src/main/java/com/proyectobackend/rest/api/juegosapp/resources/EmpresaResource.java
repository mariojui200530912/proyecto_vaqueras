package com.proyectobackend.rest.api.juegosapp.resources;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.proyectobackend.rest.api.juegosapp.dtos.MensajeResponse;
import com.proyectobackend.rest.api.juegosapp.dtos.empresa.EmpresaComision;
import com.proyectobackend.rest.api.juegosapp.dtos.empresa.EmpresaRequest;
import com.proyectobackend.rest.api.juegosapp.dtos.empresa.EmpresaResponse;
import com.proyectobackend.rest.api.juegosapp.dtos.empresa.UsuarioEmpresa;
import com.proyectobackend.rest.api.juegosapp.dtos.usuario.UsuarioRol;
import com.proyectobackend.rest.api.juegosapp.services.EmpresaService;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import java.io.InputStream;
import java.math.BigDecimal;
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

    @PATCH // PATCH se usa para actualizaciones parciales
    @Path("/{id}/comision")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response cambiarComision(
            @PathParam("id") Integer idEmpresa,
            EmpresaComision comision
    ) {
        try {
            if (comision.getComision() == null) {
                throw new Exception("Debe enviar el valor de la comisión.");
            }

            empresaService.actualizarComisionEmpresa(idEmpresa, comision.getComision());

            return Response.ok(new MensajeResponse("Comisión de la empresa actualizada correctamente."))
                    .build();

        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new MensajeResponse("Error: " + e.getMessage())) // El mensaje dirá "no puede ser mayor..."
                    .build();
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

    // Vincular usuarios a empresa
    @POST
    @Path("/{id}/usuarios")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response agregarUsuario(
            @PathParam("id") Integer idEmpresa,
            UsuarioRol usuarioRol
    ) {
        try {
            if (usuarioRol == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new MensajeResponse("El ID del usuario es obligatorio."))
                        .build();
            }
            empresaService.vincularUsuario(idEmpresa, usuarioRol.getIdUsuario(), usuarioRol.getRolEmpresa());
            return Response.ok(new MensajeResponse("Usuario vinculado a la empresa exitosamente."))
                    .build();

        } catch (Exception e) {

            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new MensajeResponse(e.getMessage()))
                    .build();
        }
    }

    @GET
    @Path("/{id}/usuarios")
    @Produces(MediaType.APPLICATION_JSON)
    public Response listarUsuariosEmpresa(@PathParam("id") Integer idEmpresa) {
        try {
            List<UsuarioEmpresa> empleados = empresaService.obtenerEmpleados(idEmpresa);
            return Response.ok(empleados).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new MensajeResponse("Error: " + e.getMessage()))
                    .build();
        }
    }

    @DELETE
    @Path("/usuarios/{idUsuario}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response eliminarUsuarioDeEmpresa(
            @PathParam("idUsuario") Integer idUsuario
    ) {
        try {
            empresaService.eliminarEmpleado(idUsuario);
            return Response.ok(new MensajeResponse("Usuario desvinculado de la empresa correctamente."))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new MensajeResponse("Error: " + e.getMessage()))
                    .build();
        }
    }

    @PATCH
    @Path("/{id}/configuracion/comentarios")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response cambiarPermisoComentarios(
            @PathParam("id") Integer idEmpresa,
            @FormParam("permitir") Boolean permitir // true o false
    ) {
        try {
            if (permitir == null) {
                throw new Exception("Debe especificar si permite comentarios (true/false).");
            }

            empresaService.configurarComentarios(idEmpresa, permitir);

            String estado = permitir ? "HABILITADOS" : "DESHABILITADOS";
            return Response.ok(new MensajeResponse("Los comentarios han sido " + estado + " para esta empresa."))
                    .build();

        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new MensajeResponse("Error: " + e.getMessage()))
                    .build();
        }
    }
}