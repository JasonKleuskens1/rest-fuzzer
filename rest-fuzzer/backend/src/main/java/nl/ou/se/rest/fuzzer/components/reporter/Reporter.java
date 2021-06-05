package nl.ou.se.rest.fuzzer.components.reporter;

import java.util.Map;

import nl.ou.se.rest.fuzzer.components.data.report.domain.Report;
import nl.ou.se.rest.fuzzer.components.data.task.domain.Task;

public interface Reporter {

    public void init(Report report, Task task);

    public void generate();

    public Boolean isMetaDataValid(Map<String, Object> metaDataTuples);

}