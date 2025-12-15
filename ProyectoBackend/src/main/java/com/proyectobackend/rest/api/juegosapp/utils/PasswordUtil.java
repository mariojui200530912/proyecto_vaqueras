/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.proyectobackend.rest.api.juegosapp.utils;

import java.util.Base64;

/**
 *
 * @author Hp
 */
public class PasswordUtil {
    public static String encodeBase64(String plainPassword) {
        try {
            return Base64.getEncoder().encodeToString(plainPassword.getBytes());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Error de encoding", e);
        }
    }
   
    // Verifica comparando codificado vs almacenado
    public static boolean checkPassword(String plainPassword, String encodedPassword) {
        return encodeBase64(plainPassword).equals(encodedPassword);
    }
}
