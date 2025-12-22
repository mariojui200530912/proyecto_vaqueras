/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.proyectobackend.rest.api.juegosapp.services;

import com.proyectobackend.rest.api.juegosapp.dtos.MensajeResponse;
import com.proyectobackend.rest.api.juegosapp.dtos.login.LoginRequest;
import com.proyectobackend.rest.api.juegosapp.dtos.usuario.CambiarPasswordRequest;
import com.proyectobackend.rest.api.juegosapp.dtos.usuario.UsuarioRequest;
import com.proyectobackend.rest.api.juegosapp.dtos.usuario.UsuarioResponse;
import com.proyectobackend.rest.api.juegosapp.exceptions.EntityAlReadyExistException;
import com.proyectobackend.rest.api.juegosapp.models.Usuario;
import com.proyectobackend.rest.api.juegosapp.models.enums.EstadoUsuario;
import com.proyectobackend.rest.api.juegosapp.models.enums.Rol;
import com.proyectobackend.rest.api.juegosapp.repositories.UsuarioRepository;
import com.proyectobackend.rest.api.juegosapp.utils.FileUploadUtil;
import com.proyectobackend.rest.api.juegosapp.utils.PasswordUtil;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 *
 * @author Hp
 */
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;

    public UsuarioService() {
        this.usuarioRepository = new UsuarioRepository();
    }

    public UsuarioResponse registrarUsuario(UsuarioRequest request) throws EntityAlReadyExistException {
        // Validaciones
        if (!request.isValid()) {
            throw new RuntimeException("Datos de registro inválidos");
        }
        // Verificar si el email ya existe
        Optional<Usuario> usuarioExistente = Optional.ofNullable(usuarioRepository.findByEmail(request.getEmail()));
        if (usuarioExistente.isPresent()) {
            throw new EntityAlReadyExistException("El email ya está registrado.");
        }

        // Crear usuario
        Usuario usuario = new Usuario();
        usuario.setNickname(request.getNickname());
        usuario.setPassword(PasswordUtil.encodeBase64(request.getPassword()));
        usuario.setEmail(request.getEmail());
        usuario.setFechaNacimiento(request.getFechaNacimiento());
        usuario.setTelefono(request.getTelefono());
        usuario.setPais(request.getPais());
        usuario.setAvatar(request.getAvatar());
        usuario.setRol(request.getRol());
        usuario.setEstado(EstadoUsuario.ACTIVO);
        usuario.setCartera_saldo(new BigDecimal(0.00));
        usuario.setFechaCreacion(LocalDateTime.now());

        // Guardar usuario
        Usuario usuarioGuardado = usuarioRepository.save(usuario);

        // Crear cartera para el usuario
        return mapUsuarioToResponse(usuarioGuardado);
    }

    public List<UsuarioResponse> listarUsuarios() {
        return usuarioRepository.list()
                .stream()
                .map(usuario -> {
                    // Convertir el usuario y cartera a un DTO de respuesta
                    return mapUsuarioToResponse(usuario);
                })
                .collect(Collectors.toList());

    }

    public UsuarioResponse obtenerUsuarioResponsePorId(Integer id) {
        Usuario usuario = usuarioRepository.findById(id);
        if (usuario == null) {
            throw new RuntimeException("Usuario no encontrado");
        }
        return mapUsuarioToResponse(usuario);
    }

    public Usuario obtenerUsuarioPorId(Integer id) {
        Usuario usuario = usuarioRepository.findById(id);
        if (usuario == null) {
            throw new RuntimeException("Usuario no encontrado");
        }
        return usuario;
    }

    public UsuarioResponse actualizarPerfil(Integer idUsuario, UsuarioRequest request) {
        if (!request.isValid()) {
            throw new RuntimeException("Datos de perfil inválidos");
        }

        Usuario usuario = usuarioRepository.findById(idUsuario);

        // Actualizar datos
        usuario.setNickname(request.getNickname());
        usuario.setPassword(request.getPassword());
        usuario.setEmail(request.getEmail());
        usuario.setFechaNacimiento(request.getFechaNacimiento());
        usuario.setTelefono(request.getTelefono());
        usuario.setPais(request.getPais());
        usuario.setAvatar(request.getAvatar());
        usuario.setRol(request.getRol());
        usuario.setEstado(EstadoUsuario.valueOf(request.getEstado().getValor().toUpperCase()));

        Usuario usuarioActualizado = usuarioRepository.update(usuario);
        return mapUsuarioToResponse(usuarioActualizado);
    }

    public MensajeResponse cambiarPassword(Integer idUsuario, CambiarPasswordRequest request) {
        if (!request.isValid()) {
            throw new RuntimeException("Datos de contraseña inválidos");
        }

        Usuario usuario = usuarioRepository.findById(idUsuario);
        if (usuario == null) {
            throw new RuntimeException("Usuario no encontrado");
        }
        // Verificar contraseña
        if (!PasswordUtil.checkPassword(request.getPasswordActual(), usuario.getPassword())) {
            throw new RuntimeException("Credenciales inválidas");
        }

        // Actualizar contraseña
        String nuevaPasswordHash = PasswordUtil.encodeBase64(request.getNuevaPassword());
        usuarioRepository.updatePassword(idUsuario, nuevaPasswordHash);

        return new MensajeResponse("Contraseña actualizada exitosamente");
    }

    public void cambiarEstado(Integer id, EstadoUsuario nuevoEstado) throws Exception {
        Usuario usuario = usuarioRepository.findById(id);
        if (usuario == null) {
            throw new Exception("Usuario no encontrado con ID: " + id);
        }
        // Regla de negocio: No bloquearse a sí mismo o al Super Admin (Opcional)
        // if (usuario.getRol() == Rol.ADMIN && id.equals(idAdminLogueado)) ...
        usuarioRepository.updateEstado(id, nuevoEstado.name());
    }

    public void eliminarUsuario(Integer id) {
        Usuario usuario = usuarioRepository.findById(id);
        if (usuario == null) {
            throw new RuntimeException("Usuario no encontrado");
        }
        usuarioRepository.delete(id);
    }

    public List<UsuarioResponse> listarPorTipo(Rol rol) {
        List<Usuario> usuarios = usuarioRepository.findByRol(rol);
        return usuarios.stream()
                .map(u -> mapUsuarioToResponse(u))
                .collect(Collectors.toList());
    }

    public UsuarioResponse login(LoginRequest request) throws EntityAlReadyExistException {
        if (!request.isValid()) {
            throw new RuntimeException("Credenciales inválidas");
        }

        // Buscar usuario
        Usuario usuario = usuarioRepository.findByEmail(request.getEmail());
        if (usuario == null) {
            throw new EntityAlReadyExistException("Las credenciales no se encuentran en la base de datos.");
        }

        // Verificar contraseña
        if (!PasswordUtil.checkPassword(request.getPassword(), usuario.getPassword())) {
            throw new RuntimeException("Credenciales inválidas");
        }

        // Verificar estado
        if (usuario.getEstado() != EstadoUsuario.ACTIVO) {
            throw new RuntimeException("Usuario inactivo o suspendido comuniquese con administrador.");
        }

        UsuarioResponse usuarioResponse = mapUsuarioToResponse(usuario);
        return usuarioResponse;
    }

    private UsuarioResponse mapUsuarioToResponse(Usuario usuario) {
        UsuarioResponse response = new UsuarioResponse();

        // Mapear todos los campos
        response.setId(usuario.getId());
        response.setNickname(usuario.getNickname());
        response.setEmail(usuario.getEmail());
        response.setFechaNacimiento(usuario.getFechaNacimiento());
        response.setTelefono(usuario.getTelefono());
        response.setPais(usuario.getPais());
        if (usuario.getAvatar() != null && usuario.getAvatar().length > 0) {
            String base64 = java.util.Base64.getEncoder().encodeToString(usuario.getAvatar());
            response.setAvatar("data:image/jpeg;base64," + base64); // Prefijo necesario para HTML
        }
        response.setRol(usuario.getRol());
        response.setEstado(usuario.getEstado());
        response.setCartera_saldo(usuario.getCartera_saldo());
        response.setFechaCreacion(usuario.getFechaCreacion());

        return response;
    }

}
