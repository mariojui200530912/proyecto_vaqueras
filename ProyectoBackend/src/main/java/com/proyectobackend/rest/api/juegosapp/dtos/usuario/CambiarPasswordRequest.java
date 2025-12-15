/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.proyectobackend.rest.api.juegosapp.dtos.usuario;

/**
 *
 * @author Hp
 */
public class CambiarPasswordRequest {
    private String passwordActual;
    private String nuevaPassword;
    private String confirmacionPassword;

    public boolean isValid() {
        return passwordActual != null && !passwordActual.trim().isEmpty()
                && nuevaPassword != null && !nuevaPassword.trim().isEmpty()
                && nuevaPassword.equals(confirmacionPassword);
    }

    public String getPasswordActual() {
        return passwordActual;
    }

    public void setPasswordActual(String passwordActual) {
        this.passwordActual = passwordActual;
    }

    public String getNuevaPassword() {
        return nuevaPassword;
    }

    public void setNuevaPassword(String nuevaPassword) {
        this.nuevaPassword = nuevaPassword;
    }

    public String getConfirmacionPassword() {
        return confirmacionPassword;
    }

    public void setConfirmacionPassword(String confirmacionPassword) {
        this.confirmacionPassword = confirmacionPassword;
    }
}
