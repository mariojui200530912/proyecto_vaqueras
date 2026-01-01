package com.proyectobackend.rest.api.juegosapp.services;

import com.proyectobackend.rest.api.juegosapp.repositories.BibliotecaRepository;
import com.proyectobackend.rest.api.juegosapp.repositories.CalificacionRepository;
import com.proyectobackend.rest.api.juegosapp.repositories.DBConnection;
import com.proyectobackend.rest.api.juegosapp.repositories.JuegoRepository;

import java.math.BigDecimal;
import java.sql.Connection;

public class CalificacionService {
    private final CalificacionRepository calificacionRepository;
    private final JuegoRepository juegoRepository;
    private final BibliotecaRepository bibliotecaRepository;

    public CalificacionService() {
        this.calificacionRepository = new CalificacionRepository();
        this.juegoRepository = new JuegoRepository();
        this.bibliotecaRepository = new BibliotecaRepository();
    }

    // Guardar Calificación
    public void calificarJuego(int idUsuario, int idJuego, double calificacion) throws Exception {
        if (calificacion < 1 || calificacion > 5) throw new Exception("La calificación debe ser entre 1 y 5");

        try (Connection conn = DBConnection.getInstance().getConnection()) {
            conn.setAutoCommit(false);
            try {
                if (!bibliotecaRepository.usuarioTieneJuego(conn, idUsuario, idJuego)) {
                    throw new Exception("Error: Debes comprar el juego para poder calificarlo.");
                }
                calificacionRepository.guardarCalificacion(conn, idUsuario, idJuego, calificacion);
                BigDecimal calificacionPromedio = calificacionRepository.obtenerPromedioJuego(conn, idJuego);
                juegoRepository.actualizarPromedioCalificacion(conn, idJuego, calificacionPromedio);
            }catch (Exception e){
                conn.rollback();
                throw new Exception("Error al calificar el juego: " + e.getMessage());
            }
        }
    }
}
