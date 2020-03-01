package net.learning.bls;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DummyServer {
    private static final Logger LOGGER = LoggerFactory.getLogger(DummyServer.class);

    private final ServerDefinition serverDefinition;

    public DummyServer(final ServerDefinition definition) {
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
                final InputStream in = clientSocket.getInputStream();
                final OutputStream out = clientSocket.getOutputStream();

                BufferedReader reader = null;

                try {
                    LOGGER.info("New connection accepted.");

                    StringBuilder requestBuilder = new StringBuilder();

                    reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));

                    String nextLine = null;
                    while((nextLine = reader.readLine()) != null && !nextLine.isEmpty()) {
                        requestBuilder.append(nextLine).append("\r\n");
                    }

                    LOGGER.info("Receive Request:\n{}", requestBuilder.toString());

                    PrintWriter pw = new PrintWriter(out);

                    pw.println("HTTP/1.1 200 OK");
                    pw.println("Content-Type: text/html");
                    pw.println("Content-Length: 115");
                    pw.println();
                    pw.println("<html><head><title>Dummy Server!</title></head><body><h1>Dummy Server</h1><br />Response is always ok</body></html>");

                    pw.flush();
                }
                finally {
                    LOGGER.info("Closing connection with user");
                    reader.close();
                    in.close();
                    out.close();
                    clientSocket.close();

                }
            }

        } catch (final IOException ioEx) {
            LOGGER.error("IO Error: {}", ioEx.getMessage(), ioEx);
            return -1;
        }
    }

    public static void main(final String[] args) {
        final ServerDefinition definition = ServerDefinition.newDefinition();
        final DummyServer server = new DummyServer(definition);

        server.start();
    }
}
