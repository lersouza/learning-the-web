package com.ciandt.webl;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Logger;

public class SimpleTcpServer {
    private static final Logger LOGGER = Logger.getLogger("SimpleTcpServer");

    private final int port;
    private final InetAddress host;
    private final int backlog;

    private final BlockingQueue<Socket> clientQueue;
    private final Thread[] threadPool;

    public SimpleTcpServer(final String host, final int port) throws UnknownHostException {
        this.host = InetAddress.getByName(host);
        this.port = port;
        this.backlog = 1;
        this.clientQueue = new ArrayBlockingQueue<Socket>(10);
        this.threadPool = new Thread[3];
    }

    private long getCurrentProcess() {
        return ProcessHandle.current().pid();
    }

    public void init() {
        for (int i = 0; i < threadPool.length; i++) {
            threadPool[i] = new Thread(new Runnable() {
                @Override
                public void run() {
                    SimpleEchoHandler handler = new SimpleEchoHandler();

                    while (true) {
                        Socket next = null;
                        
                        try {
                            // take() é bloqueante
                            next = SimpleTcpServer.this.clientQueue.take();
                        }
                        catch(InterruptedException iex) {
                            continue;
                        }
                        
                        try {
                            handler.handle(next);
                        }
                        catch(IOException ioex) {
                            // TODO: Add some error handling here
                        }
                    }
                }
            });
            threadPool[i].start();
        }
    }

    public void run() throws IOException {
        this.init();

        final ServerSocket serverSocker = new ServerSocket(this.port, this.backlog, this.host);
        final long currentProcess = getCurrentProcess();

        LOGGER.info(String.format("Server started at port %d. PID: %d. Awaiting new client to connect...", this.port,
                currentProcess));

        try {
            while (true) {
                final Socket client = serverSocker.accept(); // Esta é uma chamada bloqueante
                LOGGER.info(String.format("New client connected: %s", client.getRemoteSocketAddress()));

                if (!this.clientQueue.offer(client)) {
                    client.close();
                }
            }
        } finally {
            serverSocker.close();
        }
    }

    public static void main(final String[] args) throws UnknownHostException, IOException {
        new SimpleTcpServer("localhost", 8080).run();
    }
}