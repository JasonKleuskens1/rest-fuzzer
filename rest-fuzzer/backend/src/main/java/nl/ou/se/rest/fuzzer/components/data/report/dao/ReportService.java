package nl.ou.se.rest.fuzzer.components.data.report.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import nl.ou.se.rest.fuzzer.components.data.report.domain.Report;

public interface ReportService extends CrudRepository<Report, Long> {

    @Query("SELECT r FROM reports r ORDER BY r.createdAt DESC")
    List<Report> findAll();

    Optional<Report> findById(Long id);

}
