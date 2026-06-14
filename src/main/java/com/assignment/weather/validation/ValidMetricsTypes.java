package com.assignment.weather.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = MetricsTypesValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidMetricsTypes {

    String message() default "metricsTypes is required and cannot be empty";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}