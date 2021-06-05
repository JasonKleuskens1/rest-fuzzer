package nl.ou.se.rest.fuzzer.components.data.fuz.dao;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import nl.ou.se.rest.fuzzer.components.data.fuz.domain.FuzResponse;
import nl.ou.se.rest.fuzzer.components.data.rmd.domain.HttpMethod;

public interface FuzResponseService extends CrudRepository<FuzResponse, Long> {

    @Query(value = "SELECT COUNT(r) FROM fuz_responses r LEFT JOIN r.request req WHERE r.project.id = :projectId AND req.httpMethod IN (:httpMethods) AND r.statusCode IN (:statusCodes) AND r.request.path LIKE :path")
    Long countByFilter(Long projectId, List<HttpMethod> httpMethods, List<Integer> statusCodes, String path);

    @Query(value = "SELECT r FROM fuz_responses r LEFT JOIN FETCH r.request req WHERE r.project.id = :projectId AND req.httpMethod IN (:httpMethods) AND r.statusCode IN (:statusCodes) AND r.request.path LIKE :path")
    List<FuzResponse> findByFilter(Long projectId, List<HttpMethod> httpMethods, List<Integer> statusCodes, String path, Pageable pageable);

    @Query(value = "SELECT DISTINCT(r.statusCode) FROM fuz_responses r WHERE r.project.id = :projectId ORDER BY r.statusCode")
    List<Integer> findUniqueStatusCodesForProject(Long projectId);

    @Query(value = "SELECT r.statusCode, COUNT(r) AS count FROM fuz_responses r WHERE r.project.id = :projectId GROUP BY r.statusCode ORDER BY COUNT(r) DESC")
    List<Object[]> findUniqueStatusCodesForProjectOrderByPresence(Long projectId);

    @Modifying
    @Query(value = "DELETE FROM fuz_responses r WHERE r.project.id = :projectId")
    void deleteByProjectId(Long projectId);

    @Query(value = "SELECT r, req, a, d FROM fuz_responses r LEFT JOIN r.request req LEFT JOIN req.sequence s LEFT JOIN FETCH req.action a LEFT JOIN FETCH a.dependencies d WHERE s.id = :sequenceId")
    List<FuzResponse> findBySequenceId(Long sequenceId);

    @Query(value = "SELECT MIN(r.createdAt) FROM fuz_responses r WHERE r.project.id = :projectId")
    LocalDateTime findMinCreatedByProjectId(Long projectId);

    @Query(value = "SELECT r.statusCode, COUNT(r) AS count FROM fuz_responses r WHERE r.project.id = :projectId AND r.id <= :responseId GROUP BY r.statusCode ORDER BY r.id")
    List<Object[]> findStatusCodesAndCountsByProjectIdAndMaxId(Long projectId, Long responseId);

    @Query(value = "SELECT r FROM fuz_responses r LEFT JOIN FETCH r.request req WHERE r.project.id = :projectId ORDER BY r.id")
    List<FuzResponse> findByProjectId(Long projectId, Pageable pageable);

    // get the last response
    List<FuzResponse> findTopByProjectIdOrderByIdDesc(Long projectId);

    @Query(value = "SELECT COUNT(r) AS count FROM fuz_responses r WHERE r.project.id = :projectId AND r.id >= :fromId AND r.id <= :untilId")
    Long countByProjectIdAndFromIdAndUntilId(Long projectId, Long fromId, Long untilId);

}