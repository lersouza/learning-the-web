package net.learning.bls;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PoolServer {
    private static final Logger LOGGER = LoggerFactory.getLogger(PoolServer.class);

    private final ServerDefinition serverDefinition;
    private BlockingQueue<Socket> incomingConnections;

    public PoolServer(final ServerDefinition definition) {
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

        this.incomingConnections = new ArrayBlockingQueue<>(this.serverDefinition.getClientQueueSize());
        this.startPool();

        try {
            final ServerSocket serverSocket = new ServerSocket(
                    this.serverDefinition.getPort(),
                    this.serverDefinition.getKernelBacklog());

            LOGGER.info("Listening to connections on port {}", serverSocket.getLocalPort());

            while(true) {
                LOGGER.info("Waiting for new connection ...");

                final Socket clientSocket = serverSocket.accept();

                LOGGER.info("New connection accepted.");

                boolean clientCanBeHandled = this.incomingConnections.offer(clientSocket);

                if(!clientCanBeHandled) {
                    // Do something more polite
                    clientSocket.close();
                }
            }
        } catch (final IOException ioEx) {
            LOGGER.error("IO Error: {}", ioEx.getMessage(), ioEx);
            return -1;
        }
    }

    private void startPool() {
        for(int i = 0; i < this.serverDefinition.getPoolCapacity(); i++) {
            WorkerThread worker = this.createWorker(i);

            Thread workerThread = new Thread(worker);
            workerThread.start();
        }
    }

    protected WorkerThread createWorker(int workerId) {
       WorkerThread worker = new WorkerThread(
               String.format("PoolServer-Worker-%d", workerId),
               this.incomingConnections);

       return worker;
    }

    public static void main(final String[] args) {
        final ServerDefinition definition = ServerDefinition.newDefinition();
        final PoolServer server = new PoolServer(definition);

        server.start();
    }

    public BlockingQueue<Socket> getIncomingConnections() {
        return incomingConnections;
    }
}
