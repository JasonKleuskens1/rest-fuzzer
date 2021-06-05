package nl.ou.se.rest.fuzzer.components.fuzzer.metadata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.ou.se.rest.fuzzer.components.data.rmd.domain.RmdAction;
import nl.ou.se.rest.fuzzer.components.data.rmd.domain.RmdParameter;
import nl.ou.se.rest.fuzzer.components.fuzzer.executor.Authentication;
import nl.ou.se.rest.fuzzer.components.fuzzer.executor.BasicAuthentication;
import nl.ou.se.rest.fuzzer.components.shared.Constants;

@SuppressWarnings("unchecked")
public class MetaDataUtil {

    // variable(s)
    public abstract class Meta {
        // general
        public static final String CONFIGURATION = "configuration";

        public static final String AUTHENTICATION = "authentication";
        public static final String AUTH_METHOD = "method";
        public static final String AUTH_METHOD_BASIC = "BASIC";
        public static final String AUTH_USERNAME = "username";
        public static final String AUTH_PASSWORD = "password";

        public static final String INCLUDE_ACTIONS = "includeActions";
        public static final String EXCLUDE_ACTIONS = "excludeActions";
        public static final String ACTION = "action";
        public static final String ACTION_PATH = "path";
        public static final String ACTION_METHOD = "httpMethod";

        public static final String EXCLUDE_PARAMETERS = "excludeParameters";
        public static final String PARAMETER = "parameter";
        public static final String PARAMETER_NAME = "name";
        public static final String PARAMETER_REQUIRED = "required";

        public static final String DEFAULTS = "defaults";
        public static final String DEFAULT_VALUE = "default";

        // all fuzzers
        public static final String MAX_NUMBER_REQUESTS = "maxNumRequests";
        
        // basic fuzzer && dictionary fuzzer
        public static final String REPETITIONS = "repetitions";

        // model-based fuzzers
        public static final String MAX_SEQUENCE_LENGTH = "maxSequenceLength";
        public static final String DURATION = "duration";

        // (model-based) dictionary fuzzers
        public static final String DICTIONARIES = "dictionaries";
        public static final String MAX_DICTIONARY_PARAMS = "maxDictParams";
        public static final String MAX_DICTIONARY_ITEMS = "maxDictItems";

        // responses reporter
        public static final String POINTS_INTERVAL = "pointsInterval";
        public static final String X_TICK_INTERVAL = "xTickInterval";
        public static final String Y_TICK_INTERVAL = "yTickInterval";
    }

    private Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    private static final String TRUE = "TRUE";
    private static final String FALSE = "FALSE";

    private static final String ARRAY_START = "[";
    private static final String ARRAY_END = "]";
    private static final String EMPTY_STRING = "";
    private static final String ESCAPE_REGEX = "\\";

    private Map<String, Object> tuplesMetaData;
    private Map<String, Object> tuplesConfiguration; // Everything within the configuration block

    // constructor(s)
    public MetaDataUtil(Map<String, Object> tuplesMetaData) {
        this.tuplesMetaData = tuplesMetaData;
    }

    // method(s)
    public Boolean isValid(String... keys) {
        Boolean isValid = true;

        for (String key : keys) {
            if (!this.tuplesMetaData.containsKey(key)) {
                logger.error(String.format(Constants.Fuzzer.META_DATA_MISSING, MetaDataUtil.class, key));
                isValid = false;
            }
        }

        if (isValid && this.tuplesMetaData.containsKey(Meta.CONFIGURATION)) {
            this.tuplesConfiguration = ((JSONObject) this.getValueForKey(this.tuplesMetaData, Meta.CONFIGURATION))
                    .toMap();
        }

        return isValid;
    }

    public Integer getIntegerValue(String key) {
        return (Integer) this.getValueForKey(this.tuplesMetaData, key);
    }

    public List<Long> getLongArrayValues(String key) {
        List<Long> result = new ArrayList<>();

        JSONArray array = (JSONArray) this.getValueForKey(this.tuplesMetaData, key);
        array.forEach(intValue -> result.add(Long.valueOf((Integer)intValue)));

        return result;
    }

    public Authentication getAuthentication() {
        Authentication authentication = null;

        Map<String, String> authenticationMap = (Map<String, String>) this.getValueForKey(this.tuplesConfiguration,
                Meta.AUTHENTICATION);

        if (authenticationMap != null && !authenticationMap.isEmpty()) {
            String method = authenticationMap.get(Meta.AUTH_METHOD);
            switch (method) {
            case Meta.AUTH_METHOD_BASIC:
                String username = authenticationMap.get(Meta.AUTH_USERNAME);
                String password = authenticationMap.get(Meta.AUTH_PASSWORD);
                authentication = new BasicAuthentication(username, password);
                break;
            default:
                logger.error(String.format(Constants.Fuzzer.META_DATA_INVALID, 1, 2));
                break;
            }
        }

        return authentication;
    }

    public List<RmdAction> getFilteredActions(List<RmdAction> actions) {
        List<ConfigurationAction> includeActions = this.getIncludeActions();
        List<ConfigurationAction> excludeActions = this.getExcludeActions();
        
        if (includeActions != null && !includeActions.isEmpty()) {
            actions = actions.stream().filter(action -> isActionMatched(action, includeActions))
                    .collect(Collectors.toList());
        }

        if (excludeActions != null && !excludeActions.isEmpty()) {
            actions = actions.stream().filter(action -> !isActionMatched(action, excludeActions))
                    .collect(Collectors.toList());
        }

        logger.info(String.format(Constants.Fuzzer.NUMBER_OF_ACTIONS, actions.size()));

        List<ConfigurationParameter> excludeParameters = this.getExcludeParameters();

        if (excludeParameters != null && !excludeParameters.isEmpty()) {
            actions = actions.stream().map(action -> removeMatchedParameters(action, excludeParameters))
                    .collect(Collectors.toList());
        }

        return actions;
    }

