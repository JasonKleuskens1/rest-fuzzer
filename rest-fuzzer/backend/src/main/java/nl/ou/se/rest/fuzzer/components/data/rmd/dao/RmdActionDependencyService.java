package nl.ou.se.rest.fuzzer.components.data.rmd.dao;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import nl.ou.se.rest.fuzzer.components.data.rmd.domain.DiscoveryModus;
import nl.ou.se.rest.fuzzer.components.data.rmd.domain.HttpMethod;
import nl.ou.se.rest.fuzzer.components.data.rmd.domain.RmdActionDependency;

public interface RmdActionDependencyService extends CrudRepository<RmdActionDependency, Long> {

    @Query(value = "SELECT DISTINCT d FROM rmd_actions_dependencies d LEFT JOIN FETCH d.action a LEFT JOIN FETCH a.sut WHERE a.sut.id = :sutId")
    List<RmdActionDependency> findBySutId(Long sutId);

    @Query(value = "SELECT COUNT(DISTINCT d) FROM rmd_actions_dependencies d LEFT JOIN d.action a LEFT JOIN a.sut WHERE a.sut.id = :sutId AND d.discoveryModus IN (:discoveryModes) AND a.httpMethod IN (:httpMethods) AND a.path LIKE CONCAT('%', :path, '%')")
    Long countByFilter(Long sutId, List<DiscoveryModus> discoveryModes, List<HttpMethod> httpMethods, String path);

    @Query(value = "SELECT DISTINCT d FROM rmd_actions_dependencies d LEFT JOIN FETCH d.action a LEFT JOIN FETCH a.sut WHERE a.sut.id = :sutId AND d.discoveryModus IN (:discoveryModes) AND a.httpMethod IN (:httpMethods) AND a.path LIKE CONCAT('%', :path, '%')")
    List<RmdActionDependency> findByFilter(Long sutId, List<DiscoveryModus> discoveryModes,
            List<HttpMethod> httpMethods, String path, Pageable pageable);

    @Query(value = "SELECT d.id FROM rmd_actions_dependencies d LEFT JOIN d.action a LEFT JOIN a.sut s WHERE s.id = :sutId")
    List<Long> findIdsBySutId(Long sutId);

    @Modifying
    @Query(value = "DELETE FROM rmd_actions_dependencies d WHERE d.id IN (:ids)")
    Integer deleteByIds(List<Long> ids);

    @Query(value = "SELECT DISTINCT d FROM rmd_actions_dependencies d LEFT JOIN d.parameter p WHERE p.id = :parameterId")
    RmdActionDependency findByParameterId(Long parameterId);
    
}