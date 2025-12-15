/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.proyectobackend.rest.api.juegosapp.models.enums;

/**
 *
 * @author Hp
 */
public enum Rol {
    GAMER("GAMER"),
    ADMIN("ADMIN"),
    EMPRESA("EMPRESA");
    
    private final String valor;
    
    Rol(String valor) {
        this.valor = valor;
    }
    
    public String getValor() {
        return valor;
    }
    
    public static Rol fromString(String valor) {
        for (Rol rol : Rol.values()) {
            if (rol.valor.equalsIgnoreCase(valor)) {
                return rol;
            }
        }
        throw new IllegalArgumentException("Tipo de usuario desconocido: " + valor);
    }
}
