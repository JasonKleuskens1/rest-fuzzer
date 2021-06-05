package nl.ou.se.rest.fuzzer.components.data.rmd.factory;

import nl.ou.se.rest.fuzzer.components.data.rmd.domain.RmdAction;
import nl.ou.se.rest.fuzzer.components.data.rmd.domain.RmdParameter;
import nl.ou.se.rest.fuzzer.components.data.rmd.domain.RmdResponse;

public class RmdActionFactory {

    // variable(s)
    private RmdAction action;

    // constructor(s)
    public RmdActionFactory create(String url, String httpMethod) {
        action = new RmdAction(url, httpMethod);
        return this;
    }

    // method(s)
    public RmdActionFactory addParameter(RmdParameter parameter) {
        parameter.setPosition(action.getParameters().size() + 1);
        action.addParameter(parameter);
        return this;
    }

    public RmdActionFactory addResponse(RmdResponse response) {
        action.addResponse(response); 
        return this;
    }

    public RmdAction build() {
        return action;
    }
}