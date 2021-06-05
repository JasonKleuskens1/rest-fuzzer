package nl.ou.se.rest.fuzzer.components.service.fuz.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;

import nl.ou.se.rest.fuzzer.components.data.fuz.domain.FuzProject;
import nl.ou.se.rest.fuzzer.components.service.fuz.domain.FuzProjectDto;
import nl.ou.se.rest.fuzzer.components.service.rmd.mapper.RmdSutMapper;

public class FuzProjectMapper {

	// method(s)
	public static List<FuzProjectDto> toDtos(List<FuzProject> projects) {
		return projects.stream().map(p -> FuzProjectMapper.toDto(p, true)).collect(Collectors.toList());
	}

	public static FuzProjectDto toDto(FuzProject project, boolean mapRelations) {
		FuzProjectDto dto = new FuzProjectDto();
		BeanUtils.copyProperties(project, dto);
		if (mapRelations) {
			dto.setSut(RmdSutMapper.toDto(project.getSut(), false));
		}
		return dto;
	}

	public static FuzProject toDomain(FuzProjectDto dto) {
		FuzProject project = new FuzProject();
		BeanUtils.copyProperties(dto, project);
		return project;
	}
}
