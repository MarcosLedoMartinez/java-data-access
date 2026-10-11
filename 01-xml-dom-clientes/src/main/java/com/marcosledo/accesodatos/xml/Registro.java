package com.marcosledo.accesodatos.xml;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author Marcos Ledo Martinez
 */
public class Registro {

    private String rutaLog;

    public Registro(String rutaLog) {
        this.rutaLog = rutaLog;
        try {
            File archivo = new File(rutaLog);
            File carpeta = archivo.getParentFile();
            if (carpeta != null && !carpeta.exists()) {
                carpeta.mkdirs();
            }
            if (!archivo.exists()) {
                archivo.createNewFile();
            }
        } catch (IOException e) {
            System.out.println("Error inicializando el log: " + e.getMessage());
        }
    }
    public void registrar(String operacion, String resultado, String mensajeError) {
        LocalDateTime ahora = LocalDateTime.now();
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        String fechaFormateada = ahora.format(formato);
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(rutaLog, true))) {
            String lineaLog = String.format("%s || %s || %s || %s", fechaFormateada, operacion.toUpperCase(), resultado.toUpperCase(), (mensajeError == null ? "" : mensajeError));
            bw.write(lineaLog);
            bw.newLine();
        } catch (IOException e) {
            System.err.println("Error critico: No se pudo escribir en el registro de eventos" + e.getMessage());
        }

    }
}
