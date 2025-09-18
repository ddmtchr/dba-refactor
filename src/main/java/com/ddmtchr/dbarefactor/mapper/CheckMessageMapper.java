package com.ddmtchr.dbarefactor.mapper;

import com.ddmtchr.dbarefactor.dto.producer.CheckDto;
import com.ddmtchr.dbarefactor.entity.CheckMessage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CheckMessageMapper {
    CheckMessageMapper INSTANCE = Mappers.getMapper(CheckMessageMapper.class);

    @Mapping(target = "sent", ignore = true)
    CheckMessage toEntity(CheckDto dto);

    CheckDto toDto(CheckMessage e);
}
