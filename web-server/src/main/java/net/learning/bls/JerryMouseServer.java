package net.learning.bls;

import java.util.ArrayList;
import java.util.List;

import net.learning.handlers.DummyHandler;

public class JerryMouseServer extends PoolServer {
    
    private List<RequestHandler> handlers;
    
    @Override
    protected WorkerThread createWorker(int workerId) {
        SmarterWorker smarter = new SmarterWorker(
            String.format("JerryMouse-SmarterWorker-%d", workerId),
            getIncomingConnections(),
            this.handlers);

        return smarter;
    }

    public void registerHandler(RequestHandler handler) {
        this.handlers.add(handler);
    }

    public JerryMouseServer(ServerDefinition definition) {
        super(definition);
        this.handlers = new ArrayList<>();
    }

    public static void main(final String[] args) {
        final ServerDefinition definition = ServerDefinition.newDefinition();
        final JerryMouseServer server = new JerryMouseServer(definition);

        server.registerHandler(new DummyHandler());

        server.start();
    }


}
