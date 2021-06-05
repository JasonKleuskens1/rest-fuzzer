package nl.ou.se.rest.fuzzer.components.fuzzer.type;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import nl.ou.se.rest.fuzzer.components.data.fuz.dao.FuzDictionaryService;
import nl.ou.se.rest.fuzzer.components.data.fuz.dao.FuzRequestService;
import nl.ou.se.rest.fuzzer.components.data.fuz.dao.FuzResponseService;
import nl.ou.se.rest.fuzzer.components.data.fuz.dao.FuzSequenceService;
import nl.ou.se.rest.fuzzer.components.data.fuz.domain.FuzDictionary;
import nl.ou.se.rest.fuzzer.components.data.fuz.domain.FuzProject;
import nl.ou.se.rest.fuzzer.components.data.fuz.domain.FuzRequest;
import nl.ou.se.rest.fuzzer.components.data.fuz.domain.FuzResponse;
import nl.ou.se.rest.fuzzer.components.data.fuz.domain.FuzSequence;
import nl.ou.se.rest.fuzzer.components.data.fuz.domain.FuzSequenceStatus;
import nl.ou.se.rest.fuzzer.components.data.fuz.factory.FuzSequenceFactory;
import nl.ou.se.rest.fuzzer.components.data.rmd.dao.RmdActionDependencyService;
import nl.ou.se.rest.fuzzer.components.data.rmd.dao.RmdActionService;
import nl.ou.se.rest.fuzzer.components.data.rmd.domain.RmdAction;
import nl.ou.se.rest.fuzzer.components.data.rmd.domain.RmdActionDependency;
import nl.ou.se.rest.fuzzer.components.data.rmd.domain.RmdParameter;
import nl.ou.se.rest.fuzzer.components.data.task.domain.Task;
import nl.ou.se.rest.fuzzer.components.fuzzer.executor.ExecutorUtil;
import nl.ou.se.rest.fuzzer.components.fuzzer.metadata.MetaDataUtil;
import nl.ou.se.rest.fuzzer.components.fuzzer.metadata.MetaDataUtil.Meta;
import nl.ou.se.rest.fuzzer.components.fuzzer.util.RandomUtil;
import nl.ou.se.rest.fuzzer.components.fuzzer.util.RequestUtil;
import nl.ou.se.rest.fuzzer.components.fuzzer.util.SequenceUtil;

@Service
public class FuzzerModelBasedDictionary extends FuzzerBase implements Fuzzer {

    // variable(s)
    private FuzProject project = null;
    private MetaDataUtil metaDataUtil = null;
    private SequenceUtil sequenceUtil = null;
    List<RmdAction> actions;
    private FuzSequenceFactory sequenceFactory = new FuzSequenceFactory();

    // meta
    private List<String> dictionaryValues = new ArrayList<String>();
    Integer maxRequests;
    Integer maxDictionaryParams;
    Integer maxDictionaryItems;

    @Autowired
    private RmdActionService actionService;

    @Autowired
    private FuzRequestService requestService;

    @Autowired
    private FuzResponseService responseService;

    @Autowired
    private FuzSequenceService sequenceService;

    @Autowired
    private FuzDictionaryService dictionaryService;

    @Autowired
    private RmdActionDependencyService actionDependencyService;

    @Autowired
    private RequestUtil requestUtil;

    @Autowired
    private ExecutorUtil executorUtil;

    // method(s)
    public void start(FuzProject project, Task task) {
        this.project = project;

        this.init();

        Integer count = 0;
        Integer requestCount = 0;
        Integer total = this.actions.size();
        Integer sequencePosition = 1;

        // for all actions
        for (RmdAction action : this.actions) {

            // for all selected parameters
            List<RmdParameter> parameters = RandomUtil.getFromValues(action.getParameters(), maxDictionaryParams);
            for (RmdParameter parameter : parameters) {

                // for all selected dictionary values
                List<String> dictionaryValues = RandomUtil.getFromValues(this.dictionaryValues, maxDictionaryItems);
                for (String dictionaryValue : dictionaryValues) {

                    // create sequence
                    FuzSequence sequence = sequenceFactory.create(sequencePosition, project).build();
                    sequenceService.save(sequence);

                    // get all dependent actions + current action
                    List<RmdAction> actionsInSequence = sequenceUtil.getDependentActions(action);

                    executeSequence(sequence, actionsInSequence, parameter, dictionaryValue);

                    requestCount += sequence.getRequests().size();

                    // update sequence
                    sequenceService.save(sequence);
                    sequencePosition++;
                    
                    if (requestCount >= maxRequests) {
                        saveTaskProgress(task, total, total);
                        return;
                    }
                }
            }

            count++;
            saveTaskProgress(task, count, total);
        }

        saveTaskProgress(task, total, total);
    }

    public FuzSequence executeSequence(FuzSequence sequence, List<RmdAction> actions, RmdParameter parameter,
            String dictionaryValue) {

        FuzSequenceStatus status = FuzSequenceStatus.COMPLETED;
        
        for (RmdAction action : actions) {
            FuzRequest request = requestUtil.getRequestFromAction(action, sequence); 

            // apply dictionary fuzzing on last request in sequence
            if (actions.size() == 1 || actions.get(actions.size() - 1).equals(action)) {
                request.replaceParameterValue(parameter, dictionaryValue);
            }

            requestService.save(request);

            // update and save sequence
            sequence.addRequest(request);
            sequenceService.save(sequence);

            // execute requests
            FuzResponse response = executorUtil.processRequest(request);
            responseService.save(response);

            // abort sequence
            if (response.getStatusCode() < 200 || response.getStatusCode() >= 300) {
                status = FuzSequenceStatus.ABORTED;
                break;
            }
        }

        sequence.setStatus(status);

        return sequence;
    }

    public void init() {
        // authentication
        executorUtil.setAuthentication(metaDataUtil.getAuthentication());

        // get meta
        this.maxRequests = metaDataUtil.getIntegerValue(Meta.MAX_NUMBER_REQUESTS);
        this.maxDictionaryParams = metaDataUtil.getIntegerValue(Meta.MAX_DICTIONARY_PARAMS);
        this.maxDictionaryItems = metaDataUtil.getIntegerValue(Meta.MAX_DICTIONARY_ITEMS);

        // actions
        List<RmdAction> allActions = actionService.findBySutId(this.project.getSut().getId());
        allActions = metaDataUtil.getCorrectedActions(allActions);

        this.actions = actionService.findBySutId(this.project.getSut().getId());
        this.actions = metaDataUtil.getFilteredActions(this.actions);

        // initialize requestUtil
        requestUtil.init(project, metaDataUtil.getDefaults());

        // initialize dictionary values
        List<Long> dictionaryIds = metaDataUtil.getLongArrayValues(Meta.DICTIONARIES);
        List<FuzDictionary> dictionaries = dictionaryService.findByIds(dictionaryIds);
        dictionaries.forEach(dictionary -> this.dictionaryValues.addAll(dictionary.getItems()));

        // sequences information
        List<RmdActionDependency> dependencies = actionDependencyService.findBySutId(this.project.getSut().getId());

        this.sequenceUtil = new SequenceUtil(allActions, actions, dependencies);
    }

    public Boolean isMetaDataValid(Map<String, Object> metaDataTuples) {
        this.metaDataUtil = new MetaDataUtil(metaDataTuples);
        return metaDataUtil.isValid(Meta.CONFIGURATION, Meta.DICTIONARIES, Meta.MAX_NUMBER_REQUESTS,
                Meta.MAX_DICTIONARY_PARAMS, Meta.MAX_DICTIONARY_ITEMS);
    }
}