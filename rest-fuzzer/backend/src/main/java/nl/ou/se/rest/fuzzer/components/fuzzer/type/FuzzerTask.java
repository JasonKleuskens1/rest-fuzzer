package nl.ou.se.rest.fuzzer.components.fuzzer.type;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import nl.ou.se.rest.fuzzer.components.data.fuz.dao.FuzProjectService;
import nl.ou.se.rest.fuzzer.components.data.fuz.domain.FuzProject;
import nl.ou.se.rest.fuzzer.components.task.TaskExecution;
import nl.ou.se.rest.fuzzer.components.task.TaskExecutionBase;

@Service
public class FuzzerTask extends TaskExecutionBase implements TaskExecution {

    // variable(s)
    public static final String KEY_PROJECT_ID = "project_id";

    @Autowired
    private FuzProjectService projectService;

    private Fuzzer fuzzer;

    @Autowired
    private FuzzerBasic fuzzerBasic;

    @Autowired
    private FuzzerDictionary fuzzerDictionary;

    @Autowired
    private FuzzerModelBased fuzzerModelBased;

    @Autowired
    private FuzzerModelBasedDictionary fuzzerModelBasedDictionary;

    @Autowired
    private FuzzerModelBasedBlackbox fuzzerModelBasedBlackbox;

    @Autowired
    private FuzzerModelBasedWhitebox fuzzerModelBasedWhitebox;

    @Override
    public void execute() {
        this.logStart(FuzzerTask.class.getTypeName());

        Object objProjectId = this.getValueForKey(FuzzerTask.class, KEY_PROJECT_ID);
        if (objProjectId == null) {
            return;
        }

        Long projectId = Long.valueOf((Integer) objProjectId);
        Optional<FuzProject> oProject = projectService.findById(projectId);

        if (!this.isOptionalPresent(FuzzerTask.class, oProject, projectId)) {
            return;
        }

        FuzProject project = oProject.get();

        switch (project.getType()) {
        case BASIC_FUZZER:
            fuzzer = fuzzerBasic;
            break;
        case DICTIONARY_FUZZER:
            fuzzer = fuzzerDictionary;
            break;
        case MB_FUZZER:
            fuzzer = fuzzerModelBased;
            break;
        case MB_BLACKBOX_FUZZER:
            fuzzer = fuzzerModelBasedBlackbox;
            break;
        case MB_WHITEBOX_FUZZER:
            fuzzer = fuzzerModelBasedWhitebox;
            break;
        case MB_DICTIONARY_FUZZER:
            fuzzer = fuzzerModelBasedDictionary;
            break;
        default:
            break;
        }

        if (fuzzer.isMetaDataValid(project.getMetaDataTuples())) {
            fuzzer.start(project, this.getTask());
        }

        this.logStop(FuzzerTask.class.getTypeName());
    }
}