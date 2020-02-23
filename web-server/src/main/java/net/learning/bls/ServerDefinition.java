package net.learning.bls;

public class ServerDefinition {
    private int port;
    private String listeningAddr;

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
}
