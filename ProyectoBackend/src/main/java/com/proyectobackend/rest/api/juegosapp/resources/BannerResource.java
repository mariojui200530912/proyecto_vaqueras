package com.proyectobackend.rest.api.juegosapp.resources;

import com.proyectobackend.rest.api.juegosapp.dtos.BannerResponse;
import com.proyectobackend.rest.api.juegosapp.dtos.MensajeResponse;
import com.proyectobackend.rest.api.juegosapp.services.BannerService;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/banner")
public class BannerResource {
    private final BannerService bannerService = new BannerService();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response obtenerBanner() {
        try {
            List<BannerResponse> banner = bannerService.obtenerBannerPrincipal();
            return Response.ok(banner).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new MensajeResponse("Error al cargar banner: " + e.getMessage()))
                    .build();
        }
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response agregarAlBanner(@QueryParam("idJuego") Integer idJuego) {
        try {
            if (idJuego == null) throw new Exception("ID del juego es requerido.");

            bannerService.agregarJuegoAlBanner(idJuego);
            return Response.ok(new MensajeResponse("Juego agregado al banner principal.")).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new MensajeResponse("Error: " + e.getMessage())).build();
        }
    }

    // DELETE: Quitar un juego del banner
    // URL: /api/v1/admin/banner/50
    @DELETE
    @Path("/{idJuego}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response quitarDelBanner(@PathParam("idJuego") Integer idJuego) {
        try {
            bannerService.quitarJuegoDelBanner(idJuego);
            return Response.ok(new MensajeResponse("Juego eliminado del banner.")).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new MensajeResponse(e.getMessage())).build();
        }
    }

    // PUT: Reordenar (Opcional)
    @PUT
    @Path("/{idJuego}/orden")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response cambiarOrden(@PathParam("idJuego") Integer idJuego, @FormParam("orden") Integer nuevoOrden) {
        try {
            bannerService.cambiarPosicion(idJuego, nuevoOrden);
            return Response.ok(new MensajeResponse("Orden actualizado.")).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new MensajeResponse(e.getMessage())).build();
        }
    }
}