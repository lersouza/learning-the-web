package com.ciandt.webl;

import java.io.IOException;

import com.ciandt.webl.aplos.AplosServer;

public class App {
    public static void main(String[] args) throws IOException {
        String server = "simple-tcp";

        if(args.length > 0) {
            server = args[0];
        }

        if("simple-tcp".equals(server)) {
            new SimpleTcpServer("localhost", 8080).run();
        }
        else if("simple-http".equals(server)) {
            new AplosServer("localhost", 8080).run();
        }
        else {
            System.out.println("Choose 'simple-tcp' or 'simple-http' server. You chose: " + server);
        }
    }
}