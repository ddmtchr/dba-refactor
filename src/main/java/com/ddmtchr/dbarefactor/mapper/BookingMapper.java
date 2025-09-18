package com.ddmtchr.dbarefactor.mapper;

import com.ddmtchr.dbarefactor.dto.request.BookingChangesDto;
import com.ddmtchr.dbarefactor.dto.request.BookingRequestDto;
import com.ddmtchr.dbarefactor.dto.response.BookingResponseDto;
import com.ddmtchr.dbarefactor.entity.Booking;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper
public interface BookingMapper {
    BookingMapper INSTANCE = Mappers.getMapper(BookingMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "guest", ignore = true)
    @Mapping(target = "estate", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "amount", ignore = true)
    @Mapping(target = "paymentRequestTime", ignore = true)
    @Mapping(target = "payoutScheduledAt", ignore = true)
    Booking toEntity(BookingRequestDto dto);

    @Mapping(target = "guestId", source = "guest.id")
    @Mapping(target = "estateId", source = "estate.id")
    BookingResponseDto toResponseDto(Booking e);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "guest", ignore = true)
    @Mapping(target = "estate", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "paymentRequestTime", ignore = true)
    @Mapping(target = "payoutScheduledAt", ignore = true)
    @Mapping(target = "amount", ignore = true)
    void updateBooking(BookingChangesDto dto, @MappingTarget Booking e);
}
