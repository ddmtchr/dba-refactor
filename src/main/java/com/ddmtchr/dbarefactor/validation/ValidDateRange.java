package com.ddmtchr.dbarefactor.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = DateRangeValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidDateRange {
    String message() default "startDate must be before or equal to endDate";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
