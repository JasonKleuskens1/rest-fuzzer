package nl.ou.se.rest.fuzzer.components.service.fuz.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;

import nl.ou.se.rest.fuzzer.components.data.fuz.domain.FuzRequest;
import nl.ou.se.rest.fuzzer.components.service.fuz.domain.FuzRequestDto;
import nl.ou.se.rest.fuzzer.components.service.rmd.mapper.RmdActionMapper;

public class FuzRequestMapper {

    public static List<FuzRequestDto> toDtos(List<FuzRequest> requests) {
        return requests.stream().map(r -> FuzRequestMapper.toDto(r, true)).collect(Collectors.toList());
    }

    public static FuzRequestDto toDto(FuzRequest request, boolean mapResponse) {
        FuzRequestDto dto = new FuzRequestDto();
        BeanUtils.copyProperties(request, dto);
        dto.setAction(RmdActionMapper.toDto(request.getAction(), true));
        if (request.getSequence() != null) {
            dto.setSequence(FuzSequenceMapper.toDto(request.getSequence(), false));
        }
        if (mapResponse && request.getResponse() != null) {
            dto.setResponse(FuzResponseMapper.toDto(request.getResponse()));
        }
        return dto;
    }
}