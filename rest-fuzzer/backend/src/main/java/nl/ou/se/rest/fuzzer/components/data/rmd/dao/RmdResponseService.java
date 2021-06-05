package nl.ou.se.rest.fuzzer.components.data.rmd.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import nl.ou.se.rest.fuzzer.components.data.rmd.domain.RmdResponse;

public interface RmdResponseService extends CrudRepository<RmdResponse, Long> {

    @Query(value = "SELECT r.id FROM rmd_responses r LEFT JOIN r.action a LEFT JOIN a.sut s WHERE s.id = :sutId")
    List<Long> findIdsBySutId(Long sutId);

    @Modifying
    @Query(value = "DELETE FROM rmd_responses r WHERE r.id IN (:ids)")
    Integer deleteByIds(List<Long> ids);

}