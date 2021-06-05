package nl.ou.se.rest.fuzzer.components.service.util;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ValidatorUtil {

    private static Logger logger = LoggerFactory.getLogger(ValidatorUtil.class);

    // method(s)
    public static List<String> getViolations(Object o) {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        return validator.validate(o).stream().map(v -> String.format("%s: %s", v.getPropertyPath(), v.getMessage()))
                .collect(Collectors.toList());
    }

    public static ResponseEntity<?> getResponseForViolations(List<String> violations) {
        String json = "";
        try {
            json = new ObjectMapper().writeValueAsString(new HttpResponseDto(violations));
        } catch (JsonProcessingException e) {
            logger.warn("Building response for violations failed: " + e.getMessage());
        }
        return ResponseEntity.badRequest().body(json);
    }

    public static ResponseEntity<?> getResponseForViolation(String violation) {
        return getResponseForViolations(Arrays.asList(violation));
    }
}
