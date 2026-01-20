package com.ddmtchr.dbarefactor.dto;

import java.time.LocalDate;

public interface HasDates {
    LocalDate getStartDate();
    LocalDate getEndDate();
}
