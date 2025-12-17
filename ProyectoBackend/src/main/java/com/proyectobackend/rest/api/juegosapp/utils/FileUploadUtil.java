/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.proyectobackend.rest.api.juegosapp.utils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author Hp
 */
public class FileUploadUtil {
     private static final String BASE_UPLOAD_DIR = "C:\\Users\\Hp\\OneDrive\\Documentos\\Universidad\\Cursos\\IPC2\\EVProyecto\\Proyecto\\assets";

    public static String saveFile(InputStream inputStream, String originalFileName, String subfolder) throws IOException {
        if (inputStream == null || originalFileName == null || originalFileName.isEmpty()) {
            return null;
        }

        // Crear carpeta destino
        Path uploadPath = Paths.get(BASE_UPLOAD_DIR, subfolder);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Generar nombre único con fecha y hora
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
        String safeFileName = timestamp + "_" + originalFileName.replaceAll("\\s+", "_");

        // Ruta completa del archivo
        Path filePath = uploadPath.resolve(safeFileName);

        // Guardar archivo
        Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);

        // Devolver ruta relativa para guardar en BD
        return subfolder + "/" + safeFileName;
    }

    // Elimina archivo del servidor
    public static void deleteFile(String relativePath) {
        if (relativePath == null || relativePath.isEmpty()) {
            return;
        }

        try {
            Path absolutePath = Paths.get(BASE_UPLOAD_DIR, relativePath);
            Files.deleteIfExists(absolutePath);
        } catch (IOException e) {
            System.err.println("No se pudo eliminar el archivo: " + relativePath);
        }
    }
}
