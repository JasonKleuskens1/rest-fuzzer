package nl.ou.se.rest.fuzzer.components.data.rmd.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import nl.ou.se.rest.fuzzer.components.data.rmd.domain.RmdSut;
import nl.ou.se.rest.fuzzer.components.shared.Constants;

public interface RmdSutService extends CrudRepository<RmdSut, Long> {

    @Query("SELECT s FROM rmd_suts s ORDER BY s.createdAt DESC")
    List<RmdSut> findAll();

    @EntityGraph(value = Constants.ENTITY_GRAPH_RMD_SUTS_ALL_RELATIONS)
    Optional<RmdSut> findById(Long id);

}