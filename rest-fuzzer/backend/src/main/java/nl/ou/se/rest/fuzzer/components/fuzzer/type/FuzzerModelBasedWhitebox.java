package nl.ou.se.rest.fuzzer.components.fuzzer.type;

import java.sql.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.apache.commons.lang3.RandomStringUtils;
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
import nl.ou.se.rest.fuzzer.components.data.rmd.domain.HttpMethod;
import nl.ou.se.rest.fuzzer.components.data.rmd.domain.ParameterContext;
import nl.ou.se.rest.fuzzer.components.data.rmd.domain.ParameterType;
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
public class FuzzerModelBasedWhitebox extends FuzzerBase implements Fuzzer {

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
		Integer duration = metaDataUtil.getIntegerValue(MetaDataUtil.Meta.DURATION);

		long millis = System.currentTimeMillis();
		Date startDate = new java.sql.Date(millis);

		long timeInSecs = System.currentTimeMillis();
		long fuzzerDuration = (duration * 60 * 1000);
		Date endDate = new Date(timeInSecs + fuzzerDuration);

		int fuzzerDurationInSecs = (duration * 60);

		// authentication
		executorUtil.setAuthentication(metaDataUtil.getAuthentication());

		// get sequences
		List<RmdAction> allActions = actionService.findBySutId(this.project.getSut().getId());
		allActions = metaDataUtil.getFilteredActions(allActions);

		List<RmdAction> actions = actionService.findBySutId(this.project.getSut().getId());
		actions = metaDataUtil.getCorrectedActions(actions);

		// get dependencies for SUT.
		List<RmdActionDependency> dependencies = actionDependencyService.findBySutId(this.project.getSut().getId());

		// init requestUtil
		requestUtil.init(project, metaDataUtil.getDefaults());

		SequenceUtil sequenceUtil = new SequenceUtil(allActions, actions, dependencies);

