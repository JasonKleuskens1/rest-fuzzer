package nl.ou.se.rest.fuzzer.components.service.report.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;

import nl.ou.se.rest.fuzzer.components.data.report.domain.Report;
import nl.ou.se.rest.fuzzer.components.service.fuz.mapper.FuzProjectMapper;
import nl.ou.se.rest.fuzzer.components.service.report.domain.ReportDto;

public class ReportMapper {

    public static List<ReportDto> toDtos(List<Report> reports) {
        return reports.stream().map(r -> ReportMapper.toDto(r)).collect(Collectors.toList());
    }

    public static ReportDto toDto(Report report) {
        ReportDto dto = new ReportDto();
        BeanUtils.copyProperties(report, dto);
        dto.setProject(FuzProjectMapper.toDto(report.getProject(), false));
         return dto;
    }

    public static Report toDomain(ReportDto dto) {
        Report report = new Report();
        BeanUtils.copyProperties(dto, report);
        return report;
    }
}