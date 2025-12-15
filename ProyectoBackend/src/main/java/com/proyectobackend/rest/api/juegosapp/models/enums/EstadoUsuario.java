/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.proyectobackend.rest.api.juegosapp.models.enums;

/**
 *
 * @author Hp
 */
public enum EstadoUsuario {
    ACTIVO("ACTIVO"),
    INACTIVO("INACTIVO"),
    SUSPENDIDO("SUSPENDIDO");
    
    private final String valor;
    
    EstadoUsuario(String valor) {
        this.valor = valor;
    }
    
    public String getValor() {
        return valor;
    }
    
    public static EstadoUsuario fromString(String valor) {
        for (EstadoUsuario estado : EstadoUsuario.values()) {
            if (estado.valor.equalsIgnoreCase(valor)) {
                return estado;
            }
        }
        throw new IllegalArgumentException("Estado de usuario desconocido: " + valor);
    }
}
