package com.assignment.weather.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = StatisticTypeValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidStatisticType {

    String message() default "statisticType is required when dates are provided";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}