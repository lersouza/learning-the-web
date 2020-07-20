package com.ciandt.webl.dynamikos;

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
            .compile("(GET|POST|PUT|PATCH|OPTIONS|DELETE|TRACE|CONNECT|HEAD) (.+) (HTTP/1.1)");
    private static final Pattern HEADER_ENTRY = Pattern.compile("(.*):\\s*(.*)");
    private static final Pattern PARAMETERS = Pattern.compile("([\\w\\%]+)=([\\w\\%]+)&{0,1}");

    private String method;
    private String path;
    private String protocolVersion;
    private final Map<String, String> headers = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    private final Map<String, String> parameters = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    private String body;

    public static HttpRequest fromInputStream(final InputStream inputStream)
            throws MalformedRequestException, IOException {
        final HttpRequest request = new HttpRequest();

        final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        final String firstLine = reader.readLine();

        if (firstLine == null) {
            throw new MalformedRequestException("No request line");
        }

        final Matcher requestLineMatcher = REQUEST_LINE.matcher(firstLine);

        if (!requestLineMatcher.matches())
            throw new MalformedRequestException("Bad request line: " + firstLine);

        request.method = requestLineMatcher.group(1).toString();
        request.path = requestLineMatcher.group(2).toString();
        request.protocolVersion = requestLineMatcher.group(3).toString();

        String headerLine = null;

        while ((headerLine = reader.readLine()) != null && !headerLine.isEmpty()) {

            final Matcher headerMatcher = HEADER_ENTRY.matcher(headerLine);

            if (!headerMatcher.matches())
                throw new MalformedRequestException(String.format("Invalid header: %s", headerLine));

            request.headers.put(headerMatcher.group(1).toString(), headerMatcher.group(2).toString());

        }

        if (request.path.contains("?")) {
            System.out.println("Path=" + request.path);
            final String[] pathParts = request.path.split("\\?");

            if (pathParts.length > 1) {
                request.path = pathParts[0];
                request.processParameters(pathParts[1]);
            }
        }

        if (request.headers.containsKey("Content-Length")) {
            // There is a body out there!
            final int contentLength = Integer.parseInt(request.headers.get("Content-Length"));
            final StringBuilder bodyBuilder = new StringBuilder(contentLength);

            for (int s = 0; s < contentLength; s++) {
                bodyBuilder.append((char) reader.read());
            }

            request.body = bodyBuilder.toString();

            if("application/x-www-form-urlencoded".equals(request.getHeader("Content-Type"))) {
                request.processParameters(request.body);
            }
        }

        return request;
    }

    private void processParameters(String paramString) {
        final Matcher paramsMatcher = PARAMETERS.matcher(paramString);

        while(paramsMatcher.find()) {
            String key = paramsMatcher.group(1);
            String values = paramsMatcher.group(2);

            this.parameters.put(key, values);
        }
    }

    public String getBody() {
        return body;
    }

    public void setBody(final String body) {
        this.body = body;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getHeader(String header) {
        return this.headers.getOrDefault(header, null);
    }

    public String getProtocolVersion() {
        return protocolVersion;
    }

    public void setProtocolVersion(final String protocolVersion) {
        this.protocolVersion = protocolVersion;
    }

    public String getPath() {
        return path;
    }

    public void setPath(final String path) {
        this.path = path;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(final String method) {
        this.method = method;
    }

    public String getUserAgent() {
        return this.headers.getOrDefault("User-Agent", "");
    }

    public String getParameter(final String paramName) {
        return this.parameters.getOrDefault(paramName, null);
    }
}