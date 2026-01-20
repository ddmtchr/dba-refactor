package com.ddmtchr.dbarefactor.validation;

import com.ddmtchr.dbarefactor.dto.HasDates;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DateRangeValidator implements ConstraintValidator<ValidDateRange, HasDates> {
    @Override
    public boolean isValid(HasDates value, ConstraintValidatorContext context) {
        if (value.getStartDate() == null || value.getEndDate() == null) {
            return true;
        }
        return !value.getStartDate().isAfter(value.getEndDate());
    }
}
