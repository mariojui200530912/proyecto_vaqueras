package com.proyectobackend.rest.api.juegosapp.dtos.comentario;

import java.util.ArrayList;
import java.util.List;

public class ComentarioResponse {
    private int id;
    private String usuarioNickname;
    private String usuarioAvatarUrl;
    private String comentario;
    private String fecha;
    private Integer idPadre;

    private List<ComentarioResponse> respuestas = new ArrayList<>();

    public void agregarRespuesta(ComentarioResponse respuesta) {
        this.respuestas.add(respuesta);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsuarioNickname() {
        return usuarioNickname;
    }

    public void setUsuarioNickname(String usuarioNickname) {
        this.usuarioNickname = usuarioNickname;
    }

    public String getUsuarioAvatarUrl() {
        return usuarioAvatarUrl;
    }

    public void setUsuarioAvatarUrl(String usuarioAvatarUrl) {
        this.usuarioAvatarUrl = usuarioAvatarUrl;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public Integer getIdPadre() {
        return idPadre;
    }

    public void setIdPadre(Integer idPadre) {
        this.idPadre = idPadre;
    }
}
