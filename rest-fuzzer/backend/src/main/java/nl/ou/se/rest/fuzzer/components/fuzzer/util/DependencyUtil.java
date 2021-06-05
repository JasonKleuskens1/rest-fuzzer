package nl.ou.se.rest.fuzzer.components.fuzzer.util;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import nl.ou.se.rest.fuzzer.components.data.fuz.dao.FuzResponseService;
import nl.ou.se.rest.fuzzer.components.data.fuz.domain.FuzResponse;
import nl.ou.se.rest.fuzzer.components.data.fuz.domain.FuzSequence;
import nl.ou.se.rest.fuzzer.components.data.rmd.dao.RmdActionDependencyService;
import nl.ou.se.rest.fuzzer.components.data.rmd.domain.RmdActionDependency;
import nl.ou.se.rest.fuzzer.components.data.rmd.domain.RmdParameter;
import nl.ou.se.rest.fuzzer.components.shared.JsonUtil;

@Service
public class DependencyUtil {

    @Autowired
    RmdActionDependencyService actionDependencyService;

    @Autowired
    FuzResponseService responseService;

    // method(s)
    public boolean hasDependency(RmdParameter parameter, FuzSequence sequence) {
        RmdActionDependency actionDependency = actionDependencyService.findByParameterId(parameter.getId());
        return actionDependency != null;
    }

    public Object getValueFromPreviousRequestInSequence(RmdParameter parameter, FuzSequence sequence) {
        RmdActionDependency actionDependency = actionDependencyService.findByParameterId(parameter.getId());
        List<FuzResponse> responses = responseService.findBySequenceId(sequence.getId());

        FuzResponse response = null;
        for (FuzResponse r : responses) {
            if (r.getRequest().getAction().equals(actionDependency.getActionDependsOn())) {
                response = r;
                break;
            }
        }
        
        if (response == null) {
            return null;
        }

        Object valueFromResponse = getValueFromResponse(response.getBody(), actionDependency.getParameterDependsOn());

        return valueFromResponse;
    }

    private Object getValueFromResponse(String body, String parameterDependsOn) {
        Object object = null;
        Map<String, Object> responseObjects = JsonUtil.stringToMap(body);
        if (responseObjects.containsKey(parameterDependsOn)) {
            object = responseObjects.get(parameterDependsOn);
        } else {
            System.out.println(body);
            System.out.println(parameterDependsOn);
        }

        return object;
    }
}