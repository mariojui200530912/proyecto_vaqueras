package com.proyectobackend.rest.api.juegosapp.services;

import com.proyectobackend.rest.api.juegosapp.dtos.comentario.ComentarioRequest;
import com.proyectobackend.rest.api.juegosapp.dtos.comentario.ComentarioResponse;
import com.proyectobackend.rest.api.juegosapp.repositories.*;

import java.sql.Connection;
import java.util.*;

public class ComentarioService {
    private final ComentarioRepository comentarioRepo = new ComentarioRepository();
    private final BibliotecaRepository bibliotecaRepo = new BibliotecaRepository();
    private final JuegoRepository juegoRepo = new JuegoRepository();
    private final EmpresaRepository empresaRepo = new EmpresaRepository();

    // Guardar Comentario
    public void publicarComentario(int idUsuario, int idJuego, ComentarioRequest req) throws Exception {
        try (Connection conn = DBConnection.getInstance().getConnection()) {
            if (!bibliotecaRepo.usuarioTieneJuego(conn, idUsuario, idJuego)) {
                throw new RuntimeException("No puedes comentar ya que no has comprado el juego.");
            }

            if (idJuego > 0) {
                boolean comentariosHabilitados = empresaRepo.permiteComentarios(conn, idJuego);
                if (!comentariosHabilitados) {
                    throw new Exception("El desarrollador ha desactivado los comentarios para sus juegos temporalmente.");
                }
            }
            comentarioRepo.guardarComentario(conn, idUsuario, idJuego, req.getIdComentarioPadre(), req.getComentario());
        }
    }

    // Listar los comentarios y mapearlos
    public List<ComentarioResponse> obtenerArbolDeComentarios(int idJuego) throws Exception {
        try (Connection conn = DBConnection.getInstance().getConnection()) {
            boolean visualizacionPermitida = empresaRepo.permiteComentarios(conn, idJuego);

            if (!visualizacionPermitida) {
                return new ArrayList<>();
            }

            // Obtenemos todos los datos de la BD
            List<ComentarioResponse> listaPlana = comentarioRepo.obtenerComentariosPlanos(conn, idJuego);

            // Map para búsqueda rápida por ID
            Map<Integer, ComentarioResponse> mapaComentarios = new HashMap<>();
            List<ComentarioResponse> comentariosRaiz = new ArrayList<>();

            // Llenamos el mapa
            for (ComentarioResponse c : listaPlana) {
                mapaComentarios.put(c.getId(), c);
            }

            // Armamos las relaciones
            for (ComentarioResponse c : listaPlana) {
                if (c.getIdPadre() == null) {
                    // Es un comentario principal -> Va a la lista raíz
                    comentariosRaiz.add(c);
                } else {
                    // Es una respuesta -> Buscamos a su papá en el mapa y nos agregamos a él
                    ComentarioResponse padre = mapaComentarios.get(c.getIdPadre());
                    if (padre != null) {
                        padre.agregarRespuesta(c);
                    }
                }
            }

            // Devolvemos solo las raíces (que ya contienen a sus hijos dentro)
            Collections.reverse(comentariosRaiz);
            return comentariosRaiz;
        }
    }

    public void moderarComentario(int idComentario, String nuevoEstado) throws Exception {
        if (!"VISIBLE".equals(nuevoEstado) && !"OCULTO".equals(nuevoEstado)) {
            throw new Exception("Estado inválido.");
        }

        try (Connection conn = DBConnection.getInstance().getConnection()) {
            comentarioRepo.cambiarEstadoComentario(conn, idComentario, nuevoEstado);
        }
    }
}
