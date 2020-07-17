package com.ciandt.webl.aplos;


public class MalformedRequestException extends Exception {
    private static final long serialVersionUID = 1L;

    public MalformedRequestException(String reason) {
        super("Request is malformed: " + reason);
    }
}
