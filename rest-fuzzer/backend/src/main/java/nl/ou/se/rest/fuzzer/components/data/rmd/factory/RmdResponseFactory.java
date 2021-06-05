package nl.ou.se.rest.fuzzer.components.data.rmd.factory;

import java.time.LocalDateTime;

import nl.ou.se.rest.fuzzer.components.data.rmd.domain.RmdResponse;

public class RmdResponseFactory {

    // variable(s)
    private RmdResponse response;

    // method(s)
    public RmdResponseFactory create(Integer statusCode, String description) {
        response = new RmdResponse(statusCode, description);
        response.setCreatedAt(LocalDateTime.now());
        return this;
    }

    public RmdResponse build() {
        return this.response;
    }
}