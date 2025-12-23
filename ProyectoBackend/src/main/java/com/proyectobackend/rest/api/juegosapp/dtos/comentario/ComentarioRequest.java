package com.proyectobackend.rest.api.juegosapp.dtos.comentario;

public class ComentarioRequest {
    private String comentario;
    private Integer idComentarioPadre;

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public Integer getIdComentarioPadre() {
        return idComentarioPadre;
    }

    public void setIdComentarioPadre(Integer idComentarioPadre) {
        this.idComentarioPadre = idComentarioPadre;
    }
}
