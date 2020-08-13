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
import java.net.Socket;
import java.util.logging.Logger;

public class SimpleEchoHandler {
    private static final Logger LOGGER = Logger.getLogger(SimpleEchoHandler.class.getName());

    public void handle(final Socket client) throws IOException {
        final long currentThread = Thread.currentThread().getId();

        final PrintWriter out = new PrintWriter(client.getOutputStream(), true);
        final BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));

        final int inputDescriptor = getFileDescriptor(client.getInputStream());
        final int outputDescriptor = getFileDescriptor(client.getOutputStream());

        LOGGER.info(String.format("Thread: %d, Input: %d, Output: %s", currentThread, inputDescriptor,
                outputDescriptor));

        String message = "";

        do {
            message = reader.readLine();
            LOGGER.info(String.format("Thread: %d. Received message: %s. Sending echo...", currentThread, message));

            out.println(message);
            LOGGER.info(String.format("Thread: %d. Message '%s' sent to client.", currentThread, message));
        } while (message != null && !message.isBlank() && !"quit".equals(message));

        LOGGER.info("Client request quit. Shutting down...");

        out.close();
        reader.close();

    }

    private int getFileDescriptor(final InputStream input) throws IOException {
        if (input instanceof FileInputStream) {
            return extractFdField(((FileInputStream) input).getFD());
        }
        return -1;
    }

    private int getFileDescriptor(final OutputStream output) throws IOException {
        if (output instanceof FileOutputStream) {
            return extractFdField(((FileOutputStream) output).getFD());
        }
        return -1;
    }

    private int extractFdField(final FileDescriptor descriptor) {
        final Class<?> clazz = descriptor.getClass();

        try {
            final Field fd = clazz.getDeclaredField("fd");
            fd.setAccessible(true);

            return (int) fd.getInt(descriptor);
        } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
            return -2;
        }
    }
}