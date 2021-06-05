package nl.ou.se.rest.fuzzer.components.data.rmd.factory;

import java.time.LocalDateTime;
import java.util.Map;

import nl.ou.se.rest.fuzzer.components.data.rmd.domain.RmdParameter;

public class RmdParameterFactory {

    // variable(s)
    private RmdParameter parameter;

    // method(s)
    public RmdParameterFactory create(String name, boolean required, String description, String type, String context) {
        parameter = new RmdParameter(-1, name, required, description, type.toUpperCase(), context.toUpperCase());
        parameter.setCreatedAt(LocalDateTime.now());
        return this;
    }

    public RmdParameterFactory setMetaDataTuples(Map<String, Object> metaDataTuples) {
        parameter.setMetaDataTuples(metaDataTuples);
        return this;
    }

    public RmdParameter build() {
        return parameter;
    }
}