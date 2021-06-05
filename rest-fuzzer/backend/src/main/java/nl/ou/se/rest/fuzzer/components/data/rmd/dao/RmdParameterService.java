package nl.ou.se.rest.fuzzer.components.data.rmd.dao;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import nl.ou.se.rest.fuzzer.components.data.rmd.domain.ParameterContext;
import nl.ou.se.rest.fuzzer.components.data.rmd.domain.ParameterType;
import nl.ou.se.rest.fuzzer.components.data.rmd.domain.RmdParameter;

public interface RmdParameterService extends CrudRepository<RmdParameter, Long> {

    @Query(value = "SELECT COUNT(p) FROM rmd_parameters p LEFT JOIN p.action a LEFT JOIN a.sut s WHERE s.id = :sutId AND p.name LIKE CONCAT('%', :name, '%') AND p.context IN (:parameterContexts) AND p.type IN (:parameterTypes)")
    Long countByFilter(Long sutId, List<ParameterContext> parameterContexts, List<ParameterType> parameterTypes,
            String name);

    @Query(value = "SELECT p FROM rmd_parameters p LEFT JOIN FETCH p.action a LEFT JOIN a.sut s WHERE s.id = :sutId AND p.name LIKE CONCAT('%', :name, '%') AND p.context IN (:parameterContexts) AND p.type IN (:parameterTypes)")
    List<RmdParameter> findByFilter(Long sutId, List<ParameterContext> parameterContexts,
            List<ParameterType> parameterTypes, String name, Pageable pageable);

    @Query(value = "SELECT p FROM rmd_parameters p WHERE p.action.id = :actionId ORDER BY p.position ASC")
    List<RmdParameter> findByActionId(Long actionId);

    @Query(value = "SELECT p.id FROM rmd_parameters p LEFT JOIN p.action a LEFT JOIN a.sut s WHERE s.id = :sutId")
    List<Long> findIdsBySutId(Long sutId);

    @Modifying
    @Query(value = "DELETE FROM rmd_parameters p WHERE p.id IN (:ids)")
    Integer deleteByIds(List<Long> ids);

}