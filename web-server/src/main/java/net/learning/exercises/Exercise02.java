package net.learning.exercises;

import net.learning.bls.JerryMouseServer;

public class Exercise02 {
    /**
     * In this exercise we'll create a handler for the JerryMouseServer.
     * The handler will respond to /echo path.
     *
     * It will send back to the client the body it received
     * 
     * Example:
     *
     * POST /echo HTTP/1.1
     * Host: anyhost.org
     * Content-Length: 2
     * 
     * Hi
     *
     * =======
     *
     * HTTP/1.1 200 OK
     * Content-Length: 2
     * Content-Type: text/html
     *
     * Hi
     *
     *  
     * @param args
     */
    public static void main(String[] args) {
        // Implement the below EchoHandler class to perform the task

        // Create a new instance of the JerryMouseServer
        //
        JerryMouseServer server = null;

        // Create a new instance of the EchoHandler
        //
        EchoHandler echoHandler = null;
        

        // Register the echo handler class in the JerryMouseServer
        //

        // Call the JerryMouseServer.start()
    }

}
