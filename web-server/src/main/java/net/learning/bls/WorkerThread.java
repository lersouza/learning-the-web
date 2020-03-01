package net.learning.bls;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WorkerThread implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(WorkerThread.class);

    private BlockingQueue<Socket> incomingConnections;
    private String name;

    public WorkerThread(String name, BlockingQueue<Socket> incomingConnections) {
        this.incomingConnections = incomingConnections;
        this.name = name;
    }

    @Override
    public void run() {
        while(true) {
            try {
                Socket clientSocket = this.incomingConnections.take();
                LOGGER.info("Worker thread {} received a new connection.", this.name);

                this.handleConnection(clientSocket);
            }
            catch(InterruptedException ie) {
                LOGGER.warn("Worker thread {} was interrupted.", this.name);
                break;
            }
            catch(IOException ioE) {
                LOGGER.warn("IO Error when handling client.", ioE);
                continue;
            }
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

    protected HttpResponse handleRequest(HttpRequest request) {
        try {
            Thread.sleep(2000);
        }
        catch(InterruptedException e) {
        }

        HttpResponse response = new HttpResponse();
        response.setStatus(200, "OK");
        response.setBody("<html><head><title>Welcome!</title></head><body><h1>Welcome to my simple server</h1></body></html>", "text/html");

        return response;
    }
}
