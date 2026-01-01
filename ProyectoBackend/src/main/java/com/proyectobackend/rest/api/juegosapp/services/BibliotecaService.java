package com.proyectobackend.rest.api.juegosapp.services;

import com.proyectobackend.rest.api.juegosapp.dtos.biblioteca.BibliotecaResponse;
import com.proyectobackend.rest.api.juegosapp.dtos.juego.JuegoResponse;
import com.proyectobackend.rest.api.juegosapp.repositories.BibliotecaRepository;
import com.proyectobackend.rest.api.juegosapp.repositories.DBConnection;

import java.sql.Connection;
import java.util.List;

public class BibliotecaService {
    private final BibliotecaRepository bibliotecaRepository;

    public BibliotecaService() {
        this.bibliotecaRepository = new BibliotecaRepository();
    }

    public List<BibliotecaResponse> obtenerMisJuegos(int idUsuario) throws Exception {
        return bibliotecaRepository.obtenerBibliotecaUsuario(idUsuario);
    }

    public void cambiarEstadoInstalacion(int idUsuario, int idJuego, boolean instalar) throws Exception {
        if (instalar) {
            boolean exito = bibliotecaRepository.instalarJuego(idUsuario, idJuego);
            if (!exito) throw new Exception("No se pudo instalar. Verifica si tienes el juego.");
        } else {
            boolean exito = bibliotecaRepository.desinstalarJuego(idUsuario, idJuego);
            if (!exito) throw new Exception("No se pudo desinstalar.");
        }
    }

    public boolean verificarUsuarioTieneJuego(int idUsuario, int idJuego) throws Exception {
        try(Connection conn = DBConnection.getInstance().getConnection()){
            boolean tieneJuego = bibliotecaRepository.usuarioTieneJuego(conn, idUsuario, idJuego);
            return tieneJuego;
        } catch (Exception e) {
            throw new RuntimeException("Error al verificar si usuario tiene juego" + e.getMessage());
        }
    }
}
