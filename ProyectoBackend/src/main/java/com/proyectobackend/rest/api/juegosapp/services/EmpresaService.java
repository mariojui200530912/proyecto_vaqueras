package com.proyectobackend.rest.api.juegosapp.services;

import com.proyectobackend.rest.api.juegosapp.dtos.MensajeResponse;
import com.proyectobackend.rest.api.juegosapp.dtos.empresa.EmpresaRequest;
import com.proyectobackend.rest.api.juegosapp.dtos.empresa.EmpresaResponse;
import com.proyectobackend.rest.api.juegosapp.exceptions.EntityAlReadyExistException;
import com.proyectobackend.rest.api.juegosapp.models.Empresa;
import com.proyectobackend.rest.api.juegosapp.models.Usuario;
import com.proyectobackend.rest.api.juegosapp.repositories.EmpresaRepository;
import com.proyectobackend.rest.api.juegosapp.utils.FileUploadUtil;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EmpresaService {
    private final EmpresaRepository empresaRepository;

    public EmpresaService() {
        this.empresaRepository = new EmpresaRepository();
    }

    public EmpresaResponse crearEmpresa(EmpresaRequest request, InputStream logoInput, String fileName) throws Exception {
        // Subir logo
        String rutaLogo = null;
        try {
            // Validaciones básicas
            if (!request.isValid()) {
                throw new Exception("Datos de creacion de empresa no validos");
            }

            // Verificar si el email ya existe
            Optional<Empresa> empresaExistente = Optional.ofNullable(empresaRepository.buscarPorNombre(request.getNombre()));
            if (empresaExistente.isPresent()) {
                throw new EntityAlReadyExistException("El nombre de empresa ya está registrado.");
            }


            Empresa empresa = new Empresa();
            empresa.setNombre(request.getNombre());
            empresa.setDescripcion(request.getDescripcion());
            if (logoInput != null && fileName != null && !fileName.isEmpty()) {
                empresa.setLogo(FileUploadUtil.leerBytesDeInput(logoInput));
            }
            empresa.setComisionEspecifica(request.getComisionEspecifica());
            empresa.setFecha_creacion(LocalDateTime.now());
            // Default a TRUE si viene null
            empresa.setPermiteComentarios(request.getPermiteComentarios() != null ? request.getPermiteComentarios() : true);

            Empresa guardada = empresaRepository.crear(empresa);
            return mapToResponse(guardada);
        }catch (Exception e) {
            throw e;
        }
    }

    public List<EmpresaResponse> listarEmpresas() throws Exception {
        List<Empresa> lista = empresaRepository.listar();
        List<EmpresaResponse> response = new ArrayList<>();
        for (Empresa e : lista) {
            response.add(mapToResponse(e));
        }
        return response;
    }

    public EmpresaResponse obtenerPorId(int id) throws Exception {
        Empresa emp = empresaRepository.buscarPorId(id).orElseThrow(() -> new Exception("Empresa no encontrada"));
        return mapToResponse(emp);
    }

    public EmpresaResponse actualizarEmpresa(int id, EmpresaRequest request, InputStream logoInput, String fileName) throws Exception {
        Optional<Empresa> opt = empresaRepository.buscarPorId(id);
        if (!opt.isPresent()) throw new Exception("Empresa no encontrada");

        Empresa actual = opt.get();

        // MERGE: Solo actualizamos si viene dato nuevo
        if (request.getNombre() != null) actual.setNombre(request.getNombre());
        if (request.getDescripcion() != null) actual.setDescripcion(request.getDescripcion());
        if (request.getComisionEspecifica() != null) actual.setComisionEspecifica(request.getComisionEspecifica());
        if (request.getPermiteComentarios() != null) actual.setPermiteComentarios(request.getPermiteComentarios());

        // Manejo de Logo Nuevo
        if (logoInput != null) {
            byte[] nuevoLogo = FileUploadUtil.leerBytesDeInput(logoInput);
            if (nuevoLogo != null && nuevoLogo.length > 0) {
                actual.setLogo(nuevoLogo);
            }
        }

        boolean exito = empresaRepository.actualizar(actual);
        if (!exito) throw new Exception("Error al actualizar empresa en BD");

        return mapToResponse(actual);
    }

    public void eliminarEmpresa(int id) throws Exception {
        // recordatorio validar si la empresa tiene juegos para borrar los juegos
        boolean exito = empresaRepository.eliminar(id);
        if (!exito) throw new Exception("No se pudo eliminar (posiblemente tiene registros asociados)");
    }

    private EmpresaResponse mapToResponse(Empresa e) {
        EmpresaResponse resp = new EmpresaResponse();
        resp.setId(e.getId());
        resp.setNombre(e.getNombre());
        resp.setDescripcion(e.getDescripcion());
        if (e.getLogo() != null && e.getLogo().length > 0) {
            String base64 = java.util.Base64.getEncoder().encodeToString(e.getLogo());
            resp.setLogo(base64);
        }
        resp.setComisionEspecifica(e.getComisionEspecifica());
        resp.setFechaCreacion(e.getFecha_creacion());
        resp.setPermiteComentarios(e.getPermiteComentarios());
        return resp;
    }
}
