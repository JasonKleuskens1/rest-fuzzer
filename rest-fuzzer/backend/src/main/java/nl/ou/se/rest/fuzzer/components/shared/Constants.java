package nl.ou.se.rest.fuzzer.components.shared;

public abstract class Constants {

    // Various
    public static final String VALUE_SEPERATOR = ",";

	// Relations
    public static final String ENTITY_GRAPH_RMD_SUTS_ALL_RELATIONS = "rmd.suts.all_relations";
	public static final String ENTITY_GRAPH_FUZ_PROJECTS_RELATIONS = "fuz.projects.sut_and_actions";
    
	// Extractor
	public abstract class Extractor {
	    public static final String UNKONW_ENUM_TYPE = "Unkwon enum type: %s";
	}

	// Fuzzer
	public abstract class Fuzzer {
	    public static final String META_DATA_MISSING = "Fuzzer %s misses metadata to run: %s";
		public static final String ENCODING_UNSUPPORTED = "Encoding is unsupported: %s";
        public static final String INVALID_HTTP_METHOD = "Http method %s is unknown";
        public static final String META_DATA_INVALID = "Meta data value %s is invalid for %s";
        public static final String PARAMETER_TYPE_UNKNOWN = "Parameter type %s is unknown";
        public static final String PARAMETER_UNKNOWN = "Parameter %s (%s) is unknown";
        public static final String EXECUTION_TIME = "Request processed in %s ms";
        public static final String NUMBER_OF_ACTIONS = "Number of actions to process: %s";
	}

	// Service
	public abstract class Service {
		public static final String VALIDATION_OBJECT_NOT_FOUND = "Object %s with id %s does not exist";
		public static final String VALIDATION_OBJECT_FAILED = "Object %s not saved, because of %s validation error(s)";
		public static final String VALIDATION_SUT_USED_BY_PROJECTS = "Sut with id %s is connected to one or more project(s)";
	}

	// Task
	public abstract class Task {
		private static final String TASK_PREFIX = "[Task %s]: ";

		// Schedular
		public static final String SCHEDULAR_START = "Starting %s task(s)";
		public static final String SCHEDULAR_START_TASK = "Task %s with id %s started";
		public static final String SCHEDULAR_TASK_NOT_STARTED = "Task %s with id %s NOT started";

		public static final String EXECUTION_FACTORY_UNKNOWN = "Unkown execution type %s in task table in database";

		// Tasks
		public static final String VALUE_FOR_KEY_NOT_PRESENT = TASK_PREFIX + "No value for key %s";
		public static final String OBJECT_NOT_FOUND = TASK_PREFIX + " Object %s with id %s not found";
		public static final String START = TASK_PREFIX + "Start";
		public static final String STOP = TASK_PREFIX + "Stop";

	}

	// Reporter
	public abstract class Reporter {
        public static final String NO_RESPONSESES = "Report: %s - No responses found";
		public static final String IO_EXCEPTION = "Report: %s - IO Exception reading XDEBUG information - %s";
        public static final String ADD_INTERVAL = "Adding inteval: %s";
        public static final String PROCESSING_FILE = "Processing file: %s (%s)";
	}
}