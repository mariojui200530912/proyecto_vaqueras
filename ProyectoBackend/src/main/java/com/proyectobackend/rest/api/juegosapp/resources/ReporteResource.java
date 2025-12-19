package com.proyectobackend.rest.api.juegosapp.resources;

import com.proyectobackend.rest.api.juegosapp.services.ReporteService;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;

@Path("/reporte")
public class ReporteResource {
    private final ReporteService reporteService;

    public ReporteResource() {
        this.reporteService = new ReporteService();
    }

    @GET
    @Path("/ganancias")
    public Response getGanancias(@QueryParam("inicio") String inicio, @QueryParam("fin") String fin) {
        try {
            return construirRespuestaPDF(reporteService.reporteGanancias(inicio, fin), "ganancias.pdf");
        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/top-ventas")
    public Response getTopVentas(
            @QueryParam("inicio") String inicio,
            @QueryParam("fin") String fin,
            @QueryParam("categoria") Integer idCategoria // Opcional
    ) {
        try {
            return construirRespuestaPDF(reporteService.reporteTopVentas(inicio, fin, idCategoria), "top_ventas.pdf");
        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/empresas")
    public Response getIngresosEmpresas(@QueryParam("inicio") String inicio, @QueryParam("fin") String fin) {
        try {
            return construirRespuestaPDF(reporteService.reporteEmpresas(inicio, fin), "empresas.pdf");
        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/usuarios")
    public Response getRankingUsuarios() {
        try {
            return construirRespuestaPDF(reporteService.reporteUsuarios(), "ranking_usuarios.pdf");
        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/empresa/ventas")
    public Response getVentasPropias(
            @QueryParam("inicio") String inicio,
            @QueryParam("fin") String fin) {
        try {
            int idEmpresaLogueada = 1; // TODO: Obtener del Token
            return construirRespuestaPDF(reporteService.reporteVentasPropias(idEmpresaLogueada, inicio, fin), "mis_ventas.pdf");
        } catch (Exception e) { return Response.serverError().build(); }
    }

    @GET
    @Path("/empresa/feedback")
    public Response getFeedback() {
        try {
            int idEmpresaLogueada = 1;
            return construirRespuestaPDF(reporteService.reporteFeedback(idEmpresaLogueada), "calidad_feedback.pdf");
        } catch (Exception e) { return Response.serverError().build(); }
    }

    @GET
    @Path("/empresa/top5")
    public Response getTop5Empresa(@QueryParam("inicio") String inicio, @QueryParam("fin") String fin) {
        try {
            int idEmpresaLogueada = 1;
            return construirRespuestaPDF(reporteService.reporteTop5Empresa(idEmpresaLogueada, inicio, fin), "mi_top5.pdf");
        } catch (Exception e) { return Response.serverError().build(); }
    }

    private Response construirRespuestaPDF(byte[] bytes, String nombreArchivo) {
        return Response.ok(bytes)
                .type("application/pdf")
                .header("Content-Disposition", "inline; filename=" + nombreArchivo)
                .build();
    }
}