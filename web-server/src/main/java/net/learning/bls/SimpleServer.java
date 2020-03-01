package net.learning.bls;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleServer {
    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleServer.class);

    private final ServerDefinition serverDefinition;

    public SimpleServer(final ServerDefinition definition) {
        this.serverDefinition = definition;
    }

   /**
     * Start this server, listening to the specified http port.
     *
     *
     * */
    public int start() {
        LOGGER.info("Starting server at {}:{}",
                this.serverDefinition.getListeningAddr(), this.serverDefinition.getPort());

        try {
            final ServerSocket serverSocket = new ServerSocket(
                    this.serverDefinition.getPort(),
                    this.serverDefinition.getKernelBacklog());

            LOGGER.info("Listening to connections on port {}", serverSocket.getLocalPort());

            while(true) {
                LOGGER.info("Waiting for new connection ...");

                final Socket clientSocket = serverSocket.accept();

                LOGGER.info("New connection accepted.");

                this.handleConnection(clientSocket);
            }

        } catch (final IOException ioEx) {
            LOGGER.error("IO Error: {}", ioEx.getMessage(), ioEx);
            return -1;
        }
    }

    private void handleConnection(Socket clientSocket) throws IOException {
       final InputStream in = clientSocket.getInputStream();
       final OutputStream out = clientSocket.getOutputStream();

       try {
           HttpRequest request = null;

           try {
               request = HttpRequest.fromInputStream(in);
           }
           catch(MalformedRequestException mre) {
               LOGGER.error("Request is malformed!", mre);
               return;
           }

           LOGGER.info("Request Received:\n{}", request.toString());

           HttpResponse response = this.handleRequest(request);

           if(response != null) {
               LOGGER.info("Response will be returned to user.");
               response.writeTo(out);
           }
       }
       finally {
           LOGGER.info("Closing connection with user");
           in.close();
           out.close();
           clientSocket.close();

       }
    }

    private HttpResponse handleRequest(HttpRequest request) {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
        }

        HttpResponse response = new HttpResponse();

        response.setStatus(200, "OK");
        response.setBody("<html><head><title>Welcome!</title></head><body><h1>Welcome to my simple server</h1></body></html>", "text/html");

        return response;
    }

    public static void main(final String[] args) {
        final ServerDefinition definition = ServerDefinition.newDefinition();
        final SimpleServer server = new SimpleServer(definition);

        LOGGER.info("About to start server with definition: {}", definition.toString());

        server.start();
    }
}
