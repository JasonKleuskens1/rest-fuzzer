package nl.ou.se.rest.fuzzer.components.service.fuz.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;

import nl.ou.se.rest.fuzzer.components.data.fuz.domain.FuzSequence;
import nl.ou.se.rest.fuzzer.components.service.fuz.domain.FuzSequenceDto;

public class FuzSequenceMapper {

    public static List<FuzSequenceDto> toDtos(List<FuzSequence> sequences) {
        return sequences.stream().map(s -> FuzSequenceMapper.toDto(s, true)).collect(Collectors.toList());
    }

    public static FuzSequenceDto toDto(FuzSequence sequence, boolean mapRequests) {
        FuzSequenceDto dto = new FuzSequenceDto();
        BeanUtils.copyProperties(sequence, dto);
        if (mapRequests) { 
            dto.setRequests(FuzRequestMapper.toDtos(sequence.getRequests().stream().collect(Collectors.toList())));
        }
        return dto;
    }
}