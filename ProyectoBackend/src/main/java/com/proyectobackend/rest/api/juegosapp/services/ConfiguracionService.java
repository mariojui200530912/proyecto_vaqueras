package com.proyectobackend.rest.api.juegosapp.services;

import com.proyectobackend.rest.api.juegosapp.dtos.MensajeResponse;
import com.proyectobackend.rest.api.juegosapp.dtos.configuracion.Comision;
import com.proyectobackend.rest.api.juegosapp.repositories.ConfiguracionRepository;
import com.proyectobackend.rest.api.juegosapp.repositories.DBConnection;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;

public class ConfiguracionService {
    private final ConfiguracionRepository configRepo;

    public ConfiguracionService() {
        this.configRepo = new ConfiguracionRepository();
    }

    public Comision obtenerComision() {
        try(Connection conn = DBConnection.getInstance().getConnection()) {
            Comision comision = new Comision();
            comision.setComision(configRepo.obtenerComisionGlobal());
            return comision;
        } catch (Exception e){
            throw new RuntimeException("Error al obtener comision" + e.getMessage());
        }
    }

    public MensajeResponse cambiarComisionGlobal(Comision nuevaComision) throws Exception {

        try (Connection conn = DBConnection.getInstance().getConnection()) {
            // Validaciones básicas
            if (nuevaComision.getComision().compareTo(BigDecimal.ZERO) < 0 || nuevaComision.getComision().compareTo(new BigDecimal("100")) > 0) {
                throw new Exception("La comisión debe estar entre 0% y 100%");
            }

            conn.setAutoCommit(false); // INICIO TRANSACCIÓN
            try {
                // Actualizar la regla global
                configRepo.actualizarValorGlobal(conn, nuevaComision.getComision());

                // Ajustar automáticamente a las empresas que violan la nueva regla
                int empresasAfectadas = configRepo.recortarComisionesExcedidas(conn, nuevaComision.getComision());

                conn.commit(); // COMMIT

                // Preparamos un mensaje informativo
                String mensaje = "Comisión Global actualizada al " + nuevaComision + "%.";
                if (empresasAfectadas > 0) {
                    mensaje += " Se ajustaron automáticamente " + empresasAfectadas + " empresas que excedían este nuevo límite.";
                }

                return new MensajeResponse(mensaje);

            } catch (Exception e) {
                if (conn != null) {
                    try {
                        conn.rollback();
                    } catch (SQLException ex) {
                        throw new Exception("Error al actualizar comision " + nuevaComision, e);
                    }
                }
                throw new Exception("Error al actualizar configuración: " + e.getMessage());
            }
        }
    }
}
