package nl.ou.se.rest.fuzzer.components.service.rmd.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;

import nl.ou.se.rest.fuzzer.components.data.rmd.domain.RmdParameter;
import nl.ou.se.rest.fuzzer.components.service.rmd.domain.RmdParameterDto;
import nl.ou.se.rest.fuzzer.components.shared.JsonUtil;

public class RmdParameterMapper {

    public static List<RmdParameterDto> toDtos(List<RmdParameter> parameters, boolean mapAction) {
        return parameters.stream().map(p -> RmdParameterMapper.toDto(p, mapAction)).collect(Collectors.toList());
    }

    public static RmdParameterDto toDto(RmdParameter parameter, boolean mapAction) {
        RmdParameterDto dto = null;
        dto = new RmdParameterDto();
        BeanUtils.copyProperties(parameter, dto);
        dto.setMetaDataTuplesJson(JsonUtil.mapToString(parameter.getMetaDataTuples()));
        if (mapAction) {
            dto.setAction(RmdActionMapper.toDto(parameter.getAction(), false));
        }
        return dto;
    }
}