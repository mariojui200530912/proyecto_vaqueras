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
    // REPORTES ADMIN
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
    @Path("/top-calidad")
    @Produces("application/pdf")
    public Response getTopVentasCalidad(
            @QueryParam("inicio") String inicio,
            @QueryParam("fin") String fin,
            @QueryParam("categoria") Integer idCategoria, // Opcional
            @QueryParam("clasificacion") String clasificacion // Opcional
    ) {
        try {
            // Validar fechas obligatorias
            if (inicio == null || fin == null) {
                return Response.status(400).entity("Fechas requeridas").build();
            }

            byte[] pdf = reporteService.reporteTopVentas(inicio, fin, idCategoria, clasificacion);

            return Response.ok(pdf)
                    .header("Content-Disposition", "inline; filename=top_ventas_calidad.pdf")
                    .build();
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
    // REPORTES EMPRESAS
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
    // REPORTES USUARIO
    @GET
    @Path("/usuario/gastos")
    public Response getGastosUsuario(@QueryParam("idUsuario") Integer idUsuario) {
        try {
            if (idUsuario == null) return Response.status(400).entity("ID Usuario requerido").build();

            return construirRespuestaPDF(
                    reporteService.reporteGastosUsuario(idUsuario),
                    "historial_gastos.pdf"
            );
        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/usuario/biblioteca")
    public Response getBibliotecaUsuario(@QueryParam("idUsuario") Integer idUsuario) {
        try {
            if (idUsuario == null) return Response.status(400).entity("ID Usuario requerido").build();

            return construirRespuestaPDF(
                    reporteService.reporteBibliotecaUsuario(idUsuario),
                    "analisis_biblioteca.pdf"
            );
        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/usuario/prestamos")
    public Response getPrestamosUsuario(@QueryParam("idUsuario") Integer idUsuario) {
        try {
            if (idUsuario == null) return Response.status(400).entity("ID Usuario requerido").build();

            return construirRespuestaPDF(
                    reporteService.reportePrestamosUsuario(idUsuario),
                    "historial_prestamos.pdf"
            );
        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }
}