package com.proyectobackend.rest.api.juegosapp.resources;

import com.proyectobackend.rest.api.juegosapp.dtos.MensajeResponse;
import com.proyectobackend.rest.api.juegosapp.dtos.biblioteca.BibliotecaResponse;
import com.proyectobackend.rest.api.juegosapp.dtos.prestamo.PrestamoRequest;
import com.proyectobackend.rest.api.juegosapp.dtos.prestamo.PrestamoResponse;
import com.proyectobackend.rest.api.juegosapp.services.PrestamoService;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/prestamo")
public class PrestamoResource {
    private final PrestamoService prestamoService = new PrestamoService();

    // POST: Solicitar
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response solicitarJuego(@QueryParam("idUsuario") Integer idBeneficiario, PrestamoRequest req) {
        try {
            prestamoService.solicitarPrestamo(idBeneficiario, req.getIdJuego(), req.getIdDueno());
            return Response.status(Response.Status.CREATED)
                    .entity(new MensajeResponse("Juego agregado a tu lista (NO INSTALADO).")).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new MensajeResponse(e.getMessage())).build();
        }
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response verJuegosPrestados(@PathParam("id") Integer id) {
        try {

            int idUsuarioLogueado = id;

            List<PrestamoResponse> misJuegos = prestamoService.obtenerJuegosPrestados(idUsuarioLogueado);
            return Response.ok(misJuegos).build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new MensajeResponse("Error: " + e.getMessage()))
                    .build();
        }
    }

    // PUT: Instalar (Activa la restricción de 1 a la vez)
    @PUT
    @Path("/{id}/instalar")
    @Produces(MediaType.APPLICATION_JSON)
    public Response instalarJuego(
            @PathParam("id") Integer idPrestamo,
            @QueryParam("idUsuario") Integer idUsuario
    ) {
        try {
            prestamoService.instalarJuego(idPrestamo, idUsuario);
            return Response.ok(new MensajeResponse("Juego instalado y listo para jugar.")).build();
        } catch (Exception e) {
            return Response.status(Response.Status.CONFLICT) // 409 Conflict es adecuado aquí
                    .entity(new MensajeResponse("Error: " + e.getMessage())).build();
        }
    }

    // PUT: Desinstalar (Libera el cupo)
    @PUT
    @Path("/{id}/desinstalar")
    @Produces(MediaType.APPLICATION_JSON)
    public Response desinstalarJuego(
            @PathParam("id") Integer idPrestamo,
            @QueryParam("idUsuario") Integer idUsuario
    ) {
        try {
            prestamoService.desinstalarJuego(idPrestamo, idUsuario);
            return Response.ok(new MensajeResponse("Juego desinstalado. Ahora puedes instalar otro.")).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new MensajeResponse(e.getMessage())).build();
        }
    }

    // DELETE: Devolver (Elimina de la lista)
    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response devolverJuego(@PathParam("id") Integer idPrestamo, @QueryParam("idUsuario") Integer idBeneficiario) {
        try {
            prestamoService.devolverJuego(idPrestamo, idBeneficiario);
            return Response.ok(new MensajeResponse("Has devuelto el juego.")).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(new MensajeResponse(e.getMessage())).build();
        }
    }
}