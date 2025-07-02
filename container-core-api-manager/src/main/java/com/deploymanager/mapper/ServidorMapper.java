package com.deploymanager.mapper;

import com.deploymanager.dto.ServidorDto;
import com.deploymanager.model.Servidor;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ServidorMapper {

    ServidorDto toDto(Servidor entity);

    @Mapping(target = "password", source = "password")
    @Mapping(target = "estado",   source = "estado")
    Servidor toEntity(ServidorDto dto);

    List<ServidorDto> toDtoList(List<Servidor> entities);

    List<Servidor> toEntityList(List<ServidorDto> dtos);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "password", source = "password")
    void updateServidorFromDto(ServidorDto dto, @MappingTarget Servidor entity);
}
