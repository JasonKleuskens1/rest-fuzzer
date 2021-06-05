package nl.ou.se.rest.fuzzer.components.fuzzer.metadata;

import nl.ou.se.rest.fuzzer.components.data.rmd.domain.RmdAction;

public class ConfigurationAction {

    // variable(s)
    private String pathRegex;
    private String httpMethodRegex;

    // constructor(s)
    public ConfigurationAction(String pathRegex, String httpMethodRegex) {
        this.pathRegex = pathRegex;
        this.httpMethodRegex = httpMethodRegex;
    }

    // method(s)
    public Boolean matches(RmdAction action) {
        if ((action.getPath().matches(this.pathRegex))
                && (action.getHttpMethod().toString().matches(this.httpMethodRegex))) {
            return true;
        }

        return false;
    }

    // getter(s) and setter(s)
    public String getPathRegex() {
        return pathRegex;
    }

    public void setPathRegex(String pathRegex) {
        this.pathRegex = pathRegex;
    }

    public String getHttpMethodRegex() {
        return httpMethodRegex;
    }

    public void setHttpMethodRegex(String httpMethodRegex) {
        this.httpMethodRegex = httpMethodRegex;
    }
}