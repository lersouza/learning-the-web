package net.learning.bls;

import java.net.Socket;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SmarterWorker extends WorkerThread {
    private List<RequestHandler> handlers;

    @Override
    protected HttpResponse handleRequest(HttpRequest request) {
        HttpResponse response = null;

        for(RequestHandler handler : this.handlers) {
            Pattern pattern = Pattern.compile(handler.getPathRegex());
            Matcher matcher = pattern.matcher(request.getPath());

            if(matcher.matches()) {
                response = handler.handleRequest(request);
                break;
            }
        }

        if(response == null) {
            response = new HttpResponse();
            response.setStatus(501, "Not Implemented");
        }

        return response;
    }

    public SmarterWorker(String name, BlockingQueue<Socket> incomingConnections, List<RequestHandler> handlers) {
        super(name, incomingConnections);
        this.handlers = handlers;
    }

}
