package nl.ou.se.rest.fuzzer.components.data.fuz.factory;

import nl.ou.se.rest.fuzzer.components.data.fuz.domain.FuzConfiguration;

public class FuzConfigurationFactory {

    private FuzConfiguration configuration;

    public FuzConfigurationFactory create(String name, String items) {
        configuration = new FuzConfiguration(name, items); 
        return this;
    }

    public FuzConfiguration build() {
        return this.configuration;
    }
}