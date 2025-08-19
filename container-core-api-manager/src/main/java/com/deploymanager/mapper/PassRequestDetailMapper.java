package com.deploymanager.mapper;

import com.deploymanager.dto.PassRequestDetailDto;
import com.deploymanager.model.PassRequestDetail;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PassRequestDetailMapper {
    PassRequestDetailDto toDto(PassRequestDetail entity);

    PassRequestDetail toEntity(PassRequestDetailDto dto);

    List<PassRequestDetailDto> toDtoList(List<PassRequestDetail> entities);

    List<PassRequestDetail> toEntityList(List<PassRequestDetailDto> dtos);
}
