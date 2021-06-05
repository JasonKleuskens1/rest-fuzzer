package nl.ou.se.rest.fuzzer.components.extractor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.swagger.models.HttpMethod;
import io.swagger.models.Operation;
import io.swagger.models.Path;
import io.swagger.models.Response;
import io.swagger.models.Swagger;
import io.swagger.models.parameters.Parameter;
import io.swagger.parser.SwaggerParser;
import nl.ou.se.rest.fuzzer.components.data.rmd.domain.RmdAction;
import nl.ou.se.rest.fuzzer.components.data.rmd.domain.RmdSut;
import nl.ou.se.rest.fuzzer.components.data.rmd.factory.RmdActionFactory;
import nl.ou.se.rest.fuzzer.components.data.rmd.factory.RmdParameterFactory;
import nl.ou.se.rest.fuzzer.components.data.rmd.factory.RmdResponseFactory;

public class Extractor {

	// variable(s)
	private RmdSut sut;

	private String title;
	private String description;
	private String host;
	private String basePath;
	private List<RmdAction> actions = new ArrayList<>();
	private Logger logger = LoggerFactory.getLogger(this.getClass().getName());

	private RmdActionFactory actionFactory = new RmdActionFactory();
	private RmdParameterFactory parameterFactory = new RmdParameterFactory();
	private RmdResponseFactory responseFactory = new RmdResponseFactory();

	// constructor(s)
	public Extractor(RmdSut sut) {
		this.sut = sut;
	}

	// method(s)
	public void processV2() {
		Swagger s = new SwaggerParser().read(this.sut.getLocation());

		this.title = s.getInfo().getTitle();
		this.description = s.getInfo().getDescription().substring(0, Math.min(s.getInfo().getDescription().length(), 127));
		this.host = s.getHost();
		this.basePath = s.getBasePath();

		s.getPaths().entrySet().forEach(e -> processPath(e));
	}

	private void processPath(Entry<String, Path> path) {
		path.getValue().getOperationMap().entrySet().forEach(o -> processOperation(path.getKey(), o));
	}

	private void processOperation(String key, Entry<HttpMethod, Operation> operation) {
		actionFactory.create(key, operation.getKey().toString());

		// add parameters
		try {
			operation.getValue().getParameters().forEach(p -> processParameter(p));
			

			// add responses
			operation.getValue().getResponses().entrySet().forEach(r -> {
				try {
					processResponse(r);
				} catch (Exception e) {
					logger.error(e.getMessage());
				}
			});

			actions.add(actionFactory.build());
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		;

	}

	private void processParameter(Parameter parameter) {
		try {
			Map<String, Object> values = ExtractorUtil.getTypeAndMetas(parameter);

			String type = values.remove(ExtractorUtil.KEY_TYPE).toString();

			parameterFactory.create(parameter.getName(), parameter.getRequired(), parameter.getDescription().substring(0, Math.min(parameter.getDescription().length(), 127)), type,
					parameter.getIn());

			parameterFactory.setMetaDataTuples(values);

			actionFactory.addParameter(parameterFactory.build());
		} catch (IllegalAccessError e) {
			logger.error(e.getMessage());
		}
		;

	}

	public void processResponse(Entry<String, Response> responseEntry) {
		actionFactory.addResponse(responseFactory
				.create(Integer.valueOf(responseEntry.getKey()), responseEntry.getValue().getDescription().substring(0, Math.min(responseEntry.getValue().getDescription().length(), 127))).build());
	}

	// getter(s) and setter(s)
	public List<RmdAction> getActions() {
		return this.actions;
	}

	public String getHost() {
		return host;
	}

	public String getBasePath() {
		return basePath;
	}

	public String getTitle() {
		return title;
	}

	public String getDescription() {
		return description;
	}
}