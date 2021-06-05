package nl.ou.se.rest.fuzzer.components.extractor;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.swagger.models.parameters.FormParameter;
import io.swagger.models.parameters.Parameter;
import io.swagger.models.parameters.PathParameter;
import io.swagger.models.parameters.QueryParameter;
import io.swagger.models.properties.BaseIntegerProperty;
import io.swagger.models.properties.Property;
import io.swagger.models.properties.StringProperty;
import nl.ou.se.rest.fuzzer.components.data.rmd.domain.ParameterType;
import nl.ou.se.rest.fuzzer.components.data.rmd.domain.RmdParameter;
import nl.ou.se.rest.fuzzer.components.shared.Constants;

public abstract class ExtractorUtil {

    // variable(s)
    private static Logger logger = LoggerFactory.getLogger(ExtractorUtil.class);

    // constant(s)
    public static final String KEY_TYPE = "TYPE";

    private static final String TYPE_ARRAY = "array";

    private static final String ERROR_UNKOWN_PARAM_TYPE = "Unknown parametertype: %s";

    // method(s)
    public static Map<String, Object> getTypeAndMetas(Parameter parameter) {
        Map<String, Object> values = new HashMap<>();

        if (parameter instanceof QueryParameter) {
            QueryParameter qp = (QueryParameter) parameter;
            values.putAll(setValuesForQueryParameter(qp));

        } else if (parameter instanceof PathParameter) {
            PathParameter pp = (PathParameter) parameter;
            values.putAll(setValuesForPathParameter(pp));

        } else if (parameter instanceof FormParameter) {
            FormParameter fp = (FormParameter) parameter;
            values.putAll(setValuesForFormParameter(fp));

        } else {
            throw new IllegalAccessError(String.format(ERROR_UNKOWN_PARAM_TYPE, parameter.getClass().getName()));
        }

        putIfNotNull(values, RmdParameter.META_DATA_PATTERN, parameter.getPattern());

        return values;
    }

    private static Map<String, Object> setValuesForQueryParameter(QueryParameter queryParameter) {
        Map<String, Object> values = new HashMap<>();

        values.put(KEY_TYPE, getTypeForParameter(queryParameter.getType()));

        putIfNotNull(values, RmdParameter.META_DATA_FORMAT, queryParameter.getFormat());

        putIfNotNull(values, RmdParameter.META_DATA_MIN_VALUE, queryParameter.getMinimum());
        putIfNotNull(values, RmdParameter.META_DATA_MAX_VALUE, queryParameter.getMaximum());

        putIfNotNull(values, RmdParameter.META_DATA_MIN_LENGTH, queryParameter.getMinLength());
        putIfNotNull(values, RmdParameter.META_DATA_MAX_LENGTH, queryParameter.getMaxLength());

        putIfNotNull(values, RmdParameter.META_DATA_MIN_ITEMS, queryParameter.getMinItems());
        putIfNotNull(values, RmdParameter.META_DATA_MAX_ITEMS, queryParameter.getMaxItems());

        if (queryParameter.getEnum() != null) {
            putIfNotNull(values, RmdParameter.META_DATA_ENUM,
                    queryParameter.getEnum().stream().collect(Collectors.joining(", ")));
        }

        putIfNotNull(values, RmdParameter.META_DATA_DEFAULT, queryParameter.getDefaultValue());

        if (TYPE_ARRAY.equals(getTypeForParameter(queryParameter.getType()))) {
            putIfNotNull(values, RmdParameter.META_DATA_ARRAY_FORMAT, queryParameter.getCollectionFormat());
            if (queryParameter.getItems() != null) {
                putIfNotNull(values, RmdParameter.META_DATA_ARRAY_TYPE, queryParameter.getItems().getType());
            }
            putIfNotNull(values, RmdParameter.META_DATA_ARRAY_ENUM, getEnumFromCollection(queryParameter.getItems()));
        }

        return values;
    }

