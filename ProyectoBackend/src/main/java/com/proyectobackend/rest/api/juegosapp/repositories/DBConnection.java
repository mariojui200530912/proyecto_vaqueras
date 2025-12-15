/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.proyectobackend.rest.api.juegosapp.repositories;

import java.sql.Connection;
import java.sql.SQLException;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;

/**
 *
 * @author Hp
 */
public class DBConnection {
    private static final String IP = "172.25.30.204";
    private static final int PUERTO = 3306;
    private static final String SCHEMA = "proyecto_vaqueras";
    private static final String USER_NAME = "mariojui";
    private static final String PASSWORD = "marioJui_2308";
    private static final String URL = "jdbc:mysql://" + IP + ":" + PUERTO + "/" + SCHEMA;

    private static DBConnection instance;
    private DataSource datasource;

    private Connection connection;
    
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("Driver de MySQL registrado correctamente");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("No se pudo cargar el driver de MySQL", e);
        }
    }

    private DBConnection() {
        try {
            
            Class.forName("com.mysql.cj.jdbc.Driver");
            PoolProperties p = new PoolProperties();
            p.setUrl(URL);
            p.setDriverClassName("com.mysql.cj.jdbc.Driver");
            p.setUsername(USER_NAME);
            p.setPassword(PASSWORD);
            p.setJmxEnabled(true);
            p.setTestWhileIdle(false);
            p.setTestOnBorrow(true);
            p.setValidationQuery("SELECT 1");
            p.setTestOnReturn(false);
            p.setValidationInterval(30000);
            p.setTimeBetweenEvictionRunsMillis(30000);
            p.setMaxActive(100);
            p.setInitialSize(10);
            p.setMaxWait(10000);
            p.setRemoveAbandonedTimeout(60);
            p.setMinEvictableIdleTimeMillis(30000);
            p.setMinIdle(10);
            p.setLogAbandoned(true);
            p.setRemoveAbandoned(true);
            p.setJdbcInterceptors(
                    "org.apache.tomcat.jdbc.pool.interceptor.ConnectionState;"
                    + "org.apache.tomcat.jdbc.pool.interceptor.StatementFinalizer");
            datasource = new DataSource(p);
            datasource.setPoolProperties(p);
        } catch (ClassNotFoundException ex) {
            System.getLogger(DBConnection.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
    }

    public Connection getConnection() {
        try {
            return datasource.getConnection();
        } catch (SQLException ex) {
            System.getLogger(DBConnection.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
        return null;
    }

    public static DBConnection getInstance() {
        if (instance == null) {
            instance = new DBConnection();
        }
        return instance;
    }    
}
