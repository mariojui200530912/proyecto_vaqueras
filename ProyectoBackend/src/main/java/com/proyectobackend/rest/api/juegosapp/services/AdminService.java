package com.proyectobackend.rest.api.juegosapp.services;

import com.proyectobackend.rest.api.juegosapp.dtos.dashboard.DashboardStats;
import com.proyectobackend.rest.api.juegosapp.repositories.AdminRepository;
import com.proyectobackend.rest.api.juegosapp.repositories.DBConnection;

import java.sql.Connection;

public class AdminService {
    private final AdminRepository adminRepo = new AdminRepository();

    public DashboardStats getStats() throws Exception {
        try (Connection conn = DBConnection.getInstance().getConnection()) {
            return adminRepo.obtenerEstadisticas(conn);
        }
    }
}
