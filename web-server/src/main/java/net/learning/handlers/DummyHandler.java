package net.learning.handlers;


import net.learning.bls.HttpRequest;
import net.learning.bls.HttpResponse;
import net.learning.bls.RequestHandler;

public class DummyHandler implements RequestHandler {
    public String getPathRegex() {
        return "(.*)";
    }

    public HttpResponse handleRequest(HttpRequest request) {
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
