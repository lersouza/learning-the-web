package com.ciandt.webl.dynamikos;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DynamicosServer {
    private static Logger LOGGER = Logger.getLogger("aplos.server");

    private final int port;
    private final InetAddress address;
    private final int backlog;
    private final MetaServer metaServer;
    private final Map<String, Class<?>> controllers;
    private String rootPath = "./contents";


    public DynamicosServer(final String host, final int port) throws UnknownHostException {
        this.address = InetAddress.getByName(host);
        this.port = port;
        this.backlog = 10;
        this.metaServer = new MetaServer();
        this.controllers = new TreeMap<String,Class<?>>(String.CASE_INSENSITIVE_ORDER);
    }

    private void initialize() throws IOException {
        List<Class<?>> controllers = this.metaServer.searchControllers("com.ciandt.webl.dynamikos");

        for(Class<?> controllerClass : controllers) {
            this.controllers.put(this.metaServer.getControllerPath(controllerClass), controllerClass);
        }
    }

    public void run() throws IOException {
        this.initialize();

        final ServerSocket serverSocket = new ServerSocket(this.port, this.backlog, this.address);
        final long currentProcess = getCurrentProcess();

        LOGGER.info(String.format("Aplos Server started at port %d. PID: %d. Awaiting new client to connect...",
                this.port, currentProcess));

        try {
            while (true) {
                LOGGER.info("Waiting for new client to connect ...");
                final Socket client = serverSocket.accept();

                LOGGER.info(String.format("New client connected: %s", client.getRemoteSocketAddress()));

                final InputStream in = client.getInputStream();
                final OutputStream out = client.getOutputStream();

                HttpRequest request = null;
                HttpResponse response = null;

                try {
                    // O Protocolo HTTP é orientado a Request e Response!
                    request = HttpRequest.fromInputStream(in);
                    response = this.handleRequest(request);

                } catch (MalformedRequestException mEx) {
                    response = HttpResponse.BAD_REQUEST;
                }

                if (request != null) {
                    // O Famoso log de acesso
                    LOGGER.info(String.format("%s - %s - %s - %d", client.getRemoteSocketAddress(),
                            request.getUserAgent(), request.getPath(), response.getStatusCode()));
                }

                // Solicitamos que a responsta seja escrita na strem do cliente.
                response.addHeader("Server", "Dynamikos");
                response.writeTo(out);
                
                client.close();
            }
        } finally {
            serverSocket.close();
        }
    }

    private HttpResponse handleRequest(HttpRequest request) {
        if (request == null || request.getMethod() == null) {
            return HttpResponse.BAD_REQUEST;
        }

        Class<?> controllerClass = this.controllers.getOrDefault(request.getPath(), null);

        if (controllerClass == null) {
            return this.handleStatic(request);
        }
        return this.metaServer.invokeMethod(controllerClass, request);
    }

    private HttpResponse handleStatic(HttpRequest request) {
        if (!"GET".equals(request.getMethod().toUpperCase())) {
            return HttpResponse.METHOD_NOT_ALLOWED;
        }

        File file = new File(this.rootPath, request.getPath());

        // Se for um diretório, tentamos obter o index.html
        if(file.isDirectory()) {
            file = new File(file, "index.html");
        }

        if (!file.exists()) {
            LOGGER.info(String.format("File not found: %s", file.getAbsolutePath()));
            return HttpResponse.NOT_FOUND;
        }

        try {
            Path filePath = file.toPath();

            byte[] contents = Files.readAllBytes(filePath);
            String mimeType = Files.probeContentType(filePath);

            HttpResponse response = new HttpResponse(200, "OK");

            response.setBody(contents, mimeType);
            return response;
        } catch (Exception ex) {
            LOGGER.log(Level.WARNING, "Error processing file " + file.getName() + ". Ex: " + ex.getMessage());
            return HttpResponse.INTERNAL_SERVER_ERROR;
        }
    }

    private long getCurrentProcess() {
        return ProcessHandle.current().pid();
    }

    public static void main(final String[] args) throws IOException {
        new DynamicosServer("localhost", 8080).run();
    }
}
