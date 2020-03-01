package net.learning.bls;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Map;
import java.util.TreeMap;

public class HttpResponse {
    private int statusCode;
    private String statusMessage;
    private Map<String, String> headers;
    private String body;

    public HttpResponse() {
        this.headers = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    }

    public void writeTo(OutputStream responseStream) {
        PrintWriter writer = new PrintWriter(responseStream);

        // Write the response status line
        writer.printf("HTTP/1.1 %d %s\r\n", getStatusCode(), getStatusMessage());

        for(String header : this.headers.keySet()) {
            writer.printf("%s: %s\r\n", header, this.headers.get(header));
        }

        writer.println(); // End of mandatory message

        if(getBody() != null && !getBody().isEmpty()) {
            writer.print(getBody());
        }

        writer.flush();
    }

    public void setStatus(int statusCode, String statusMessage) {
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getStatusMessage() {
        return this.statusMessage;
    }

    public void addHeader(String header, String headerValue) {
        this.headers.put(header, headerValue);
    }

    public String getHeader(String header) {
        return headers.getOrDefault(header, "");
    }

    public void setBody(String body, String contentType) {
        int contentLength = body != null ? body.length() : 0;
        String contentTypeHeader = contentType != null && !contentType.isEmpty() ? contentType : "text/html";

        this.body = body;
        this.addHeader("Content-Length", String.valueOf(contentLength));
        this.addHeader("Content-Type", contentTypeHeader);
    }

    public String getBody() {
        return body;
    }
}
