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

import nl.ou.se.rest.fuzzer.components.data.fuz.dao.FuzDictionaryService;
import nl.ou.se.rest.fuzzer.components.data.fuz.domain.FuzDictionary;
import nl.ou.se.rest.fuzzer.components.service.fuz.domain.FuzDictionaryDto;
import nl.ou.se.rest.fuzzer.components.service.fuz.domain.FuzProjectDto;
import nl.ou.se.rest.fuzzer.components.service.fuz.mapper.FuzDictionaryMapper;
import nl.ou.se.rest.fuzzer.components.service.util.ValidatorUtil;
import nl.ou.se.rest.fuzzer.components.shared.Constants;

@RestController()
@RequestMapping("/rest/dictionaries")
public class FuzDictionaryController {

    // variable(s)
    private Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    @Autowired
    FuzDictionaryService dictionaryService;

    // method(s)
    @RequestMapping(method = RequestMethod.GET)
    public @ResponseBody List<FuzDictionaryDto> findAll() {
        List<FuzDictionary> dictionaries = dictionaryService.findAll();
        return FuzDictionaryMapper.toDtos(dictionaries);
    }

    @RequestMapping(path = "{id}", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<?> findById(@PathVariable(name = "id") Long id) {
        Optional<FuzDictionary> dictionary = dictionaryService.findById(id);

        if (!dictionary.isPresent()) {
            logger.warn(String.format(Constants.Service.VALIDATION_OBJECT_NOT_FOUND, FuzDictionary.class, id));
            return ResponseEntity.badRequest().body(new FuzProjectDto());
        }

        return ResponseEntity.ok(FuzDictionaryMapper.toDto(dictionary.get()));
    }

    @RequestMapping(method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<?> add(@RequestBody FuzDictionaryDto dictionaryDto) {
        FuzDictionary dictionary = FuzDictionaryMapper.toDomain(dictionaryDto);
        dictionary.setCreatedAt(LocalDateTime.now());

        List<String> violations = ValidatorUtil.getViolations(dictionary);
        if (!violations.isEmpty()) {
            logger.warn(String.format(Constants.Service.VALIDATION_OBJECT_FAILED, FuzDictionary.class, violations.size()));
            return ValidatorUtil.getResponseForViolations(violations);
        }

        dictionary = dictionaryService.save(dictionary);
        return ResponseEntity.ok(FuzDictionaryMapper.toDto(dictionary));
    }

    @RequestMapping(path = "{id}", method = RequestMethod.DELETE)
    public @ResponseBody ResponseEntity<?> delete(@PathVariable(name = "id") Long id) {
        Optional<FuzDictionary> dictionary = dictionaryService.findById(id);

        if (!dictionary.isPresent()) {
            logger.warn(String.format(Constants.Service.VALIDATION_OBJECT_NOT_FOUND, FuzDictionary.class, id));
            return ResponseEntity.badRequest().body(new FuzDictionaryDto());
        }

        dictionaryService.deleteById(id);
        return ResponseEntity.ok(FuzDictionaryMapper.toDto(dictionary.get()));
    }
}