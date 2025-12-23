package com.proyectobackend.rest.api.juegosapp.services;

import com.proyectobackend.rest.api.juegosapp.dtos.grupo.GrupoResponse;
import com.proyectobackend.rest.api.juegosapp.repositories.DBConnection;
import com.proyectobackend.rest.api.juegosapp.repositories.GrupoRepository;

import java.sql.Connection;

public class GrupoService {
    private final GrupoRepository grupoRepo = new GrupoRepository();

    // CREAR GRUPO
    public GrupoResponse crearGrupo(int idCreador, String nombre) throws Exception {
        try (Connection conn = DBConnection.getInstance().getConnection();) {
            conn.setAutoCommit(false); // INICIAR TRANSACCIÓN
            try {
                // Crear grupo
                int idGrupo = grupoRepo.crear(conn, idCreador, nombre);

                // Agregar al creador como primer miembro automáticamente
                grupoRepo.agregarMiembro(conn, idGrupo, idCreador);

                conn.commit(); // ✅

                // 3. Retornar el objeto creado
                GrupoResponse resp = new GrupoResponse();
                resp.setId(idGrupo);
                resp.setNombre(nombre);
                resp.setIdCreador(idCreador);
                return resp;

            } catch (Exception e) {
                if (conn != null) conn.rollback();
                throw new Exception("Error al crear grupo: " + e.getMessage());
            }
        } catch (Exception e) {
            throw new Exception("Error al crear grupo general: " + e.getMessage());
        }
    }

    // AGREGAR USUARIO AL GRUPO
    public void invitarUsuario(int idGrupo, int idUsuarioInvitado) throws Exception {
        try (Connection conn = DBConnection.getInstance().getConnection()){
            // Validaciones
            if (grupoRepo.esMiembro(conn, idGrupo, idUsuarioInvitado)) {
                throw new Exception("El usuario ya pertenece al grupo.");
            }

            grupoRepo.agregarMiembro(conn, idGrupo, idUsuarioInvitado);

        } catch (Exception e) {
            throw new Exception("Error al ingresar usuario a grupo: " + e.getMessage());
        }
    }

    // OBTENER DETALLES (GET)
    public GrupoResponse obtenerDetalleGrupo(int idGrupo) throws Exception {
        try (Connection conn = DBConnection.getInstance().getConnection()) {
            GrupoResponse grupo = grupoRepo.buscarPorId(conn, idGrupo);
            if (grupo == null) throw new Exception("Grupo no encontrado");

            // Llenamos la lista de miembros
            grupo.setMiembros(grupoRepo.obtenerMiembros(conn, idGrupo));

            return grupo;
        }
    }

    // ELIMINAR GRUPO (Solo el creador puede hacerlo)
    public void eliminarGrupo(int idUsuarioSolicitante, int idGrupo) throws Exception {
        try (Connection conn = DBConnection.getInstance().getConnection()) {
            GrupoResponse g = grupoRepo.buscarPorId(conn, idGrupo);
            if (g == null) throw new Exception("Grupo no existe");

            if (g.getIdCreador() != idUsuarioSolicitante) {
                throw new Exception("Solo el creador puede eliminar el grupo familiar.");
            }

            grupoRepo.eliminarGrupo(conn, idGrupo);
        }
    }

    // SALIRSE DEL GRUPO (O ELIMINAR MIEMBRO)
    public void expulsarOSalir(int idUsuarioSolicitante, int idGrupo, int idUsuarioAExpulsar) throws Exception {
        try (Connection conn = DBConnection.getInstance().getConnection()) {
            GrupoResponse g = grupoRepo.buscarPorId(conn, idGrupo);

            // Regla: Puedes salirte tú mismo, O el creador puede expulsar a alguien.
            boolean esElMismo = (idUsuarioSolicitante == idUsuarioAExpulsar);
            boolean esElCreador = (g.getIdCreador() == idUsuarioSolicitante);

            if (!esElMismo && !esElCreador) {
                throw new Exception("No tienes permisos para eliminar a este usuario.");
            }

            if (g.getIdCreador() == idUsuarioAExpulsar) {
                throw new Exception("El creador no puede salirse. Debe eliminar el grupo.");
            }

            grupoRepo.eliminarMiembro(conn, idGrupo, idUsuarioAExpulsar);
        }
    }
}

