package nl.ou.se.rest.fuzzer.components.extractor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.ou.se.rest.fuzzer.components.data.rmd.domain.DiscoveryModus;
import nl.ou.se.rest.fuzzer.components.data.rmd.domain.HttpMethod;
import nl.ou.se.rest.fuzzer.components.data.rmd.domain.ParameterContext;
import nl.ou.se.rest.fuzzer.components.data.rmd.domain.RmdAction;
import nl.ou.se.rest.fuzzer.components.data.rmd.domain.RmdActionDependency;
import nl.ou.se.rest.fuzzer.components.data.rmd.domain.RmdParameter;
import nl.ou.se.rest.fuzzer.components.data.rmd.domain.RmdSut;
import nl.ou.se.rest.fuzzer.components.data.rmd.factory.RmdActionDependencyFactory;

public class DependencyFinder {

    // variable(s)
    private Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    private static final String ID = "id";

    private static final String EMPTY_STRING = "";
    private static final String PATH_DEPENDENCY_FORMAT = "{%s}";
    private static final String ID_DEPENDENCIES_UNDERSCORE_ID = "_id";
    private static final List<String> PATH_DEPENDENCY_NAMES = Arrays.asList(new String[] { "id", "parent" });

    private RmdActionDependencyFactory actionDependencyFactory = new RmdActionDependencyFactory();

    private RmdSut sut;
    private Set<RmdActionDependency> actionDependencies = new TreeSet<>();

    // constructor(s)
    public DependencyFinder(RmdSut sut) {
        this.sut = sut;
    }

    // method(s)
    public void process() {
        this.sut.getActions().forEach(a -> a.getParameters().forEach(p -> processParameter(p)));
    }

    public Set<RmdActionDependency> getActionDepencies() {
        return this.actionDependencies;
    }

    private void processParameter(RmdParameter parameter) {
        Optional<RmdAction> actionDependsOn = getDependencyForParameter(parameter);

        if (actionDependsOn.isPresent()) {
            actionDependencies.add(actionDependencyFactory
                    .create(DiscoveryModus.AUTOMATIC, parameter.getAction(), parameter, actionDependsOn.get(), ID)
                    .build());
        }
    }

    private Optional<RmdAction> getDependencyForParameter(RmdParameter parameter) {
        String actionPath = null;

        if (isDependencyFromPath(parameter)) {
            actionPath = getActionPathFromPath(parameter);
        } else if (isDependencyFromOtherEntity(parameter)) {
            actionPath = getAchtionPathFromOtherEntity(parameter);
        } else if (isFromPath(parameter)) {
            logger.info(String.format("%s \t %s \t %s -> %s", parameter.getAction().getHttpMethod(),
                    parameter.getAction().getPath(), parameter.getName(), "NOT FOUND"));
        }

        return findActionForNameAndHttpMethod(actionPath, HttpMethod.POST);
    }

    private Optional<RmdAction> findActionForNameAndHttpMethod(String actionPath, HttpMethod httpMethod) {
        return this.sut.getActions().stream()
                .filter(a -> actionPath != null && actionPath.equals(a.getPath()) && httpMethod == a.getHttpMethod())
                .findFirst();
    }

    private String getActionPathFromPath(RmdParameter parameter) {
        List<String> uriParts = new ArrayList<String>(Arrays.asList(parameter.getAction().getPath().split("/")));
        int idx = uriParts.indexOf(String.format(PATH_DEPENDENCY_FORMAT, parameter.getName()));
        uriParts = uriParts.subList(0, idx);
        String uri = uriParts.stream().collect(Collectors.joining("/"));
        uri = uri.replace("{parent}", "{id}");
        return uri;
    }

    private String getAchtionPathFromOtherEntity(RmdParameter parameter) {
        Predicate<String> isParameter = item -> item.indexOf("{") != -1;
        List<String> uriParts = new ArrayList<String>(Arrays.asList(parameter.getAction().getPath().split("/")));
        uriParts.removeIf(isParameter);
        uriParts.remove(uriParts.size() - 1);
        String uri = uriParts.stream().collect(Collectors.joining("/"));

        String entityName = parameter.getName().replaceAll(ID_DEPENDENCIES_UNDERSCORE_ID, EMPTY_STRING) + "s";

        return uri + "/" + entityName;
    }

    private Boolean isDependencyFromPath(RmdParameter parameter) {
        return ParameterContext.PATH == parameter.getContext() && PATH_DEPENDENCY_NAMES.contains(parameter.getName());
    }

    private Boolean isFromPath(RmdParameter parameter) {
        return ParameterContext.PATH == parameter.getContext();
    }

    private Boolean isDependencyFromOtherEntity(RmdParameter parameter) {
        return parameter.getName().indexOf(ID_DEPENDENCIES_UNDERSCORE_ID) != -1;
    }
}