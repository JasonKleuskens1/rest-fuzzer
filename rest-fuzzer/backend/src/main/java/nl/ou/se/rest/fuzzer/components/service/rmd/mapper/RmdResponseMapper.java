package nl.ou.se.rest.fuzzer.components.service.rmd.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;

import nl.ou.se.rest.fuzzer.components.data.rmd.domain.RmdResponse;
import nl.ou.se.rest.fuzzer.components.service.rmd.domain.RmdResponseDto;

public class RmdResponseMapper {

    public static List<RmdResponseDto> toDtos(List<RmdResponse> responses) {
        return responses.stream().map(r -> RmdResponseMapper.toDto(r)).collect(Collectors.toList());
    }

    private static RmdResponseDto toDto(RmdResponse response) {
        RmdResponseDto dto = new RmdResponseDto();
        BeanUtils.copyProperties(response, dto);
        return dto;
    }
}