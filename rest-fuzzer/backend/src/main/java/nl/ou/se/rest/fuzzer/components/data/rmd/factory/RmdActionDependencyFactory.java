package nl.ou.se.rest.fuzzer.components.data.rmd.factory;

import java.time.LocalDateTime;

import nl.ou.se.rest.fuzzer.components.data.rmd.domain.DiscoveryModus;
import nl.ou.se.rest.fuzzer.components.data.rmd.domain.RmdAction;
import nl.ou.se.rest.fuzzer.components.data.rmd.domain.RmdActionDependency;
import nl.ou.se.rest.fuzzer.components.data.rmd.domain.RmdParameter;

public class RmdActionDependencyFactory {

    // variable(s)
    private RmdActionDependency actionDependency;

    // constructor(s)
    public RmdActionDependencyFactory create(DiscoveryModus discoveryModus, RmdAction action, RmdParameter parameter,
            RmdAction actionDependsOn, String parameterDependsOn) {
        actionDependency = new RmdActionDependency(discoveryModus, action, parameter, actionDependsOn, parameterDependsOn);
        actionDependency.setCreatedAt(LocalDateTime.now());
        return this;
    }

    // method(s)
    public RmdActionDependency build() {
        return actionDependency;
    }
}