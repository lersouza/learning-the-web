package com.ciandt.webl.aplos;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Logger;

public class AplosServer {
    private static Logger LOGGER = Logger.getLogger("aplos.server");

    private final int port;
    private final InetAddress address;
    private final int backlog;

    private final BlockingQueue<Socket> clientQueue;
    private final Thread[] threadPool;

    private final HttpServletRegistry servletRegistry;

    public AplosServer(final String host, final int port) throws UnknownHostException {
        this.address = InetAddress.getByName(host);
        this.port = port;
        this.backlog = 3;

        this.clientQueue = new ArrayBlockingQueue<Socket>(10);
        this.threadPool = new Thread[3];

        this.servletRegistry = new HttpServletRegistry();
    }

    public void init() {
        for (int i = 0; i < threadPool.length; i++) {
            threadPool[i] = new Thread(new HttpHandler(this.clientQueue, this.servletRegistry));
            threadPool[i].start();
        }
    }

    public void run() throws IOException {
        this.init();

        final ServerSocket serverSocket = new ServerSocket(this.port, this.backlog, this.address);
        final long currentProcess = getCurrentProcess();

        LOGGER.info(String.format("Aplos Server started at port %d. PID: %d. Awaiting new client to connect...",
                this.port, currentProcess));

        try {
            while (true) {
                final Socket client = serverSocket.accept();
                LOGGER.info(String.format("New client connected: %s", client.getRemoteSocketAddress()));

                if(!this.clientQueue.offer(client)) {
                    LOGGER.warning("Client queue is FULL. Rejecting client.");
                    client.close();
                }
            }
        } finally {
            serverSocket.close();
        }
    }

    private long getCurrentProcess() {
        return ProcessHandle.current().pid();
    }

    public static void main(final String[] args) throws IOException {
        new AplosServer("localhost", 8080).run();
    }
}
