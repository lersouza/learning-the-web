package com.ciandt.webl.aplos;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HttpRequest {
    private static final Pattern REQUEST_LINE = Pattern
            .compile("(GET|POST|PUT|PATCH|OPTIONS|DELETE|TRACE|CONNECT|HEAD) (.*) (HTTP/1.1)");
    private static final Pattern HEADER_ENTRY = Pattern.compile("(.*):\\s*(.*)");

    private String method;
    private String path;
    private String protocolVersion;
    private Map<String, String> headers = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    private String body;

    public static HttpRequest fromInputStream(InputStream inputStream) throws MalformedRequestException, IOException {
        HttpRequest request = new HttpRequest();

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String firstLine = reader.readLine();
                    
        if(firstLine == null) {
            throw new MalformedRequestException("No request line");
        }
    
        Matcher requestLineMatcher = REQUEST_LINE.matcher(firstLine);
   
        if(!requestLineMatcher.matches())
            throw new MalformedRequestException("Bad request line: " + firstLine);

        request.method = requestLineMatcher.group(1).toString();
        request.path = requestLineMatcher.group(2).toString();
        request.protocolVersion = requestLineMatcher.group(3).toString();

        String headerLine = null;
        
        while((headerLine = reader.readLine())  != null && !headerLine.isEmpty()) {
    
            Matcher headerMatcher = HEADER_ENTRY.matcher(headerLine);

            if(!headerMatcher.matches())
                throw new MalformedRequestException(String.format("Invalid header: %s", headerLine));

            request.headers.put(headerMatcher.group(1).toString(), headerMatcher.group(2).toString());

        }

        if(request.headers.containsKey("Content-Length")) {
            //There is a body out there!
            int contentLength = Integer.parseInt(request.headers.get("Content-Length"));
            StringBuilder bodyBuilder = new StringBuilder(contentLength);
            
            for(int s = 0; s < contentLength; s++) {
                bodyBuilder.append((char)reader.read());
            }

            request.body = bodyBuilder.toString();
        }
        
        return request;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getProtocolVersion() {
        return protocolVersion;
    }

    public void setProtocolVersion(String protocolVersion) {
        this.protocolVersion = protocolVersion;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getUserAgent() {
        return this.headers.getOrDefault("User-Agent", "");
    }
}