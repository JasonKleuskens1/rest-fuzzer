package nl.ou.se.rest.fuzzer.components.fuzzer.type;

import java.sql.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import nl.ou.se.rest.fuzzer.components.data.fuz.dao.FuzRequestService;
import nl.ou.se.rest.fuzzer.components.data.fuz.dao.FuzResponseService;
import nl.ou.se.rest.fuzzer.components.data.fuz.domain.FuzProject;
import nl.ou.se.rest.fuzzer.components.data.fuz.domain.FuzRequest;
import nl.ou.se.rest.fuzzer.components.data.fuz.domain.FuzResponse;
import nl.ou.se.rest.fuzzer.components.data.rmd.dao.RmdActionService;
import nl.ou.se.rest.fuzzer.components.data.rmd.domain.RmdAction;
import nl.ou.se.rest.fuzzer.components.data.task.domain.Task;
import nl.ou.se.rest.fuzzer.components.fuzzer.executor.ExecutorUtil;
import nl.ou.se.rest.fuzzer.components.fuzzer.metadata.MetaDataUtil;
import nl.ou.se.rest.fuzzer.components.fuzzer.metadata.MetaDataUtil.Meta;
import nl.ou.se.rest.fuzzer.components.fuzzer.util.RequestUtil;

@Service
public class FuzzerModelBasedBlackbox extends FuzzerBase implements Fuzzer {

    // variable(s)
    private FuzProject project = null;
    private MetaDataUtil metaDataUtil = null;

    @Autowired
    private RmdActionService actionService;

    @Autowired
    private FuzRequestService requestService;

    @Autowired
    private FuzResponseService responseService;

    @Autowired
    private RequestUtil requestUtil;

    @Autowired
    private ExecutorUtil executorUtil;

    public void start(FuzProject project, Task task) {
        this.project = project;

        // get meta
        Integer duration = metaDataUtil.getIntegerValue(MetaDataUtil.Meta.DURATION);

    	long millis=System.currentTimeMillis();  
    	Date startDate=new java.sql.Date(millis);      	

		long timeInSecs = System.currentTimeMillis();  
		long fuzzerDuration = (duration * 60 * 1000);
		Date endDate = new Date(timeInSecs + fuzzerDuration);
		
		int fuzzerDurationInSecs = (duration * 60);
        
        // authentication
        executorUtil.setAuthentication(metaDataUtil.getAuthentication());

        // get actions
        List<RmdAction> actions = actionService.findBySutId(this.project.getSut().getId());
        actions = metaDataUtil.getFilteredActions(actions); 

        // init requestUtil
        requestUtil.init(project, metaDataUtil.getDefaults());

        while (isWithinRange(startDate, endDate)) {
        	// Random is too predictable when using it without tracking the previous runs. This will be more dynamic
            RmdAction a = actions.get((int)(System.currentTimeMillis() % actions.size()));        	
            
            	// get random action
                FuzRequest request = requestUtil.getRequestFromAction(a, null);
                requestService.save(request);

                // execute requests
                FuzResponse response = executorUtil.processRequest(request);
                responseService.save(response);

                // update progress
                int runtimeInSecs = (int) ((System.currentTimeMillis() - millis) / 1000);
                if (runtimeInSecs > fuzzerDurationInSecs)
                	runtimeInSecs = fuzzerDurationInSecs;
                saveTaskProgress(task, runtimeInSecs, fuzzerDurationInSecs);
        }
    }

    public Boolean isMetaDataValid(Map<String, Object> metaDataTuples) {
        this.metaDataUtil = new MetaDataUtil(metaDataTuples);
        return metaDataUtil.isValid(Meta.CONFIGURATION, Meta.MAX_SEQUENCE_LENGTH, Meta.MAX_NUMBER_REQUESTS);
    }
    
    private Boolean isWithinRange(Date startDate, Date endDate) {
	    	long millis=System.currentTimeMillis();  
	    	java.sql.Date currentDate=new java.sql.Date(millis);  
    	   return !(currentDate.before(startDate) || currentDate.after(endDate));
    	}
}