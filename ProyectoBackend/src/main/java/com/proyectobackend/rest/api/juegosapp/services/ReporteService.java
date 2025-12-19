package com.proyectobackend.rest.api.juegosapp.services;

import com.proyectobackend.rest.api.juegosapp.repositories.DBConnection;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;

import java.io.InputStream;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

public class ReporteService {
    // REPORTES ADMIN
    // 1. Ganancias Globales
    public byte[] reporteGanancias(String fechaInicio, String fechaFin) throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("FECHA_INICIO", fechaInicio);
        params.put("FECHA_FIN", fechaFin);
        return generarPDF("GananciasGlobales.jasper", params);
    }

    // 2. Top Ventas (Con filtro opcional de categoría)
    public byte[] reporteTopVentas(String fechaInicio, String fechaFin, Integer idCategoria) throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("FECHA_INICIO", fechaInicio);
        params.put("FECHA_FIN", fechaFin);
        // Jasper maneja nulls en SQL si la query está bien hecha
        params.put("ID_CATEGORIA", idCategoria);
        return generarPDF("TopVentas.jasper", params);
    }

    // 3. Ingresos por Empresa
    public byte[] reporteEmpresas(String fechaInicio, String fechaFin) throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("FECHA_INICIO", fechaInicio);
        params.put("FECHA_FIN", fechaFin);
        return generarPDF("IngresosEmpresa.jasper", params);
    }

    // 4. Ranking Usuarios
    public byte[] reporteUsuarios() throws Exception {
        // Este reporte quizás no necesita filtros de fecha, es histórico
        Map<String, Object> params = new HashMap<>();
        return generarPDF("RankingUsuarios.jasper", params);
    }

    // REPORTES EMPRESAS
    // 1. Reporte de Ventas Propias
    public byte[] reporteVentasPropias(int idEmpresa, String fechaInicio, String fechaFin) throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("ID_EMPRESA", idEmpresa);
        params.put("FECHA_INICIO", fechaInicio);
        params.put("FECHA_FIN", fechaFin);
        return generarPDF("VentasPropias.jasper", params);
    }

    // 2. Reporte de Feedback (Calidad y Peores comentarios para análisis)
    public byte[] reporteFeedback(int idEmpresa) throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("ID_EMPRESA", idEmpresa);
        return generarPDF("FeedbackEmpresa.jasper", params);
    }

    // 3. TOP 5 Juegos de la Empresa
    public byte[] reporteTop5Empresa(int idEmpresa, String fechaInicio, String fechaFin) throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("ID_EMPRESA", idEmpresa);
        params.put("FECHA_INICIO", fechaInicio);
        params.put("FECHA_FIN", fechaFin);
        return generarPDF("Top5Empresa.jasper", params);
    }

    // Método Genérico Privado para evitar repetir código
    private byte[] generarPDF(String nombreArchivoJasper, Map<String, Object> params) throws Exception {
        try(Connection conn = DBConnection.getInstance().getConnection()) {
            InputStream reportStream = getClass().getResourceAsStream("/reportes/" + nombreArchivoJasper);
            if (reportStream == null) throw new Exception("Reporte no encontrado: " + nombreArchivoJasper);

            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(reportStream);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, conn);

            return JasperExportManager.exportReportToPdf(jasperPrint);
        } catch(Exception ex) {
            throw new Exception("Existe un problema al general el reporte: " + ex.getMessage());
        }
    }
}
