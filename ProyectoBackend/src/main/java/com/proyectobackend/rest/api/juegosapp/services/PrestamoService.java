package com.proyectobackend.rest.api.juegosapp.services;

import com.proyectobackend.rest.api.juegosapp.repositories.BibliotecaRepository;
import com.proyectobackend.rest.api.juegosapp.repositories.DBConnection;
import com.proyectobackend.rest.api.juegosapp.repositories.PrestamoRepository;

import java.sql.Connection;

public class PrestamoService {
    private final PrestamoRepository prestamoRepo = new PrestamoRepository();
    private final BibliotecaRepository bibliotecaRepo = new BibliotecaRepository();

    public void solicitarPrestamo(int idBeneficiario, int idJuego, int idDueno) throws Exception {
        try (Connection conn = DBConnection.getInstance().getConnection()) {
            conn.setAutoCommit(false);
            try {
                if (idBeneficiario == idDueno) throw new Exception("No puedes pedirte prestado a ti mismo.");

                // Validar propiedad del dueño
                if (!bibliotecaRepo.usuarioTieneJuego(conn, idDueno, idJuego)) {
                    throw new Exception("El dueño no tiene este juego.");
                }
                if (bibliotecaRepo.usuarioTieneJuego(conn, idBeneficiario, idJuego)) {
                    throw new Exception("¡Ya tienes este juego en tu biblioteca! No necesitas pedirlo prestado.");
                }

                // Validar Grupo
                if (!prestamoRepo.estanEnMismoGrupo(conn, idBeneficiario, idDueno)) {
                    throw new Exception("No están en el mismo grupo familiar.");
                }

                // Validar si ya lo pedí yo mismo (para no duplicar mi lista)
                if (prestamoRepo.yaLoTienePrestado(conn, idBeneficiario, idJuego)) {
                    throw new Exception("Ya tienes este juego en tu lista de préstamos.");
                }

                prestamoRepo.crearPrestamo(conn, idBeneficiario, idJuego, idDueno);
                conn.commit();

            } catch (Exception e) {
                if (conn != null) conn.rollback();
                throw new Exception(e.getMessage());
            }
        } catch (Exception e) {
            throw new Exception("Error al prestar juego: " + e.getMessage());
        }
    }

    // INSTALAR (AQUÍ APLICAMOS LA REGLA DE "UNO A LA VEZ")
    public void instalarJuego(int idPrestamo, int idUsuario) throws Exception {
        try (Connection conn = DBConnection.getInstance().getConnection()) {
            conn.setAutoCommit(false);
            try {
                // Verificar que el préstamo sea mío
                if (!prestamoRepo.yaLoTienePrestado(conn, idPrestamo, idUsuario)) {
                    throw new Exception("Este préstamo no te pertenece.");
                }

                // REGLA DE ORO: Solo un juego instalado a la vez
                if (prestamoRepo.tieneJuegoInstalado(conn, idUsuario)) {
                    throw new Exception("Ya tienes un juego prestado instalado. Debes desinstalarlo o devolverlo antes de jugar otro.");
                }

                // Proceder a instalar
                prestamoRepo.actualizarEstado(conn, idPrestamo, "INSTALADO");

                conn.commit();
            } catch (Exception e) {
                if (conn != null) conn.rollback();
                throw new Exception(e.getMessage());
            }
        } catch (Exception e) {
            throw new Exception("Error al instalar juego prestado: " + e.getMessage());
        }
    }

    // DESINSTALAR (Nuevo: Para liberar el cupo sin devolver el juego)
    public void desinstalarJuego(int idPrestamo, int idUsuario) throws Exception {
        try (Connection conn = DBConnection.getInstance().getConnection()) {
            if (!prestamoRepo.yaLoTienePrestado(conn, idPrestamo, idUsuario)) {
                throw new Exception("Este préstamo no te pertenece.");
            }
            // Volvemos el estado a NO_INSTALADO para liberar el cupo
            prestamoRepo.actualizarEstado(conn, idPrestamo, "NO_INSTALADO");
        }
    }

    // DEVOLVER (Borrar registro)
    public void devolverJuego(int idPrestamo, int idBeneficiario) throws Exception {
        try (Connection conn = DBConnection.getInstance().getConnection()) {
            prestamoRepo.eliminarPrestamo(conn, idPrestamo, idBeneficiario);
        }
    }
}

