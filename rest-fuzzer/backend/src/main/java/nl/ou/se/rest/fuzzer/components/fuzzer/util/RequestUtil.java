package nl.ou.se.rest.fuzzer.components.fuzzer.util;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import nl.ou.se.rest.fuzzer.components.data.fuz.domain.FuzProject;
import nl.ou.se.rest.fuzzer.components.data.fuz.domain.FuzRequest;
import nl.ou.se.rest.fuzzer.components.data.fuz.domain.FuzSequence;
import nl.ou.se.rest.fuzzer.components.data.fuz.factory.FuzRequestFactory;
import nl.ou.se.rest.fuzzer.components.data.rmd.domain.ParameterContext;
import nl.ou.se.rest.fuzzer.components.data.rmd.domain.RmdAction;
import nl.ou.se.rest.fuzzer.components.fuzzer.metadata.ConfigurationParameter;

@Service
public class RequestUtil {

    // variable(s)
    private static FuzRequestFactory requestFactory = new FuzRequestFactory();

    @Autowired
    private ParameterUtil parameterUtil;
    
    private FuzProject project;

    // method(s)
    public void init(FuzProject project, Map<ConfigurationParameter, Object> defaults) {
        this.project = project;
        this.parameterUtil.init(defaults);
    }

    public FuzRequest getRequestFromAction(RmdAction action, FuzSequence sequence) {
        requestFactory.create(this.project, action);

        for (ParameterContext context : ParameterContext.values()) {
            requestFactory.addParameterMap(context,
                    parameterUtil.createParameterMap(action.getParametersByContext(context), sequence));
        }
        // 15. build the valid input
        return requestFactory.build();
    }
}