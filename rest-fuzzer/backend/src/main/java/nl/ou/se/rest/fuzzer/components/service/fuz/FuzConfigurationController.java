package nl.ou.se.rest.fuzzer.components.service.fuz;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import nl.ou.se.rest.fuzzer.components.data.fuz.dao.FuzConfigurationService;
import nl.ou.se.rest.fuzzer.components.data.fuz.domain.FuzConfiguration;
import nl.ou.se.rest.fuzzer.components.service.fuz.domain.FuzConfigurationDto;
import nl.ou.se.rest.fuzzer.components.service.fuz.mapper.FuzConfigurationMapper;
import nl.ou.se.rest.fuzzer.components.service.util.ValidatorUtil;
import nl.ou.se.rest.fuzzer.components.shared.Constants;

@RestController()
@RequestMapping("/rest/configurations")
public class FuzConfigurationController {

    // variable(s)
    private Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    @Autowired
    FuzConfigurationService configurationService;

    // method(s)
    @RequestMapping(method = RequestMethod.GET)
    public @ResponseBody List<FuzConfigurationDto> findAll() {
        List<FuzConfiguration> configurations = configurationService.findAll();
        return FuzConfigurationMapper.toDtos(configurations);
    }

    @RequestMapping(path = "{id}", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<?> findById(@PathVariable(name = "id") Long id) {
        Optional<FuzConfiguration> configuration = configurationService.findById(id);

        if (!configuration.isPresent()) {
            logger.warn(String.format(Constants.Service.VALIDATION_OBJECT_NOT_FOUND, FuzConfiguration.class, id));
            return ResponseEntity.badRequest().body(new FuzConfigurationDto());
        }

        return ResponseEntity.ok(FuzConfigurationMapper.toDto(configuration.get()));
    }

    @RequestMapping(method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<?> add(@RequestBody FuzConfigurationDto configurationDto) {
        FuzConfiguration configuration = FuzConfigurationMapper.toDomain(configurationDto);
        configuration.setCreatedAt(LocalDateTime.now());

        List<String> violations = ValidatorUtil.getViolations(configuration);
        if (!violations.isEmpty()) {
            logger.warn(
                    String.format(Constants.Service.VALIDATION_OBJECT_FAILED, FuzConfiguration.class, violations.size()));
            return ValidatorUtil.getResponseForViolations(violations);
        }

        configuration = configurationService.save(configuration);
        return ResponseEntity.ok(FuzConfigurationMapper.toDto(configuration));
    }

    @RequestMapping(path = "{id}", method = RequestMethod.DELETE)
    public @ResponseBody ResponseEntity<?> delete(@PathVariable(name = "id") Long id) {
        Optional<FuzConfiguration> configuration = configurationService.findById(id);

        if (!configuration.isPresent()) {
            logger.warn(String.format(Constants.Service.VALIDATION_OBJECT_NOT_FOUND, FuzConfigurationDto.class, id));
            return ResponseEntity.badRequest().body(new FuzConfigurationDto());
        }

        configurationService.deleteById(id);
        return ResponseEntity.ok(FuzConfigurationMapper.toDto(configuration.get()));
    }
}