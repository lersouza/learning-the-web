package com.ciandt.webl.aplos;

public class HttpServletRegistry {
    private final HttpServlet defaultServlet;

    public HttpServletRegistry() {
        this.defaultServlet = new StaticHttpServlet();   
    }

    public HttpServlet forRequest(HttpRequest request) {
        return this.defaultServlet;
    }
}