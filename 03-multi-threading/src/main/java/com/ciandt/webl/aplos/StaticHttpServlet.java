package com.ciandt.webl.aplos;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StaticHttpServlet implements HttpServlet {
    private static Logger LOGGER = Logger.getLogger("aplos.staticServlet");

    private String rootPath = "./contents";
    private int count = 0;

    @Override
    public String path() {
        return "/";
    }

    @Override
    public void doGet(HttpRequest request, HttpResponse response) {
        File file = new File(this.rootPath, request.getPath());

        this.count++;

        // Se for um diretório, tentamos obter o index.html
        if(file.isDirectory()) {
            file = new File(file, "index.html");
        }

        if (!file.exists()) {
            LOGGER.info(String.format("File not found: %s", file.getAbsolutePath()));
            response.setStatus(404, "Not Found");
            return;
        }

        try {
            Path filePath = file.toPath();

            byte[] contents = Files.readAllBytes(filePath);
            String mimeType = Files.probeContentType(filePath);

            response.setBody(contents, mimeType);
        } catch (Exception ex) {
            LOGGER.log(Level.WARNING, "Error processing file " + file.getName() + ". Ex: " + ex.getMessage());
            response.setStatus(500, "Internal Server Error");
        }
    }

    @Override
    public void doPost(HttpRequest request, HttpResponse response) {
        response.setStatus(405, "Method Not Allowed");
    }
    
}