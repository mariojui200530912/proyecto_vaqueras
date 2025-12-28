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
    public byte[] reporteTopVentas(String fechaInicio, String fechaFin, Integer idCategoria, String clasificacion) throws Exception {

        Map<String, Object> params = new HashMap<>();
        // Parámetros obligatorios
        params.put("FECHA_INICIO", fechaInicio);
        params.put("FECHA_FIN", fechaFin);
        // Parámetros opcionales (Jasper maneja nulls en SQL con la lógica que pusimos)
        params.put("ID_CATEGORIA", idCategoria); // Puede ser null
        params.put("CLASIFICACION", clasificacion); // Puede ser null (ej: "M", "T", "E")

        return generarPDF("top_balanceado.jasper", params);
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

    // REPORTES USUARIO
    // 1. Historial de Gastos
    public byte[] reporteGastosUsuario(int idUsuario) throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("ID_USUARIO", idUsuario);
        params.put("TITULO_REPORTE", "Historial de Gastos");
        params.put("NOMBRE_USUARIO", "ID: " + idUsuario); // O busca el nombre real si prefieres

        return generarPDF("HistorialGastos.jasper", params);
    }

    // 2. Análisis de Biblioteca (Ratings Comunidad vs Personal)
    public byte[] reporteBibliotecaUsuario(int idUsuario) throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("ID_USUARIO", idUsuario);
        params.put("TITULO_REPORTE", "Análisis de Biblioteca");
        params.put("NOMBRE_USUARIO", "ID: " + idUsuario);

        return generarPDF("AnalisisBiblioteca.jasper", params);
    }

    // 3. Historial de Préstamos Familiares
    public byte[] reportePrestamosUsuario(int idUsuario) throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("ID_USUARIO", idUsuario);
        params.put("TITULO_REPORTE", "Historial de Préstamos");
        params.put("NOMBRE_USUARIO", "ID: " + idUsuario);

        return generarPDF("HistorialPrestamos.jasper", params);
    }

    // Metodo Genérico Privado para evitar repetir código
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
