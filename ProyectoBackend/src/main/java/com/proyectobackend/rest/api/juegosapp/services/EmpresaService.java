package com.proyectobackend.rest.api.juegosapp.services;

import com.proyectobackend.rest.api.juegosapp.dtos.MensajeResponse;
import com.proyectobackend.rest.api.juegosapp.dtos.empresa.EmpresaRequest;
import com.proyectobackend.rest.api.juegosapp.dtos.empresa.EmpresaResponse;
import com.proyectobackend.rest.api.juegosapp.dtos.empresa.UsuarioEmpresa;
import com.proyectobackend.rest.api.juegosapp.exceptions.EntityAlReadyExistException;
import com.proyectobackend.rest.api.juegosapp.models.Empresa;
import com.proyectobackend.rest.api.juegosapp.models.Usuario;
import com.proyectobackend.rest.api.juegosapp.models.enums.Rol;
import com.proyectobackend.rest.api.juegosapp.repositories.ConfiguracionRepository;
import com.proyectobackend.rest.api.juegosapp.repositories.DBConnection;
import com.proyectobackend.rest.api.juegosapp.repositories.EmpresaRepository;
import com.proyectobackend.rest.api.juegosapp.repositories.UsuarioRepository;
import com.proyectobackend.rest.api.juegosapp.utils.FileUploadUtil;

import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EmpresaService {
    private final EmpresaRepository empresaRepository;
    private final ConfiguracionRepository configRepository;
    private final UsuarioRepository usuarioRepository;

    public EmpresaService() {
        this.empresaRepository = new EmpresaRepository();
        this.configRepository = new ConfiguracionRepository();
        this.usuarioRepository = new UsuarioRepository();
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
        } catch (Exception e) {
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
        if (!request.isValidComisionEspecifica()) {
            throw new Exception("Datos de comision especifica de empresa no validos");
        }
        BigDecimal comisionGlobal = configRepository.obtenerComisionGlobal();
        Optional<Empresa> opt = empresaRepository.buscarPorId(id);
        if (!opt.isPresent()) throw new Exception("Empresa no encontrada");

        Empresa actual = opt.get();
        // MERGE: Solo actualizamos si viene dato nuevo
        if (request.getNombre() != null) actual.setNombre(request.getNombre());
        if (request.getDescripcion() != null) actual.setDescripcion(request.getDescripcion());
        if (request.getComisionEspecifica().compareTo(comisionGlobal) > 0) {
            throw new Exception("La comisión asignada (" + request.getComisionEspecifica() + "%) " +
                    "no puede ser mayor a la Comisión General de la Plataforma (" +
                    comisionGlobal + "%).");
        }
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

    public void actualizarComisionEmpresa(int idEmpresa, BigDecimal nuevaComision) throws Exception {
        // Obtener la Comisión General actual de la plataforma
        BigDecimal comisionGlobal = configRepository.obtenerComisionGlobal();

        // Validacion compareTo devuelve: 1 si es mayor, 0 si es igual, -1 si es menor
        if (nuevaComision.compareTo(comisionGlobal) > 0) {
            throw new Exception("La comisión asignada (" + nuevaComision + "%) " +
                    "no puede ser mayor a la Comisión General de la Plataforma (" +
                    comisionGlobal + "%).");
        }

        // Validación extra: No puede ser negativa
        if (nuevaComision.compareTo(BigDecimal.ZERO) < 0) {
            throw new Exception("La comisión no puede ser negativa.");
        }

        //Si pasa la validación, actualizamos
        empresaRepository.actualizarComision(idEmpresa, nuevaComision);
    }

    public void eliminarEmpresa(int id) throws Exception {
        // recordatorio validar si la empresa tiene juegos para borrar los juegos
        boolean exito = empresaRepository.eliminar(id);
        if (!exito) throw new Exception("No se pudo eliminar (posiblemente tiene registros asociados)");
    }

    public void vincularUsuario(int idEmpresa, int idUsuario, String rol) throws Exception {
        try (Connection conn = DBConnection.getInstance().getConnection()) {
            // Si el rol viene nulo, ponemos uno por defecto
            String rolFinal = (rol == null || rol.isEmpty()) ? "EMPLEADO" : rol.toUpperCase();
            conn.setAutoCommit(false);
            try {
                empresaRepository.agregarUsuarioAEmpresa(conn, idEmpresa, idUsuario, rolFinal);
                usuarioRepository.cambiarRol(conn, idUsuario, Rol.EMPRESA);
                conn.commit();
            }catch (Exception e) {
                conn.rollback();
                throw new Exception("Error al agregar empresa en BD");
            }
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                throw new Exception("Este usuario YA está vinculado a esta empresa.");
            } else {
                throw new RuntimeException("Error al vincular usuario con empresa"); // Si es otro error, lo relanzamos
            }
        }
    }

    public List<UsuarioEmpresa> obtenerEmpleados(int idEmpresa) throws Exception {
        try (Connection conn = DBConnection.getInstance().getConnection()) {
            return empresaRepository.listarUsuariosPorEmpresa(conn, idEmpresa);
        } catch (Exception e) {
            throw new Exception("Error al listar empleados: " + e.getMessage());
        }
    }

    public void eliminarEmpleado(int idEmpresa, int idUsuario) throws Exception {
        try (Connection conn = DBConnection.getInstance().getConnection()) {
            conn.setAutoCommit(false); // Transacción por seguridad
            try {
                boolean eliminado = empresaRepository.desvincularUsuario(conn, idEmpresa, idUsuario);

                if (!eliminado) {
                    throw new Exception("El usuario no pertenece a esta empresa o ya fue eliminado.");
                }
                conn.commit();
            } catch (Exception e) {
                if (conn != null) conn.rollback();
                throw new Exception("Ocurrio un error al eliminar: " + e.getMessage());
            }
        }
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
