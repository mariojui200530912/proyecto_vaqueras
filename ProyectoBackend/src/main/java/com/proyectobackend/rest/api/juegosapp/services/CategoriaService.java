package com.proyectobackend.rest.api.juegosapp.services;

import com.proyectobackend.rest.api.juegosapp.dtos.categoria.CategoriaRequest;
import com.proyectobackend.rest.api.juegosapp.dtos.categoria.CategoriaResponse;
import com.proyectobackend.rest.api.juegosapp.models.Categoria;
import com.proyectobackend.rest.api.juegosapp.repositories.CategoriaRepository;

import java.util.ArrayList;
import java.util.List;

public class CategoriaService {
    private final CategoriaRepository categoriaRepository;

    public CategoriaService() {
        this.categoriaRepository = new CategoriaRepository();
    }

    public CategoriaResponse crearCategoria(CategoriaRequest request) throws Exception {
        if (request.getNombre() == null || request.getNombre().trim().isEmpty()) {
            throw new Exception("El nombre de la categoría es obligatorio.");
        }

        if (categoriaRepository.existeNombre(request.getNombre().trim())) {
            throw new Exception("Ya existe una categoría con ese nombre.");
        }

        Categoria cat = new Categoria();
        cat.setNombre(request.getNombre().trim());
        cat.setDescripcion(request.getDescripcion());

        Categoria guardada = categoriaRepository.crear(cat);
        return mapToResponse(guardada);
    }

    public List<CategoriaResponse> listarCategorias() throws Exception {
        List<Categoria> lista = categoriaRepository.listar();
        List<CategoriaResponse> response = new ArrayList<>();
        for (Categoria c : lista) {
            response.add(mapToResponse(c));
        }
        return response;
    }

    public CategoriaResponse actualizarCategoria(int id, CategoriaRequest request) throws Exception {
        Categoria catActual = categoriaRepository.buscarPorId(id)
                .orElseThrow(() -> new Exception("Categoría no encontrada."));

        // Si cambia el nombre, verificar que no choque con otra existente
        if (request.getNombre() != null && !request.getNombre().trim().equals(catActual.getNombre())) {
            if (categoriaRepository.existeNombre(request.getNombre().trim())) {
                throw new Exception("Ya existe otra categoría con ese nombre.");
            }
            catActual.setNombre(request.getNombre().trim());
        }

        if (request.getDescripcion() != null) {
            catActual.setDescripcion(request.getDescripcion());
        }

        if (categoriaRepository.actualizar(catActual)) {
            return mapToResponse(catActual);
        } else {
            throw new Exception("Error al actualizar la categoría.");
        }
    }

    public void eliminarCategoria(int id) throws Exception {
        // Nota: Esto fallará si hay juegos usando esta categoría (por FK)
        // Deberías capturar la SQLException específica en el Resource o manejarla aquí.
        if (!categoriaRepository.eliminar(id)) {
            throw new Exception("No se pudo eliminar la categoría (posiblemente esté en uso).");
        }
    }

    private CategoriaResponse mapToResponse(Categoria c) {
        CategoriaResponse resp = new CategoriaResponse();
        resp.setId(c.getId());
        resp.setNombre(c.getNombre());
        resp.setDescripcion(c.getDescripcion());
        return resp;
    }
}
