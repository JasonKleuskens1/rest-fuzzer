package nl.ou.se.rest.fuzzer.components.data.fuz.dao;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import nl.ou.se.rest.fuzzer.components.data.fuz.domain.FuzRequest;
import nl.ou.se.rest.fuzzer.components.data.rmd.domain.HttpMethod;

public interface FuzRequestService extends CrudRepository<FuzRequest, Long> {

    @Query(value = "SELECT COUNT(r) FROM fuz_requests r WHERE r.project.id = :projectId AND r.httpMethod IN (:httpMethods) AND r.path LIKE CONCAT('%', :path, '%')")
	Long countByFilter(Long projectId, List<HttpMethod> httpMethods, String path);

    @Query(value = "SELECT r FROM fuz_requests r WHERE r.project.id = :projectId AND r.httpMethod IN (:httpMethods) AND r.path LIKE CONCAT('%', :path, '%')")
    List<FuzRequest> findByFilter(Long projectId, List<HttpMethod> httpMethods, String path, Pageable pageable);

    @Modifying
    @Query(value = "DELETE FROM fuz_requests r WHERE r.project.id = :projectId")
    Integer deleteByProjectId(Long projectId);

}