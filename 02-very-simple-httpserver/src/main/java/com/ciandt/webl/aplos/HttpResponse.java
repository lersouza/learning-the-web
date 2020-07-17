package com.ciandt.webl.aplos;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.TreeMap;

public class HttpResponse {
    private int statusCode;
    private String statusMessage;
    private Map<String, String> headers = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    private byte[] body;

    // Respostas Comuns
    public static HttpResponse BAD_REQUEST = new HttpResponse(400, "Bad Request");
    public static HttpResponse NOT_FOUND = new HttpResponse(404, "Not Found");
    public static HttpResponse METHOD_NOT_ALLOWED = new HttpResponse(405, "Method Not Allowed");
    public static HttpResponse INTERNAL_SERVER_ERROR = new HttpResponse(500, "Internal Server Error");

    public HttpResponse(int statusCode, String statusMessage) {
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
    }

    public void writeTo(OutputStream out) throws IOException {
        writeln(out, "HTTP/1.1 %d %s", getStatusCode(), getStatusMessage());

        for(String header : this.headers.keySet()) {
            writeln(out, "%s: %s", header, this.headers.get(header));
        }
        if(getBody() != null && getBody().length > 0) {
            writeln(out);
            out.write(getBody());
        }

        out.flush();
    }

    private void writeln(OutputStream out, String content, Object ...args) throws IOException {
        if (args != null && args.length > 0) {
            content = String.format(content, args);
        }
        out.write(content.getBytes());
        writeln(out);
    }

    private void writeln(OutputStream out) throws IOException {
        out.write("\n".getBytes());
    }

    public int getStatusCode() {
        return statusCode;
    }

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body, String contentType) {
        int contentLength = body != null ? body.length : 0;
        String contentTypeHeader = contentType != null && !contentType.isEmpty() ? contentType : "text/html";

        this.body = body;

        this.addHeader("Content-Length", String.valueOf(contentLength));
        this.addHeader("Content-Type", contentTypeHeader);
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void addHeader(String header, String headerValue) {
        this.headers.put(header, headerValue);
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }
    
}