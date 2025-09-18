package com.ddmtchr.dbarefactor.validation;

import com.ddmtchr.dbarefactor.dto.request.BookingRequestDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DateRangeValidator implements ConstraintValidator<ValidDateRange, BookingRequestDto> {
    @Override
    public boolean isValid(BookingRequestDto value, ConstraintValidatorContext context) {
        if (value.getStartDate() == null || value.getEndDate() == null) {
            return true;
        }
        return !value.getStartDate().isAfter(value.getEndDate());
    }
}
