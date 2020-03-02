package net.learning.exercises;

import net.learning.bls.JerryMouseServer;

public class Exercise03 {
     /**
     * In this exercise we'll create a handler for the JerryMouseServer.
     * The handler will respond to /weather path.
     *
     * It will send back to the client a JSON with the weather
     * 
     * Example:
     *
     * GET /weather HTTP/1.1
     * Host: anyhost.org
     * 
     * =======
     *
     * HTTP/1.1 200 OK
     * Content-Length: <length>
     * Content-Type: application/json
     *
     * { weather: { temperature: 35 } }
     *  
     *
     *  
     * @param args
     */
   public static void main(String[] args) {
      // Implement the below WeatherHandler class to perform the task

     // Create a new instance of the JerryMouseServer
     //
     JerryMouseServer server = null;

     // Create a new instance of the WeatherHandler
     //
     WeatherHandler weatherHandler = null;
     

     // Register the weather handler class in the JerryMouseServer
     //

     // Call the JerryMouseServer.start()
   }
}
