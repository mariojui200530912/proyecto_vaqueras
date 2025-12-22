/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.proyectobackend.rest.api.juegosapp.resources;

import com.proyectobackend.rest.api.juegosapp.dtos.MensajeResponse;
import com.proyectobackend.rest.api.juegosapp.dtos.login.LoginRequest;
import com.proyectobackend.rest.api.juegosapp.dtos.usuario.CambiarPasswordRequest;
import com.proyectobackend.rest.api.juegosapp.dtos.usuario.UsuarioEstadoRequest;
import com.proyectobackend.rest.api.juegosapp.dtos.usuario.UsuarioRequest;
import com.proyectobackend.rest.api.juegosapp.dtos.usuario.UsuarioResponse;
import com.proyectobackend.rest.api.juegosapp.models.Usuario;
import com.proyectobackend.rest.api.juegosapp.models.enums.EstadoUsuario;
import com.proyectobackend.rest.api.juegosapp.models.enums.Rol;
import com.proyectobackend.rest.api.juegosapp.services.UsuarioService;
import com.proyectobackend.rest.api.juegosapp.utils.FileUploadUtil;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.List;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

@Path("/usuario")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UsuarioResource {

    private final UsuarioService usuarioService;

    public UsuarioResource() {
        this.usuarioService = new UsuarioService();
    }

    @POST
    @Path("/registro")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response registrarUsuario(
            @FormDataParam("nickname") String nickname,
            @FormDataParam("password") String password,
            @FormDataParam("email") String email,
            @FormDataParam("fechaNacimiento") String fechaNacimiento,
            @FormDataParam("telefono") String telefono,
            @FormDataParam("pais") String pais,
            @FormDataParam("avatar") InputStream avatarInput,
            @FormDataParam("avatar") FormDataContentDisposition fileDetail
    ) {
        try {

            UsuarioRequest request = new UsuarioRequest();
            request.setNickname(nickname);
            request.setPassword(password);
            request.setEmail(email);
            request.setFechaNacimiento(LocalDate.parse(fechaNacimiento));
            request.setTelefono(telefono);
            request.setPais(pais);

            // Guardar imagen si se envía
            if (avatarInput != null) {
                request.setAvatar(FileUploadUtil.leerBytesDeInput(avatarInput));
            }

            request.setRol(Rol.valueOf("GAMER"));

            UsuarioResponse usuario = usuarioService.registrarUsuario(request);
            return Response.status(Response.Status.CREATED).entity(usuario).build();

        } catch (Exception e) {

            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new MensajeResponse("Error al registrar usuario: " + e.getMessage()))
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }
    }

    @GET
    public Response listarUsuarios() {
        try {
            List<UsuarioResponse> usuarios = usuarioService.listarUsuarios();
            return Response.ok(usuarios).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new MensajeResponse("Error al listar usuarios: " + e.getMessage()))
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }
    }

    @GET
    @Path("/{id}")
    public Response obtenerUsuario(@PathParam("id") Integer id) {
        try {
            UsuarioResponse usuario = usuarioService.obtenerUsuarioResponsePorId(id);
            return Response.ok(usuario).build();
        } catch (Exception e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new MensajeResponse(e.getMessage()))
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response actualizarPerfil(
            @PathParam("id") Integer id,
            @FormDataParam("nickname") String nickname,
            @FormDataParam("email") String email,
            @FormDataParam("fechaNacimiento") String fechaNacimientoStr,
            @FormDataParam("telefono") String telefono,
            @FormDataParam("pais") String pais,
            @FormDataParam("rol") String rol, // solo admin puede cambiar
            @FormDataParam("estado") String estado, // solo admin puede cambiar
            @FormDataParam("avatar") InputStream avatarInput,
            @FormDataParam("avatar") FormDataContentDisposition fileDetail
    ) {
            Usuario usuarioActual = usuarioService.obtenerUsuarioPorId(id);

        try {
            if (usuarioActual == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(new MensajeResponse("Usuario no encontrado."))
                        .build();
            }

            // Convertir fecha (si se envía)
            LocalDate fechaNacimiento = usuarioActual.getFechaNacimiento();
            if (fechaNacimientoStr != null) {
                fechaNacimiento = LocalDate.parse(fechaNacimientoStr);
            }

            // Construir request manteniendo valores actuales cuando no se envíen
            UsuarioRequest request = new UsuarioRequest();
            request.setNickname(nickname != null ? nickname : usuarioActual.getNickname());
            request.setPassword(usuarioActual.getPassword());
            request.setEmail(email != null ? email : usuarioActual.getEmail());
            request.setFechaNacimiento(fechaNacimiento);
            request.setTelefono(telefono != null ? telefono : usuarioActual.getTelefono());
            request.setPais(pais != null ? pais : usuarioActual.getPais());
            // Guardar nueva imagen solo si se envía
            if (avatarInput != null && fileDetail != null && fileDetail.getFileName() != null && !fileDetail.getFileName().isEmpty()) {
                request.setAvatar(FileUploadUtil.leerBytesDeInput(avatarInput));
            }

            // Solo admin puede modificar tipo y estado; si no vienen, conservar
            if (rol != null && !rol.isEmpty()) {
                request.setRol(Rol.valueOf(rol));
            } else {
                request.setRol(usuarioActual.getRol());
            }

            if (estado != null && !estado.isEmpty()) {
                request.setEstado(EstadoUsuario.valueOf(estado));
            } else {
                request.setEstado(usuarioActual.getEstado());
            }

            // Llamar servicio para actualizar (el servicio debe respetar null en password => no cambiar)
            UsuarioResponse usuarioActualizado = usuarioService.actualizarPerfil(id, request);
            return Response.ok(usuarioActualizado).build();

        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new MensajeResponse("Error al actualizar perfil: " + e.getMessage()))
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }
    }

    @PUT
    @Path("/{id}/password")
    public Response cambiarPassword(@PathParam("id") Integer id, CambiarPasswordRequest request) {
        try {
            MensajeResponse response = usuarioService.cambiarPassword(id, request);
            return Response.ok(response).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new MensajeResponse(e.getMessage()))
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }
    }

    @PUT
    @Path("/{id}/estado")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response cambiarEstadoUsuario(@PathParam("id") Integer id, UsuarioEstadoRequest request) {
        try {
            // Validamos que envíen un estado
            if (request.getEstado() == null || request.getEstado().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new MensajeResponse("Debe proporcionar el nuevo estado (ACTIVO o BLOQUEADO)."))
                        .build();
            }

            // Validamos que el estado exista en el Enum
            EstadoUsuario nuevoEstado;
            try {
                nuevoEstado = EstadoUsuario.valueOf(request.getEstado().toUpperCase());
            } catch (IllegalArgumentException e) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new MensajeResponse("Estado inválido. Valores permitidos: ACTIVO, BLOQUEADO"))
                        .build();
            }

            // Llamamos al servicio (Explicado abajo)
            usuarioService.cambiarEstado(id, nuevoEstado);

            return Response.ok(new MensajeResponse("El estado del usuario ha sido actualizado a: " + nuevoEstado))
                    .build();

        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new MensajeResponse("Error al cambiar estado: " + e.getMessage()))
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response eliminarUsuario(@PathParam("id") Integer id) {
        try {
            usuarioService.eliminarUsuario(id);
            return Response.ok(new MensajeResponse("Usuario eliminado correctamente")).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new MensajeResponse("Error al eliminar usuario: " + e.getMessage()))
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }
    }

    @POST
    @Path("/login")
    public Response login(LoginRequest request) {
        try {
            UsuarioResponse response = usuarioService.login(request);
            return Response.ok(response).build();
        } catch (Exception e) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(new MensajeResponse(e.getMessage()))
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }
    }

}


