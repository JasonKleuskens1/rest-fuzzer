package nl.ou.se.rest.fuzzer.components.data.fuz.factory;

import nl.ou.se.rest.fuzzer.components.data.fuz.domain.FuzProject;
import nl.ou.se.rest.fuzzer.components.data.fuz.domain.FuzSequence;

public class FuzSequenceFactory {

    private FuzSequence sequence;

    public FuzSequenceFactory create(int position, FuzProject project) {
        sequence = new FuzSequence(position, project);
        return this;
    }

    public FuzSequence build() {
        return sequence;
    }
}