package nl.ou.se.rest.fuzzer.components.service.rmd.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;

import nl.ou.se.rest.fuzzer.components.data.rmd.domain.RmdSut;
import nl.ou.se.rest.fuzzer.components.service.rmd.domain.RmdSutDto;

public abstract class RmdSutMapper {

	// method(s)
	public static List<RmdSutDto> toDtos(List<RmdSut> suts) {
		return suts.stream().map(s -> RmdSutMapper.toDto(s, false)).collect(Collectors.toList());
	}

	public static RmdSutDto toDto(RmdSut sut, boolean mapRelations) {
        RmdSutDto dto = new RmdSutDto();
        BeanUtils.copyProperties(sut, dto);
        return dto;
    }

    public static RmdSut toDomain(RmdSutDto dto) {
        RmdSut sut = new RmdSut();
        BeanUtils.copyProperties(dto, sut);
        return sut;
    }
}