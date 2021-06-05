package nl.ou.se.rest.fuzzer.components.data.fuz.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import nl.ou.se.rest.fuzzer.components.data.fuz.domain.FuzProject;

public interface FuzProjectService extends CrudRepository<FuzProject, Long> {

    @Query("SELECT p FROM fuz_projects p ORDER BY p.createdAt DESC")
    List<FuzProject> findAll();

    Optional<FuzProject> findById(Long id);

    Long countBySutId(Long id);

}