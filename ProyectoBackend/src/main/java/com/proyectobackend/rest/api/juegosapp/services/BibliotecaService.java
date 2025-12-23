package com.proyectobackend.rest.api.juegosapp.services;

import com.proyectobackend.rest.api.juegosapp.dtos.juego.JuegoResponse;
import com.proyectobackend.rest.api.juegosapp.repositories.BibliotecaRepository;

import java.util.List;

public class BibliotecaService {
    private final BibliotecaRepository bibliotecaRepository;

    public BibliotecaService() {
        this.bibliotecaRepository = new BibliotecaRepository();
    }

    public List<JuegoResponse> obtenerMisJuegos(int idUsuario) throws Exception {
        return bibliotecaRepository.obtenerBibliotecaUsuario(idUsuario);
    }
}
