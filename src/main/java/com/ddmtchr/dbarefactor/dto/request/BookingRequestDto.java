package com.ddmtchr.dbarefactor.dto.request;

import com.ddmtchr.dbarefactor.validation.ValidDateRange;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ValidDateRange
public class BookingRequestDto {
    @NotNull
    private Long guestId;

    @NotNull
    private Long estateId;

    @NotNull
    private LocalDate startDate;

    @NotNull
    private LocalDate endDate;
}
