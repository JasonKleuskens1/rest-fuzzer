package nl.ou.se.rest.fuzzer.components.fuzzer.type;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import nl.ou.se.rest.fuzzer.components.data.fuz.dao.FuzRequestService;
import nl.ou.se.rest.fuzzer.components.data.fuz.dao.FuzResponseService;
import nl.ou.se.rest.fuzzer.components.data.fuz.dao.FuzSequenceService;
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
import nl.ou.se.rest.fuzzer.components.data.task.domain.Task;
import nl.ou.se.rest.fuzzer.components.fuzzer.executor.ExecutorUtil;
import nl.ou.se.rest.fuzzer.components.fuzzer.metadata.MetaDataUtil;
import nl.ou.se.rest.fuzzer.components.fuzzer.metadata.MetaDataUtil.Meta;
import nl.ou.se.rest.fuzzer.components.fuzzer.util.RequestUtil;
import nl.ou.se.rest.fuzzer.components.fuzzer.util.SequenceUtil;

@Service
public class FuzzerModelBased extends FuzzerBase implements Fuzzer {

    // variable(s)
    private FuzProject project = null;
    private MetaDataUtil metaDataUtil = null;

    @Autowired
    private RmdActionService actionService;

    @Autowired
    private RmdActionDependencyService actionDependencyService;

    @Autowired
    private FuzRequestService requestService;

    @Autowired
    private FuzResponseService responseService;

    @Autowired
    private FuzSequenceService sequenceService;

    @Autowired
    private RequestUtil requestUtil;

    @Autowired
    private ExecutorUtil executorUtil;

    private FuzSequenceFactory sequenceFactory = new FuzSequenceFactory();

    public void start(FuzProject project, Task task) {
        this.project = project;

        // get meta
        Integer maxSequenceLength = metaDataUtil.getIntegerValue(MetaDataUtil.Meta.MAX_SEQUENCE_LENGTH);
        Integer maxNumRequests = metaDataUtil.getIntegerValue(MetaDataUtil.Meta.MAX_NUMBER_REQUESTS);

        // authentication
        executorUtil.setAuthentication(metaDataUtil.getAuthentication());

        // get sequences
        List<RmdAction> allActions = actionService.findBySutId(this.project.getSut().getId());
        allActions = metaDataUtil.getCorrectedActions(allActions);

        List<RmdAction> actions = actionService.findBySutId(this.project.getSut().getId());
        actions = metaDataUtil.getFilteredActions(actions);

        List<RmdActionDependency> dependencies = actionDependencyService.findBySutId(this.project.getSut().getId());

        SequenceUtil sequenceUtil = new SequenceUtil(allActions, actions, dependencies);
        List<String> sequences = sequenceUtil.getValidSequences(maxSequenceLength);

        int total = sequenceUtil.getNumberOfRequests(sequences);

        // cap at maxNumRequests
        if (total > maxNumRequests) {
            sequences = sequenceUtil.getRandomSequences(sequences, maxNumRequests);
            total = maxNumRequests;
        }

        // init requestUtil
        requestUtil.init(project, metaDataUtil.getDefaults());

        // for all sequences
        int sequencePosition = 1;
        for (String sequenceString : sequences) {
            FuzSequenceStatus status = FuzSequenceStatus.COMPLETED;
            List<RmdAction> actionsFromSequence = sequenceUtil.getActionsFromSequence(sequenceString);

            FuzSequence sequence = sequenceFactory.create(sequencePosition, project).build();
            sequenceService.save(sequence);

            // for each item in sequence
            for (RmdAction a : actionsFromSequence) {
                FuzRequest request = requestUtil.getRequestFromAction(a, sequence);
                requestService.save(request);
                sequence.addRequest(request);

                sequence = sequenceService.save(sequence);

                // execute requests
                FuzResponse response = executorUtil.processRequest(request);
                responseService.save(response);

                // abort sequence
                if (response.getStatusCode() < 200 || response.getStatusCode() >= 300) {
                    status = FuzSequenceStatus.ABORTED;
                    break;
                }
            }

            // update sequence
            sequence.setStatus(status);
            sequenceService.save(sequence);

            sequencePosition++;
            saveTaskProgress(task, sequencePosition, sequences.size());
        }
    }

    public Boolean isMetaDataValid(Map<String, Object> metaDataTuples) {
        this.metaDataUtil = new MetaDataUtil(metaDataTuples);
        return metaDataUtil.isValid(Meta.CONFIGURATION, Meta.MAX_SEQUENCE_LENGTH, Meta.MAX_NUMBER_REQUESTS);
    }
}