    private static Map<String, Object> setValuesForPathParameter(PathParameter pathParameter) {
        Map<String, Object> values = new HashMap<>();

        values.put(KEY_TYPE, getTypeForParameter(pathParameter.getType()));

        putIfNotNull(values, RmdParameter.META_DATA_FORMAT, pathParameter.getFormat());

        putIfNotNull(values, RmdParameter.META_DATA_MIN_VALUE, pathParameter.getMinimum());
        putIfNotNull(values, RmdParameter.META_DATA_MAX_VALUE, pathParameter.getMaximum());

        putIfNotNull(values, RmdParameter.META_DATA_MIN_LENGTH, pathParameter.getMinLength());
        putIfNotNull(values, RmdParameter.META_DATA_MAX_LENGTH, pathParameter.getMaxLength());

        putIfNotNull(values, RmdParameter.META_DATA_MIN_ITEMS, pathParameter.getMinItems());
        putIfNotNull(values, RmdParameter.META_DATA_MAX_ITEMS, pathParameter.getMaxItems());

        if (pathParameter.getEnum() != null) {
            putIfNotNull(values, RmdParameter.META_DATA_ENUM,
                    pathParameter.getEnum().stream().collect(Collectors.joining(", ")));
        }

        putIfNotNull(values, RmdParameter.META_DATA_DEFAULT, pathParameter.getDefaultValue());

        if (TYPE_ARRAY.equals(getTypeForParameter(pathParameter.getType()))) {
            putIfNotNull(values, RmdParameter.META_DATA_ARRAY_FORMAT, pathParameter.getCollectionFormat());
            if (pathParameter.getItems() != null) {
                putIfNotNull(values, RmdParameter.META_DATA_ARRAY_TYPE, pathParameter.getItems().getType());
            }
            putIfNotNull(values, RmdParameter.META_DATA_ARRAY_ENUM, getEnumFromCollection(pathParameter.getItems()));
        }

        return values;
    }

    private static Map<String, Object> setValuesForFormParameter(FormParameter formParameter) {
        Map<String, Object> values = new HashMap<>();

        values.put(KEY_TYPE, getTypeForParameter(formParameter.getType()));

        putIfNotNull(values, RmdParameter.META_DATA_FORMAT, formParameter.getFormat());

        putIfNotNull(values, RmdParameter.META_DATA_MIN_VALUE, formParameter.getMinimum());
        putIfNotNull(values, RmdParameter.META_DATA_MAX_VALUE, formParameter.getMaximum());

        putIfNotNull(values, RmdParameter.META_DATA_MIN_LENGTH, formParameter.getMinLength());
        putIfNotNull(values, RmdParameter.META_DATA_MAX_LENGTH, formParameter.getMaxLength());

        putIfNotNull(values, RmdParameter.META_DATA_MIN_ITEMS, formParameter.getMinItems());
        putIfNotNull(values, RmdParameter.META_DATA_MAX_ITEMS, formParameter.getMaxItems());

        if (formParameter.getEnum() != null) {
            putIfNotNull(values, RmdParameter.META_DATA_ENUM,
                    formParameter.getEnum().stream().collect(Collectors.joining(", ")));
        }

        putIfNotNull(values, RmdParameter.META_DATA_DEFAULT, formParameter.getDefaultValue());

        if (TYPE_ARRAY.equals(getTypeForParameter(formParameter.getType()))) {
            putIfNotNull(values, RmdParameter.META_DATA_ARRAY_FORMAT, formParameter.getCollectionFormat());
            if (formParameter.getItems() != null) {
                putIfNotNull(values, RmdParameter.META_DATA_ARRAY_TYPE, formParameter.getItems().getType());
            }
            putIfNotNull(values, RmdParameter.META_DATA_ARRAY_ENUM, getEnumFromCollection(formParameter.getItems()));
        }

        return values;
    }

    private static String getEnumFromCollection(Property property) {
        if (property == null || property.getType() == null) {
            return null;
        }
        String result = null;

        switch (property.getType()) {
        case StringProperty.TYPE:
            StringProperty stringProperty = (StringProperty) property;
            if (stringProperty.getEnum() != null) {
                result = stringProperty.getEnum().stream().collect(Collectors.joining(Constants.VALUE_SEPERATOR));
            }
            break;
        case BaseIntegerProperty.TYPE:
            // no enum
            break;
        default:
            logger.warn(String.format(Constants.Extractor.UNKONW_ENUM_TYPE, property.getType()));
            break;
        }
        return result;
    }

    private static String getTypeForParameter(String type) {
        if (type == null) {
            // default to String, for date/datetime types it may not be set
            type = ParameterType.STRING.toString();
        }
        return type;
    }

    private static void putIfNotNull(Map<String, Object> values, String key, Object value) {
        if (value != null) {
            values.put(key, value);
        }
    }
}