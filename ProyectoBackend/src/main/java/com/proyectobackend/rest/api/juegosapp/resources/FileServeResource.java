package com.proyectobackend.rest.api.juegosapp.resources;

import com.proyectobackend.rest.api.juegosapp.dtos.MensajeResponse;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

@Path("/uploads")
public class FileServeResource {
    private static final String BASE_UPLOAD_DIR = "C:\\Users\\Hp\\OneDrive\\Documentos\\Universidad\\Cursos\\IPC2\\EVProyecto\\Proyecto\\assets";

    @GET
    @Path("/{subfolder}/{fileName}")
    @Produces({"image/png", "image/jpeg", "image/jpg", "image/webp"})
    public Response getFile(
            @PathParam("subfolder") String subfolder,
            @PathParam("fileName") String fileName
    ) {
        try {
            File file = Paths.get(BASE_UPLOAD_DIR, subfolder, fileName).toFile();

            if (!file.exists()) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"error\":\"Archivo no encontrado\"}")
                        .type(MediaType.APPLICATION_JSON)
                        .build();
            }

            String mimeType = Files.probeContentType(file.toPath());
            if (mimeType == null) mimeType = "application/octet-stream";

            return Response.ok(file, mimeType)
                    .header("Content-Disposition", "inline; filename=\"" + fileName + "\"")
                    .build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new MensajeResponse("Error al servir el archivo: " + e.getMessage()))
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }
    }
}