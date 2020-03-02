package net.learning.exercises;

import net.learning.bls.HttpRequest;
import net.learning.bls.HttpResponse;
import net.learning.bls.RequestHandler;;

public class WeatherHandler implements RequestHandler {
    @Override
    public String getPathRegex() {
        //TODO responds to /weather requests
        return null;
    }

    @Override
    public HttpResponse handleRequest(HttpRequest request) {
        // TODO return a JSON with some weather info
       // JSON example: { weather: { temperature: 35 } } 

        return null;
    }
}
