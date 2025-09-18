package com.ddmtchr.dbarefactor.mapper;

import com.ddmtchr.dbarefactor.dto.request.EstateRequestDto;
import com.ddmtchr.dbarefactor.dto.response.EstateResponseDto;
import com.ddmtchr.dbarefactor.entity.Estate;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface EstateMapper {
    EstateMapper INSTANCE = Mappers.getMapper(EstateMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "owner", ignore = true)
    Estate toEntity(EstateRequestDto dto);

    @Mapping(target = "ownerId", source = "owner.id")
    EstateResponseDto toResponseDto(Estate e);

}
