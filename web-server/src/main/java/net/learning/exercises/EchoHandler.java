package net.learning.exercises;

import net.learning.bls.HttpRequest;
import net.learning.bls.HttpResponse;
import net.learning.bls.RequestHandler;

public class EchoHandler implements RequestHandler {

    public EchoHandler() {
    }

    @Override
    public String getPathRegex() {
        return null;
    }

    @Override
    public HttpResponse handleRequest(HttpRequest request) {
        // TODO Auto-generated method stub
        return null;
    }
}

