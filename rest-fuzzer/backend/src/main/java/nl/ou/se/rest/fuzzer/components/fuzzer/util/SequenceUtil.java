package nl.ou.se.rest.fuzzer.components.fuzzer.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import nl.ou.se.rest.fuzzer.components.data.rmd.domain.RmdAction;
import nl.ou.se.rest.fuzzer.components.data.rmd.domain.RmdActionDependency;

public class SequenceUtil {

    // variable(s)
    private static final String SEPERATOR = ",";
    private static final int NUM_SEQUENCES_SINGLE_LENGTH = 3;

    private List<RmdAction> allActions = new ArrayList<>();
    private List<RmdAction> filteredActions = new ArrayList<>();
    private List<RmdActionDependency> dependencies = new ArrayList<>();

    private Map<Long, List<Long>> mappedDepencies = new HashMap<>();
    private Map<Long, RmdAction> mappedActions = new HashMap<>();

    public SequenceUtil(List<RmdAction> allActions, List<RmdAction> filteredActions, List<RmdActionDependency> dependencies) {
        this.allActions = allActions;
        this.filteredActions = filteredActions;
        this.dependencies = dependencies;
        this.init();
    }

    // method(s)
    private void init() {
        // initialise mapped dependencies
        dependencies.forEach(d -> {
            List<Long> dependsOnActionIds = new ArrayList<>();
            Long actionId = d.getAction().getId();
            if (this.mappedDepencies.containsKey(actionId)) {
                dependsOnActionIds = this.mappedDepencies.get(actionId);
            }
            dependsOnActionIds.add(d.getActionDependsOn().getId());
            this.mappedDepencies.put(actionId, dependsOnActionIds);
        });

        // initialise mapped actions
        this.allActions.forEach(a -> {
            this.mappedActions.put(a.getId(), a);
        });
    }

    public List<String> getValidSequences(Integer sequenceLength) {
        List<String> sequences = new ArrayList<String>();
        for (int sequence = 1; sequence <= sequenceLength; sequence++) {
            sequences = this.createNewSequences(sequences);
        }

        return sequences;
    }

    public List<RmdAction> getDependentActions(RmdAction action) {
        List<RmdAction> actions = new ArrayList<>();
        actions.add(action);
        List<RmdAction> allActions = new ArrayList<>();
        allActions = getDependentActions(actions);
        Collections.reverse(allActions);
        return allActions;
    }

    private List<RmdAction> getDependentActions(List<RmdAction> actions) {
        List<RmdAction> dependentActions = new ArrayList<>(); 

        for (RmdAction action : actions) {
            if (this.mappedDepencies.containsKey(action.getId())) {
                List<Long> dependencies = this.mappedDepencies.get(action.getId());
                dependentActions.addAll(dependencies.stream().map(actionId -> this.mappedActions.get(actionId)).collect(Collectors.toList()));            
            }
        }

        // only unique results
        dependentActions = dependentActions.stream().distinct().collect(Collectors.toList());

        if (!dependentActions.isEmpty()) {
            actions.addAll(getDependentActions(dependentActions));
            return actions;
        } else {
            return actions;
        }
    }

    private List<String> createNewSequences(List<String> sequences) {
        List<String> newSequences = new ArrayList<String>();
        if (sequences.isEmpty()) {
            newSequences = this.filteredActions.stream().map(a -> a.getId().toString()).filter(s -> satisfiesAllDependencies(s))
                    .collect(Collectors.toList());
        } else {
            for (String sequence : sequences) {
                for (RmdAction action : this.filteredActions) {
                    String newSequence = sequence.concat(SEPERATOR + action.getId().toString());
                    if (satisfiesAllDependencies(newSequence)) {
                        newSequences.add(newSequence);
                    }
                }
            }
        }

        sequences.addAll(newSequences);

        return sequences;
    }

    private Boolean satisfiesAllDependencies(String sequence) {
        String[] actionStringIds = sequence.split(",");
        List<Long> actionIds = Arrays.asList(actionStringIds).stream().map(id -> Long.parseLong(id))
                .collect(Collectors.toList());

        // only check last item, the sequence -1 is already checked and thus valid
        return satisfiesDependenciesForLastItem(actionIds);
    }

    private Boolean satisfiesDependenciesForLastItem(List<Long> actionIds) {
        List<Long> requiredDependencies = new ArrayList<>();
        Long actionId = actionIds.get(actionIds.size() - 1);

        if (this.mappedDepencies.containsKey(actionId)) {
            this.mappedDepencies.get(actionId).forEach(id -> { requiredDependencies.add(id); });
        }

        if (!requiredDependencies.isEmpty() && actionIds.size() > 1) {
            List<Long> dependenciesInSequence = actionIds.subList(0, actionIds.size() - 1);
            requiredDependencies.removeIf(d -> dependenciesInSequence.contains(d));
        }

        return requiredDependencies.isEmpty();
    }

    public List<RmdAction> getActionsFromSequence(String sequence) {
        List<RmdAction> actions = new ArrayList<>();
        String[] actionIds = sequence.split(SEPERATOR);

        for (String id : actionIds) {
            actions.add(this.mappedActions.get(Long.valueOf(id)));
        }

        return actions;
    }

    public int getNumberOfRequests(List<String> sequences) {
        return sequences.stream().mapToInt(s -> {
            return s.split(SEPERATOR).length;
        }).sum();
    }

    public List<String> getRandomSequences(List<String> sequences, Integer maxSize) {
        List<String> result = new ArrayList<>();

        // put a number of sequences to the result with length == 1
        while (result.size() < NUM_SEQUENCES_SINGLE_LENGTH) {
            Optional<String> match = sequences.stream().filter(s -> s.split(SEPERATOR).length == 1).findFirst();
            if (match.isPresent()) {
                result.add(match.get());
                sequences.remove(match.get());
            } else {
                break;
            }
        }

        // get a list of sequences with maxSize number of requests (randomized)
        Collections.shuffle(sequences);

        for (int i = 0; getNumberOfRequests(result) < maxSize; i++) {
            result.add(sequences.get(i));
        }

        int diff = getNumberOfRequests(result) - maxSize;
        Optional<String> remove = result.stream().filter(s -> s.split(SEPERATOR).length == diff).findFirst();
        if (remove.isPresent()) {
            result.remove(remove.get());
        } else {
            result.remove(result.size() - 1);
        }

        // shuffle result
        Collections.shuffle(result);

        return result;
    }
}