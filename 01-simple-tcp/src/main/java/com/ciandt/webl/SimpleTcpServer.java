package com.ciandt.webl;

import java.io.BufferedReader;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Logger;

public class SimpleTcpServer {
    private static final Logger LOGGER = Logger.getLogger("SimpleTcpServer");

    private final int port;
    private final InetAddress host;
    private final int backlog;

    public SimpleTcpServer(final String host, final int port) throws UnknownHostException {
        this.host = InetAddress.getByName(host);
        this.port = port;
        this.backlog = 10;
    }

    private int getFileDescriptor(final InputStream input) throws IOException {
        if (input instanceof FileInputStream) {
            return extractFdField(((FileInputStream)input).getFD());
        }
        return -1;
    }

    private int getFileDescriptor(final OutputStream output) throws IOException {
        if (output instanceof FileOutputStream) {
            return extractFdField(((FileOutputStream)output).getFD());
        }
        return -1;
    }

    private int extractFdField(FileDescriptor descriptor) {
        final Class<?> clazz = descriptor.getClass();

        try {
            final Field fd = clazz.getDeclaredField("fd");
            fd.setAccessible(true);

            return (int) fd.getInt(descriptor);
        } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
            return -2;
        }
    }

    private long getCurrentProcess() {
        return ProcessHandle.current().pid();
    }

    public void run() throws IOException {
        final ServerSocket serverSocker = new ServerSocket(this.port, this.backlog, this.host);
        final long currentProcess = getCurrentProcess();

        LOGGER.info(String.format("Server started at port %d. PID: %d. Awaiting new client to connect...", this.port, currentProcess));

        final Socket client = serverSocker.accept();
        LOGGER.info(String.format("New client connected: %s", client.getRemoteSocketAddress()));

        final PrintWriter out = new PrintWriter(client.getOutputStream(), true);
        final BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));

        final int inputDescriptor = getFileDescriptor(client.getInputStream());
        final int outputDescriptor = getFileDescriptor(client.getOutputStream());

        LOGGER.info(String.format("Process: %d, Input: %d, Output: %s", currentProcess, inputDescriptor, outputDescriptor));

        String message = "";

        do {
            message = reader.readLine();
            LOGGER.info(String.format("Received message: %s. Sending echo...", message));

            out.println(message);
            LOGGER.info(String.format("Message '%s' sent to client.", message));
        } while (message != null && !message.isBlank() && !"quit".equals(message));

        LOGGER.info("Client request quit. Shutting down...");

        out.close();
        reader.close();
        serverSocker.close();
    }

    public static void main(String[] args) throws UnknownHostException, IOException {
        new SimpleTcpServer("localhost", 8080).run();
    }
}