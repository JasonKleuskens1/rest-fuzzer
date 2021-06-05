package nl.ou.se.rest.fuzzer.components.fuzzer.metadata;

import nl.ou.se.rest.fuzzer.components.data.rmd.domain.RmdParameter;

public class ConfigurationParameter {

    // variable(s)
    private String nameRegex;
    private Boolean required;
    private ConfigurationAction action;

    // constructor(s)
    public ConfigurationParameter(String nameRegex, Boolean required, ConfigurationAction action) {
        this.nameRegex = nameRegex;
        this.required = required;
        this.action = action;
    }

    // method(s)
    public Boolean matches(RmdParameter parameter) {
        if (this.required == null) {
            return parameter.getName().matches(this.nameRegex);
        }

        return parameter.getName().matches(this.nameRegex) && parameter.getRequired() == this.required;
    }

    // getter(s) and setter(s)
    public String getNameRegex() {
        return nameRegex;
    }

    public void setNameRegex(String nameRegex) {
        this.nameRegex = nameRegex;
    }

    public Boolean getRequired() {
        return required;
    }

    public void setRequired(Boolean required) {
        this.required = required;
    }

    public ConfigurationAction getAction() {
        return action;
    }

    public void setAction(ConfigurationAction action) {
        this.action = action;
    }
}