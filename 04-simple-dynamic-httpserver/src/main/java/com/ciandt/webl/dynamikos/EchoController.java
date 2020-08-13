package com.ciandt.webl.dynamikos;

@Controller(path = "/echo")
public class EchoController {

    @Operation(method = "get")
    public HttpResponse get(
        @Param(name = "name") String name,
        @Param(name = "message") String message) {

        HttpResponse response = new HttpResponse(200, "OK");
        response.setBody(String.format("You, %s, sent this message: %s", name, message), "text/plain");

        return response;
    }
}