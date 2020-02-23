package net.learning.bls;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Server {
    private static final Logger LOGGER = LoggerFactory.getLogger(Server.class);

    private ServerDefinition serverDefinition;

    public Server(ServerDefinition definition) {
        this.serverDefinition = definition;
    }

    public Server() {
        this.serverDefinition = new ServerDefinition();
        this.serverDefinition.setPort(8080);
        this.serverDefinition.setListeningAddr("0.0.0.0");
    }

    /**
     * Start this server, listening to the specified http port.
     *
     *
     * */
    public void start() {
        LOGGER.info("Starting server at {}:{}", 
                this.serverDefinition.getListeningAddr(), this.serverDefinition.getPort());
    }

    public static void main(String[] args) {
        Server server = new Server(); // Create default server at 0.0.0.0:8080
        server.start();

        System.out.println("Welcome to my blocking server");
    }
}
