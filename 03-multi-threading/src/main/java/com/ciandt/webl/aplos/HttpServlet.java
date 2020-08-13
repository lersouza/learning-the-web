package com.ciandt.webl.aplos;

public interface HttpServlet {

    String path();

    void doGet(HttpRequest request, HttpResponse response);

    void doPost(HttpRequest request, HttpResponse response);
   
}