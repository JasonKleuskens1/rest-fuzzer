package nl.ou.se.rest.fuzzer.components.data.fuz.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import nl.ou.se.rest.fuzzer.components.data.fuz.domain.FuzConfiguration;

public interface FuzConfigurationService extends CrudRepository<FuzConfiguration, Long> {

    @Query("SELECT c FROM fuz_configurations c ORDER BY c.createdAt DESC")
	List<FuzConfiguration> findAll();

    Optional<FuzConfiguration> findById(Long id);

}