package com.ciandt.webl.dynamikos;

@Controller(path = "/json")
public class JsonController {

    @Operation(method = "get")
    public HttpResponse get(@Param(name = "code") String code) {
        if("123456".equals(code)) {
            return HttpResponse.NOT_FOUND;
        }

        HttpResponse response = new HttpResponse(200, "OK");
        response.setBody("{ \"yourCode\": \"" + code + "\" } ", "application/json");

        return response;
    }
    
}