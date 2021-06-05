package nl.ou.se.rest.fuzzer.components.data.rmd.dao;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import nl.ou.se.rest.fuzzer.components.data.rmd.domain.HttpMethod;
import nl.ou.se.rest.fuzzer.components.data.rmd.domain.RmdAction;

public interface RmdActionService extends CrudRepository<RmdAction, Long> {

    @Query(value = "SELECT DISTINCT a FROM rmd_actions a LEFT JOIN FETCH a.parameters LEFT JOIN FETCH a.sut WHERE a.sut.id = :sutId")
    List<RmdAction> findBySutId(Long sutId);

    @Query(value = "SELECT COUNT(a) FROM rmd_actions a WHERE a.sut.id = :sutId AND a.httpMethod IN (:httpMethods) AND a.path LIKE CONCAT('%', :path, '%')")
    Long countByFilter(Long sutId, List<HttpMethod> httpMethods, String path);

    @Query(value = "SELECT a FROM rmd_actions a WHERE a.sut.id = :sutId AND a.httpMethod IN (:httpMethods) AND a.path LIKE CONCAT('%', :path, '%')")
    List<RmdAction> findByFilter(Long sutId, List<HttpMethod> httpMethods, String path, Pageable pageable);

    @Modifying
    @Query(value = "DELETE FROM rmd_actions a WHERE a.sut.id = :sutId")
    Integer deleteBySutId(Long sutId);

}