		while (isWithinRange(startDate, endDate)) {

			// get sequences based on SUT.
			List<String> sequences = sequenceUtil.getValidSequences(maxSequenceLength);

			int sequencePosition = 1;

			// if within range we can start looping through the sequences
			for (String sequenceString : sequences) {
				if (isWithinRange(startDate, endDate)) {
					FuzSequenceStatus status = FuzSequenceStatus.COMPLETED;
					List<RmdAction> actionsFromSequence = sequenceUtil.getActionsFromSequence(sequenceString);

					FuzSequence sequence = sequenceFactory.create(sequencePosition, project).build();
					sequenceService.save(sequence);
					if (isWithinRange(startDate, endDate)) {
						// for each item in sequence
						for (RmdAction a : actionsFromSequence) {
							if (isWithinRange(startDate, endDate) && a != null) {

								FuzRequest request = requestUtil.getRequestFromAction(a, sequence);

								List<RmdParameter> parameters = RandomUtil.getFromValues(a.getParameters(), null);

								if (request.getHttpMethod() == HttpMethod.GET) {
									for (RmdParameter param : parameters) {
										if (param.getRequired() == false) 
										{
											request.removeParameter(param);
										}
									}
								}

								requestService.save(request);

								// update and save sequence
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

								// update progress
								int runtimeInSecs = (int) ((System.currentTimeMillis() - millis) / 1000);
								if (runtimeInSecs > fuzzerDurationInSecs)
									runtimeInSecs = fuzzerDurationInSecs;
								saveTaskProgress(task, runtimeInSecs, fuzzerDurationInSecs);
							}
						}
					} else {
						status = FuzSequenceStatus.ABORTED;
					}
					// update sequence
					sequence.setStatus(status);
					sequenceService.save(sequence);
				}

				// update progress
				int runtimeInSecs = (int) ((System.currentTimeMillis() - millis) / 1000);
				if (runtimeInSecs > fuzzerDurationInSecs)
					runtimeInSecs = fuzzerDurationInSecs;
				saveTaskProgress(task, runtimeInSecs, fuzzerDurationInSecs);
			}
		}
	}

	public Boolean isMetaDataValid(Map<String, Object> metaDataTuples) {
		this.metaDataUtil = new MetaDataUtil(metaDataTuples);
		return metaDataUtil.isValid(Meta.CONFIGURATION, Meta.MAX_SEQUENCE_LENGTH, Meta.MAX_NUMBER_REQUESTS);
	}

	private Boolean isWithinRange(Date startDate, Date endDate) {
		long millis = System.currentTimeMillis();
		java.sql.Date currentDate = new java.sql.Date(millis);
		return !(currentDate.before(startDate) || currentDate.after(endDate));
	}

	private String getRandomString(ParameterContext context, String format, String description) {
		if (context == ParameterContext.FORMDATA) {

			if (format.contains("format") || format.contains("FORMAT")) {
				if (format.contains("email")) {
					return getRandomEmail();
				}
				if (format.contains("uri")) {
					return getRandomUri();
				}
				if (format.contains("date-time")) {
					return getRandomDateTime();
				}
				if (format.contains("ip")) {
					return getRandomIPAddress();
				}
				if (format.contains("uuid")) {
					return getRandomUUID();
				}
				if (format.contains("VALUE")) {

					if (format.contains("MIN_VALUE") && format.contains("MAX_VALUE")) {
						String[] splittedString = format.split(",");
						String minValue = splittedString[0].replace("\"MIN_VALUE\":", "").replace("{", "").replace("}",
								"");
						String maxValue = splittedString[0].replace("\"MAX_VALUE\":", "").replace("{", "").replace("}",
								"");
						Random r = new Random();
						int low = Integer.parseInt(minValue);
						int high = Integer.parseInt(maxValue);
						int result = r.nextInt(high - low) + low;
						return Integer.toString(result);
					} else if (format.contains("MIN_VALUE")) {

						String[] splittedString = format.split(",");
						String minValue = splittedString[0].replace("\"MIN_VALUE\":", "").replace("{", "").replace("}",
								"");
						Random r = new Random();
						int low = Integer.parseInt(minValue);
						int result = r.nextInt() + low;
						return Integer.toString(result);
					} else if (format.contains("MAX_VALUE")) {
						String[] splittedString = format.split(",");
						String minValue = splittedString[0].replace("\"MAX_VALUE\":", "").replace("{", "").replace("}",
								"");
						Random r = new Random();
						int high = Integer.parseInt(minValue);
						int result = r.nextInt(high);
						return Integer.toString(result);
					}
				}
				if (format.contains("MIN_LENGTH")) {

					String[] splittedString = format.split(",");
					String minValue = splittedString[0].replace("\"MIN_LENGTH\":", "").replace("{", "").replace("}",
							"");
					Random r = new Random();
					int low = Integer.parseInt(minValue);
					int result = r.nextInt() + low;
					return Integer.toString(result);
				}
				if (description.contains("uuid")) {
					return getRandomUUID();
				}
				if (description.contains("date")) {
					return getRandomDateTime();
				}
			}
		} else if (context == ParameterContext.HEADER) {

		} else if (context == ParameterContext.PATH) {
			if (format.contains("enum") || format.contains("ENUM")) {
				String[] splittedString = format.split(":");
				String enumString = splittedString[splittedString.length - 1];
				enumString = enumString.replace("\"", "").replace("{", "").replace("}", "");
				Random rn = new Random();
				String[] options = enumString.split(";");
				return options[rn.nextInt(options.length - 1)];
			}
			if (description.contains("uuid")) {
				return getRandomUUID();
			}
			if (description.contains("date")) {
				return getRandomDateTime();
			}
		} else if (context == ParameterContext.QUERY) {
			if (format.contains("format") || format.contains("FORMAT")) {
				if (format.contains("uri")) {
					return getRandomUri();
				}
				if (format.contains("date-time")) {
					return getRandomDateTime();
				}
				if (format.contains("ip")) {
					return getRandomIPAddress();
				}
				if (format.contains("email")) {
					return getRandomEmail();
				}
				if (format.contains("uuid")) {
					return getRandomUUID();
				}
				if (format.contains("VALUE")) {

					if (format.contains("MIN_VALUE") && format.contains("MAX_VALUE")) {
						String[] splittedString = format.split(",");
						String minValue = splittedString[0].replace("\"MIN_VALUE\":", "").replace("{", "").replace("}",
								"");
						String maxValue = splittedString[0].replace("\"MAX_VALUE\":", "").replace("{", "").replace("}",
								"");
						Random r = new Random();
						int low = Integer.parseInt(minValue);
						int high = Integer.parseInt(maxValue);
						int result = r.nextInt(high - low) + low;
						return Integer.toString(result);
					} else if (format.contains("MIN_VALUE")) {

						String[] splittedString = format.split(",");
						String minValue = splittedString[0].replace("\"MIN_VALUE\":", "").replace("{", "").replace("}",
								"");
						Random r = new Random();
						int low = Integer.parseInt(minValue);
						int result = r.nextInt() + low;
						return Integer.toString(result);
					} else if (format.contains("MAX_VALUE")) {
						String[] splittedString = format.split(",");
						String minValue = splittedString[0].replace("\"MAX_VALUE\":", "").replace("{", "").replace("}",
								"");
						Random r = new Random();
						int high = Integer.parseInt(minValue);
						int result = r.nextInt(high);
						return Integer.toString(result);
					}
				}
				if (description.contains("JSON:API")) {

				}
			}
		}

		String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789;'";
		return RandomStringUtils.random((int) (System.currentTimeMillis() % 10), characters);
	}

	private String getRandomUUID() {
		UUID uuid = UUID.randomUUID();
		return uuid.toString();
	}

	private String getRandomEmail() {
		String allowedChars = "abcdefghijklmnopqrstuvwxyz" + "1234567890" + "_-.";
		String email = RandomStringUtils.random(getRandomInteger(), allowedChars) + "@thesis.com";
		return email;
	}

	private String getRandomIPAddress() {
		Random r = new Random();
		return r.nextInt(256) + "." + r.nextInt(256) + "." + r.nextInt(256) + "." + r.nextInt(256);
	}

	private String getRandomDateTime() {
		Random r = new Random();
		return r.nextInt(2100) + "-" + (r.nextInt(12) + 1) + "-" + (r.nextInt(28) + 1) + " " + r.nextInt(24) + ":"
				+ r.nextInt(60) + ":" + r.nextInt(60);
	}

	private String getRandomUri() {
		String allowedChars = "abcdefghijklmnopqrstuvwxyz" + "1234567890" + "_-.";
		return "http://www." + RandomStringUtils.random(getRandomInteger(), allowedChars) + ".nl";
	}

	private int getRandomInteger() {
		return (int) (System.currentTimeMillis() % 127);
	}
}