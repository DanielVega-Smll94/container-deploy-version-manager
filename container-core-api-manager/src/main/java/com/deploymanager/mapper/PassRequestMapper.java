package com.deploymanager.mapper;
import com.deploymanager.dto.PassRequestUpsertDto;
import com.deploymanager.dto.PassRequestDto;
import com.deploymanager.model.PassRequestHeader;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PassRequestMapper {
    PassRequestDto toDto(PassRequestHeader entity);

    PassRequestHeader toEntity(PassRequestUpsertDto dto);

    List<PassRequestDto> toDtoList(List<PassRequestHeader> entities);

    // 👇 Este método es el que usas para actualizar parcialmente
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateHeaderFromDto(PassRequestUpsertDto dto, @MappingTarget PassRequestHeader entity);
}
