package nl.ou.se.rest.fuzzer.components.data.fuz.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import nl.ou.se.rest.fuzzer.components.data.fuz.domain.FuzDictionary;

public interface FuzDictionaryService extends CrudRepository<FuzDictionary, Long> {

    @Query("SELECT d FROM fuz_dictionaries d ORDER BY d.createdAt DESC")
	List<FuzDictionary> findAll();

    Optional<FuzDictionary> findById(Long id);

    @Query("SELECT d FROM fuz_dictionaries d WHERE d.id IN (:ids)")
    List<FuzDictionary> findByIds(List<Long> ids);

}