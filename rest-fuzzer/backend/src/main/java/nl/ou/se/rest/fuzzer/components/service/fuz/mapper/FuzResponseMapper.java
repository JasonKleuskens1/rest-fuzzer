package nl.ou.se.rest.fuzzer.components.service.fuz.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;

import nl.ou.se.rest.fuzzer.components.data.fuz.domain.FuzResponse;
import nl.ou.se.rest.fuzzer.components.service.fuz.domain.FuzResponseDto;

public class FuzResponseMapper {

    public static List<FuzResponseDto> toDtos(List<FuzResponse> responses) {
        return responses.stream().map(r -> FuzResponseMapper.toDto(r)).collect(Collectors.toList());
    }

    public static FuzResponseDto toDto(FuzResponse response) {
        FuzResponseDto dto = new FuzResponseDto();
        BeanUtils.copyProperties(response, dto);
        dto.setRequest(FuzRequestMapper.toDto(response.getRequest(), false));
        return dto;
    }
}