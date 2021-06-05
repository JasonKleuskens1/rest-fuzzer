package nl.ou.se.rest.fuzzer.components.fuzzer.type;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import nl.ou.se.rest.fuzzer.components.data.fuz.dao.FuzDictionaryService;
import nl.ou.se.rest.fuzzer.components.data.fuz.dao.FuzRequestService;
import nl.ou.se.rest.fuzzer.components.data.fuz.dao.FuzResponseService;
import nl.ou.se.rest.fuzzer.components.data.fuz.domain.FuzDictionary;
import nl.ou.se.rest.fuzzer.components.data.fuz.domain.FuzProject;
import nl.ou.se.rest.fuzzer.components.data.fuz.domain.FuzRequest;
import nl.ou.se.rest.fuzzer.components.data.fuz.domain.FuzResponse;
import nl.ou.se.rest.fuzzer.components.data.rmd.dao.RmdActionService;
import nl.ou.se.rest.fuzzer.components.data.rmd.domain.RmdAction;
import nl.ou.se.rest.fuzzer.components.data.rmd.domain.RmdParameter;
import nl.ou.se.rest.fuzzer.components.data.task.domain.Task;
import nl.ou.se.rest.fuzzer.components.fuzzer.executor.ExecutorUtil;
import nl.ou.se.rest.fuzzer.components.fuzzer.metadata.MetaDataUtil;
import nl.ou.se.rest.fuzzer.components.fuzzer.metadata.MetaDataUtil.Meta;
import nl.ou.se.rest.fuzzer.components.fuzzer.util.RandomUtil;
import nl.ou.se.rest.fuzzer.components.fuzzer.util.RequestUtil;

@Service
public class FuzzerDictionary extends FuzzerBase implements Fuzzer {

    // variable(s)
    private FuzProject project = null;
    private MetaDataUtil metaDataUtil = null;
    private List<String> dictionaryValues = new ArrayList<String>();

    List<RmdAction> actions;
    Integer maxNumRequests;
    Integer repetitions;
    Integer maxDictionaryParams;
    Integer maxDictionaryItems;
    
    @Autowired
    private RmdActionService actionService;

    @Autowired
    private FuzRequestService requestService;

    @Autowired
    private FuzResponseService responseService;

    @Autowired
    private FuzDictionaryService dictionaryService;

    @Autowired
    private RequestUtil requestUtil;

    @Autowired
    private ExecutorUtil executorUtil;

    // method(s)
    public void start(FuzProject project, Task task) {
        this.project = project;

        this.init(); 

        Integer count = 0;
        Integer total = this.repetitions * this.actions.size() * maxDictionaryParams * maxDictionaryItems;

        // cap at maxNumRequests
        if (total > maxNumRequests) {
            total = maxNumRequests;
        }

        for (Integer i = 0; i < this.repetitions; i++) {

            for (RmdAction a : this.actions) {

                List<RmdParameter> parameters = RandomUtil.getFromValues(a.getParameters(), maxDictionaryParams);
                for (RmdParameter parameter : parameters) {

                    List<String> dictionaryValues = RandomUtil.getFromValues(this.dictionaryValues, maxDictionaryItems);
                    for (String dictionaryValue : dictionaryValues) {

                        FuzRequest request = requestUtil.getRequestFromAction(a, null);
                        request.replaceParameterValue(parameter, dictionaryValue);
                        requestService.save(request);

                        FuzResponse response = executorUtil.processRequest(request);
                        responseService.save(response);

                        count++;
                        saveTaskProgress(task, count, total);

                        if (count >= total) {
                            return;
                        }                        
                    }
                }
            }
        }
    }

    public void init() {
        // authentication
        executorUtil.setAuthentication(metaDataUtil.getAuthentication());

        // get meta
        this.maxNumRequests = metaDataUtil.getIntegerValue(Meta.MAX_NUMBER_REQUESTS);
        this.repetitions = metaDataUtil.getIntegerValue(Meta.REPETITIONS);
        this.maxDictionaryParams = metaDataUtil.getIntegerValue(Meta.MAX_DICTIONARY_PARAMS);
        this.maxDictionaryItems = metaDataUtil.getIntegerValue(Meta.MAX_DICTIONARY_ITEMS);

        List<Long> dictionaryIds = metaDataUtil.getLongArrayValues(Meta.DICTIONARIES);
        List<FuzDictionary> dictionaries = dictionaryService.findByIds(dictionaryIds);
        dictionaries.forEach(dictionary -> this.dictionaryValues.addAll(dictionary.getItems()));

        // actions
        this.actions = actionService.findBySutId(this.project.getSut().getId());
        this.actions = metaDataUtil.getFilteredActions(this.actions);

        // initialize requestUtil
        requestUtil.init(project, metaDataUtil.getDefaults());        
    }

    public Boolean isMetaDataValid(Map<String, Object> metaDataTuples) {
        this.metaDataUtil = new MetaDataUtil(metaDataTuples);
        return metaDataUtil.isValid(Meta.CONFIGURATION, Meta.MAX_NUMBER_REQUESTS, Meta.REPETITIONS, Meta.DICTIONARIES, Meta.MAX_DICTIONARY_PARAMS,
                Meta.MAX_DICTIONARY_ITEMS);
    }
}