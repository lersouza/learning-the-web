package net.learning.bls;

public class ServerDefinition {
    private int port;
    private String listeningAddr;
    private int kernelBacklog; 
    private int clientQueueSize;
    private int poolCapacity;

    public static ServerDefinition newDefinition() {
        ServerDefinition serverDef = new ServerDefinition();
        
        int portNumber = Integer.parseInt(System.getProperty("httpPort", "8080"));
        int backlog = Integer.parseInt(System.getProperty("kernelBacklog", "10"));
        int queueSize = Integer.parseInt(System.getProperty("clientQueueSize", "30"));
        int poolCapacity = Integer.parseInt(System.getProperty("poolCapacity", "10"));
        String listeningAddr = System.getProperty("httpBind", "0.0.0.0");

        serverDef.setPort(portNumber);
        serverDef.setListeningAddr(listeningAddr);
        serverDef.setKernelBacklog(backlog);
        serverDef.setClientQueueSize(queueSize);
        serverDef.setPoolCapacity(poolCapacity);

        return serverDef;
    }

    public void setListeningAddr(String listeningAddr) {
        this.listeningAddr = listeningAddr;
    }

    public String getListeningAddr() {
        return listeningAddr;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getPort() {
        return port;
    }

    public void setKernelBacklog(int kernelBacklog) {
        this.kernelBacklog = kernelBacklog;
    }

    public int getKernelBacklog() {
        return kernelBacklog;
    }

    @Override
    public String toString() {
        return String.format("%s:%d, backlog=%d",
                getListeningAddr(),
                getPort(),
                getKernelBacklog());
    }

    public void setClientQueueSize(int clientQueueSize) {
        this.clientQueueSize = clientQueueSize;
    }

    public int getClientQueueSize() {
        return clientQueueSize;
    }

    public void setPoolCapacity(int poolCapacity) {
        this.poolCapacity = poolCapacity;
    }

    public int getPoolCapacity() {
        return poolCapacity;
    }
}
