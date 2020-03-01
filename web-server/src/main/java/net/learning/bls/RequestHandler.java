package net.learning.bls;

public interface RequestHandler {

    /**
     * Returns a regular expression for paths that this handler should handle.
     *
     * @return A string containing the Regular Expression.
     */
    String getPathRegex();

    /**
     * Handles a request and produces a response to the client.
     *
     * @param request The request to be handled.
     * @return A HttpResponse object to send back to client.
     */
    HttpResponse handleRequest(HttpRequest request);
}
