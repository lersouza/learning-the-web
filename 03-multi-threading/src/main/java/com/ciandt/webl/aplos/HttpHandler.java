package com.ciandt.webl.aplos;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Logger;

public class HttpHandler implements Runnable {
    private static Logger LOGGER = Logger.getLogger("aplos.httpHandler");

    private final BlockingQueue<Socket> incomingBlockingQueue;
    private final HttpServletRegistry servletRegistry;

    public HttpHandler(final BlockingQueue<Socket> incomingQueue, HttpServletRegistry registry) {
        this.incomingBlockingQueue = incomingQueue;
        this.servletRegistry = registry;
    }

    @Override
    public void run() {
        while(true) {
            Socket client = null;

            try {
                client = this.incomingBlockingQueue.take();
            }
            catch(final InterruptedException iex) {
                continue;
            }

            InputStream in = null;
            OutputStream out = null;

            try {
                in = client.getInputStream();
                out = client.getOutputStream();
            }
            catch(IOException ioEx) {
                LOGGER.warning("Could not read client input or output stream");
                
                try{
                    client.close();
                }
                catch (Exception ex) {
                    LOGGER.warning("Could not close client");
                }

                continue;
            }

            HttpRequest request = null;
            HttpResponse response = null;

            try {
                // O Protocolo HTTP é orientado a Request e Response!
                request = HttpRequest.fromInputStream(in);
                Thread.sleep(10000);
                response = this.handleRequest(request);
            } catch (final MalformedRequestException mEx) {
                response = HttpResponse.BAD_REQUEST;
            } catch(final IOException ioEx) {
                response = HttpResponse.INTERNAL_SERVER_ERROR;
            } catch (InterruptedException e) {
                response = HttpResponse.INTERNAL_SERVER_ERROR;
            }


            if (request != null) {
                // O Famoso log de acesso
                LOGGER.info(String.format("%s - %s - %s - %d", client.getRemoteSocketAddress(), request.getUserAgent(),
                        request.getPath(), response.getStatusCode()));
            }

            // Solicitamos que a responsta seja escrita na strem do cliente.
            response.addHeader("Server", "Aplos");
            
            try {
                response.writeTo(out);
                client.close();
            } catch (IOException ioEx) {
                LOGGER.warning("Could not send response to client.");
            }
        }
    }

    private HttpResponse handleRequest(HttpRequest request) {
        if (request == null || request.getMethod() == null) {
            return HttpResponse.BAD_REQUEST;
        }

        HttpServlet servlet = this.servletRegistry.forRequest(request);
        HttpResponse response = new HttpResponse(200, "OK");

        switch(request.getMethod().toUpperCase()) {
            case  "GET":
                servlet.doGet(request, response);
                break;
            case "POST":
                servlet.doPost(request, response);
                break;
            default:
                response = HttpResponse.METHOD_NOT_ALLOWED;
        }

        return response;
    }

}