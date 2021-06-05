package nl.ou.se.rest.fuzzer.components.data.fuz.dao;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import nl.ou.se.rest.fuzzer.components.data.fuz.domain.FuzSequence;
import nl.ou.se.rest.fuzzer.components.data.fuz.domain.FuzSequenceStatus;

public interface FuzSequenceService extends CrudRepository<FuzSequence, Long> {

    @Query(value = "SELECT COUNT(s) FROM fuz_sequences s WHERE s.project.id = :projectId AND s.status IN (:statuses) AND s.length IN (:lengths) AND CAST(s.id AS string) LIKE :filterId")
    Long countByProjectId(Long projectId, List<FuzSequenceStatus> statuses, List<Integer> lengths, String filterId);

    @Query(value = "SELECT s FROM fuz_sequences s WHERE s.project.id = :projectId AND s.status IN (:statuses) AND s.length IN (:lengths) AND CAST(s.id AS string) LIKE :filterId")
    List<FuzSequence> findByProjectId(Long projectId, List<FuzSequenceStatus> statuses, List<Integer> lengths,
            String filterId, Pageable pageable);

    @Modifying
    @Query(value = "DELETE FROM fuz_sequences s WHERE s.project.id = :projectId")
    Integer deleteByProjectId(Long projectId);

}