    public List<RmdAction> getCorrectedActions(List<RmdAction> actions) {
        List<ConfigurationParameter> excludeParameters = this.getExcludeParameters();

        if (excludeParameters != null && !excludeParameters.isEmpty()) {
            actions = actions.stream().map(action -> removeMatchedParameters(action, excludeParameters))
                    .collect(Collectors.toList());
        }

        return actions;
    }    

    public Map<ConfigurationParameter, Object> getDefaults() {
        Map<ConfigurationParameter, Object> defaults = new HashMap<>();

        List<Map<String, Object>> defaultsMaps = (ArrayList<Map<String, Object>>) this
                .getValueForKey(this.tuplesConfiguration, Meta.DEFAULTS);

        if (defaultsMaps == null) {
            return defaults;
        }

        defaultsMaps.forEach(map -> {
            ConfigurationParameter cp = getParameter(map);
            Object value = getDefault(map);
            defaults.put(cp, value);
        });

        return defaults;
    }

    private List<ConfigurationAction> getIncludeActions() {
        List<Map<String, String>> actionsMap = (ArrayList<Map<String, String>>) getValueForKey(this.tuplesConfiguration,
                Meta.INCLUDE_ACTIONS);
        if (actionsMap == null) {
            return null;
        }
        return actionsMap.stream().map(m -> getAction(m)).collect(Collectors.toList());
    }

    private List<ConfigurationAction> getExcludeActions() {
        List<Map<String, String>> actionsMap = (ArrayList<Map<String, String>>) getValueForKey(this.tuplesConfiguration,
                Meta.EXCLUDE_ACTIONS);
        if (actionsMap == null) {
            return null;
        }
        return actionsMap.stream().map(m -> getAction(m)).collect(Collectors.toList());
    }

    private List<ConfigurationParameter> getExcludeParameters() {
        List<Map<String, Object>> parametersMap = (ArrayList<Map<String, Object>>) getValueForKey(
                this.tuplesConfiguration, Meta.EXCLUDE_PARAMETERS);
        if (parametersMap == null) {
            return null;
        }
        return parametersMap.stream().map(m -> getParameter(m)).collect(Collectors.toList());
    }

    private ConfigurationAction getAction(Map<String, String> actionMap) {
        String pathRegex = (String) actionMap.get(Meta.ACTION_PATH);
        String httpMethodRegex = (String) actionMap.get(Meta.ACTION_METHOD);

        return new ConfigurationAction(pathRegex, httpMethodRegex);
    }

    private ConfigurationParameter getParameter(Map<String, Object> map) {
        Map<String, String> parameterMap = (Map<String, String>) map.get(Meta.PARAMETER);
        String nameRegex = (String) parameterMap.get(Meta.PARAMETER_NAME);
        String requiredRegex = (String) parameterMap.get(Meta.PARAMETER_REQUIRED);

        Boolean required = null;
        if (requiredRegex != null) {
            switch (requiredRegex.toUpperCase()) {
            case TRUE:
                required = true;
                break;
            case FALSE:
                required = false;
                break;
            default:
                break;
            }
        }

        Map<String, String> actionMap = (Map<String, String>) map.get(Meta.ACTION);
        ConfigurationAction action = getAction(actionMap);

        return new ConfigurationParameter(nameRegex, required, action);
    }

    private Object getDefault(Map<String, Object> map) {
        String value = (String) map.get(Meta.DEFAULT_VALUE);

        // boolean
        if (TRUE.equalsIgnoreCase(value)) {
            return true;
        } else if (FALSE.equalsIgnoreCase(value)) {
            return false;
        }

        // integer
        Integer iValue = null;
        try {
            iValue = Integer.parseInt(value);
        } catch (Exception e) {
            // do nothing
        }

        if (iValue != null) {
            return iValue;
        }

        // array
        if (value.startsWith(ARRAY_START) && value.endsWith(ARRAY_END)) {
            value = value.replaceAll(ESCAPE_REGEX + ARRAY_START, EMPTY_STRING).replaceAll(ARRAY_END, EMPTY_STRING);
            List<String> array = new ArrayList<>();
            array.add(value);
            return array;
        }

        // string
        return value;
    }

    private RmdAction removeMatchedParameters(RmdAction action, List<ConfigurationParameter> parameters) {
        List<RmdParameter> parametersToRemove = new ArrayList<RmdParameter>();

        for (RmdParameter parameter : action.getParameters()) {
            for (ConfigurationParameter cp : parameters) {
                if (cp.getAction().matches(action) && cp.matches(parameter)) {
                    parametersToRemove.add(parameter);
                }
            }
        }

        action.getParameters().removeAll(parametersToRemove);

        return action;
    }

    private boolean isActionMatched(RmdAction action, List<ConfigurationAction> actions) {
        for (ConfigurationAction ca : actions) {
            if (ca.matches(action)) {
                return true;
            }
        }
        return false;
    }

    private Object getValueForKey(Map<String, Object> tuples, String key) {
        if (!tuples.containsKey(key)) {
            return null;
        }

        return tuples.get(key);
    }
}