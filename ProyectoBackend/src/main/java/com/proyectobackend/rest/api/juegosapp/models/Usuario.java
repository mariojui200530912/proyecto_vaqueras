/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.proyectobackend.rest.api.juegosapp.models;

import com.proyectobackend.rest.api.juegosapp.models.enums.EstadoUsuario;
import com.proyectobackend.rest.api.juegosapp.models.enums.Rol;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 *
 * @author Hp
 */
public class Usuario {
    private Integer id;
    private String nickname;
    private String email;
    private String password;
    private LocalDate fechaNacimiento;
    private String telefono;
    private String pais;
    private byte[] avatar;
    private Rol rol;
    private EstadoUsuario estado;
    private BigDecimal carteraSaldo;
    private LocalDateTime fechaCreacion;
    private Boolean bibliotecaPublica;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
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

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public byte[] getAvatar() {
        return avatar;
    }

    public void setAvatar(byte[] avatar) {
        this.avatar = avatar;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    public EstadoUsuario getEstado() {
        return estado;
    }

    public void setEstado(EstadoUsuario estado) {
        this.estado = estado;
    }

    public BigDecimal getCarteraSaldo() {
        return carteraSaldo;
    }

    public void setCarteraSaldo(BigDecimal carteraSaldo) {
        this.carteraSaldo = carteraSaldo;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public Boolean getBibliotecaPublica() {
        return bibliotecaPublica;
    }

    public void setBibliotecaPublica(Boolean bibliotecaPublica) {
        this.bibliotecaPublica = bibliotecaPublica;
    }
}
