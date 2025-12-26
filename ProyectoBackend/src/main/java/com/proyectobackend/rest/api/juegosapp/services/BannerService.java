package com.proyectobackend.rest.api.juegosapp.services;

import com.proyectobackend.rest.api.juegosapp.dtos.BannerResponse;
import com.proyectobackend.rest.api.juegosapp.models.Juego;
import com.proyectobackend.rest.api.juegosapp.repositories.BannerRepository;
import com.proyectobackend.rest.api.juegosapp.repositories.DBConnection;
import com.proyectobackend.rest.api.juegosapp.repositories.JuegoRepository;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public class BannerService {
    private final BannerRepository bannerRepo = new BannerRepository();
    private final JuegoRepository juegoRepo = new JuegoRepository();

    public List<BannerResponse> obtenerBannerPrincipal() throws Exception {
        try (Connection conn = DBConnection.getInstance().getConnection()) {

            // Intentar obtener la configuración manual (Prioridad Alta)
            List<BannerResponse> banner = bannerRepo.obtenerBannerConfigurado(conn);

            // Algoritmo de "Fallback" (Respaldo)
            // Si el administrador no ha configurado nada (lista vacía), mostrara los 5 ultimos juegos
            if (banner.isEmpty()) {
                banner = bannerRepo.obtenerUltimosLanzamientos(conn, 5);
            }

            // Validación final (Si sigue vacío es porque no hay juegos en el sistema)
            if (banner.isEmpty()) {
                return new ArrayList<>();
            }

            return banner;
        }
    }

    public void agregarJuegoAlBanner(int idJuego) throws Exception {
        try (Connection conn = DBConnection.getInstance().getConnection()) {
            conn.setAutoCommit(false); // Transacción
            try {
                // Validar si el juego existe

                if (!juegoRepo.buscarPorId(conn, idJuego).isEmpty()) {
                    throw new Exception("El juego no existe.");
                }

                // Validar si ya está en el banner
                if (bannerRepo.existeEnBanner(conn, idJuego)) {
                    throw new Exception("Este juego ya se encuentra configurado en el banner.");
                }

                // Obtener el siguiente número de orden (para ponerlo al final)
                int orden = bannerRepo.obtenerSiguienteOrden(conn);

                // Insertar
                bannerRepo.agregarConfiguracion(conn, idJuego, orden);

                conn.commit();
            } catch (Exception e) {
                if (conn != null) conn.rollback();
                throw new Exception(e.getMessage());
            }
        } catch (Exception e) {
            throw new Exception("Error al configurar el banner: " + e.getMessage());
        }

    }

    // QUITAR JUEGO DEL BANNER
    public void quitarJuegoDelBanner(int idJuego) throws Exception {
        try (Connection conn = DBConnection.getInstance().getConnection()) {
            bannerRepo.eliminarConfiguracion(conn, idJuego);
        }
    }

    // CAMBIAR POSICIÓN (Reordenar)
    public void cambiarPosicion(int idJuego, int nuevaPosicion) throws Exception {
        try (Connection conn = DBConnection.getInstance().getConnection()) {
            bannerRepo.actualizarOrden(conn, idJuego, nuevaPosicion);
        }
    }
}
