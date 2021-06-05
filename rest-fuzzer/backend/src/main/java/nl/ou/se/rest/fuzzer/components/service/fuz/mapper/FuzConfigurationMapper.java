package nl.ou.se.rest.fuzzer.components.service.fuz.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;

import nl.ou.se.rest.fuzzer.components.data.fuz.domain.FuzConfiguration;
import nl.ou.se.rest.fuzzer.components.service.fuz.domain.FuzConfigurationDto;

public class FuzConfigurationMapper {

    public static List<FuzConfigurationDto> toDtos(List<FuzConfiguration> configurations) {
        return configurations.stream().map(d -> FuzConfigurationMapper.toDto(d)).collect(Collectors.toList());
    }

    public static FuzConfigurationDto toDto(FuzConfiguration configuration) {
        FuzConfigurationDto dto = new FuzConfigurationDto();
        BeanUtils.copyProperties(configuration, dto);
        return dto;
    }

    public static FuzConfiguration toDomain(FuzConfigurationDto configurationDto) {
        FuzConfiguration configuration = new FuzConfiguration(configurationDto.getName(), configurationDto.getItemsJson());
        return configuration;
    }
}