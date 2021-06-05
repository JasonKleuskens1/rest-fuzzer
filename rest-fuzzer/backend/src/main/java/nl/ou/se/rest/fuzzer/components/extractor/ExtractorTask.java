package nl.ou.se.rest.fuzzer.components.extractor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import nl.ou.se.rest.fuzzer.components.data.rmd.dao.RmdActionDependencyService;
import nl.ou.se.rest.fuzzer.components.data.rmd.dao.RmdActionService;
import nl.ou.se.rest.fuzzer.components.data.rmd.dao.RmdSutService;
import nl.ou.se.rest.fuzzer.components.data.rmd.domain.RmdAction;
import nl.ou.se.rest.fuzzer.components.data.rmd.domain.RmdActionDependency;
import nl.ou.se.rest.fuzzer.components.data.rmd.domain.RmdSut;
import nl.ou.se.rest.fuzzer.components.data.task.dao.TaskService;
import nl.ou.se.rest.fuzzer.components.task.TaskExecution;
import nl.ou.se.rest.fuzzer.components.task.TaskExecutionBase;

@Service
public class ExtractorTask extends TaskExecutionBase implements TaskExecution {

    // variable(s)
    public static final String KEY_SUT_ID = "sut_id";

    @Autowired
    private RmdSutService sutService;
    
    @Autowired
    private RmdActionService actionService;
   
    @Autowired
    private RmdActionDependencyService actionDependencyService;

    @Autowired
    private TaskService taskService;

    // method(s)
    public void execute() {
    	this.logStart(ExtractorTask.class.getTypeName());

    	Object objSutId = this.getValueForKey(ExtractorTask.class, KEY_SUT_ID);
    	if (objSutId == null) {
    		return;
    	}

		Long sutId = Long.valueOf((Integer) objSutId);
    	Optional<RmdSut> oSut = sutService.findById(sutId);
    	
    	if (!this.isOptionalPresent(ExtractorTask.class, oSut, sutId)) {
    	    return;
    	}

    	RmdSut sut = oSut.get();

    	extract(sut);
    	
    	getTask().setProgress(new BigDecimal(50));
    	taskService.save(getTask());

        relate(sut);

        getTask().setProgress(new BigDecimal(100));
        taskService.save(getTask());
        
    	this.logStop(ExtractorTask.class.getTypeName());
	}

    private void extract(RmdSut sut) {
        Extractor extractor = new Extractor(sut);
        extractor.processV2();

        sut.setTitle(extractor.getTitle());
        sut.setDescription(extractor.getDescription());
        sut.setHost(extractor.getHost());
        sut.setBasePath(extractor.getBasePath());
        sutService.save(sut);

        List<RmdAction> actions = extractor.getActions();
        actions.forEach(a -> a.setSut(sut));
        actionService.saveAll(actions);
    }

    private void relate(RmdSut sut) {
        sut = sutService.findById(sut.getId()).get(); // refresh needed

        DependencyFinder dependencyFinder = new DependencyFinder(sut);
        dependencyFinder.process();
        Set<RmdActionDependency> actionDependencies = dependencyFinder.getActionDepencies();

        actionDependencyService.saveAll(actionDependencies);
    }    
}