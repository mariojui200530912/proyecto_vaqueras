/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.proyectobackend.rest.api.juegosapp.dtos.login;

/**
 *
 * @author Hp
 */
public class LoginRequest {
    private String email;
    private String password;
    
    public boolean isValid() {
        return email != null && !email.trim().isEmpty()
                && password != null && !password.trim().isEmpty();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
