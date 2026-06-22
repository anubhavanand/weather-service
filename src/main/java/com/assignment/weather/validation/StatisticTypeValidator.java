package com.assignment.weather.validation;

import com.assignment.weather.dto.MetricsRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validates that statisticType is not null when dates are provided.
 */
public class StatisticTypeValidator implements ConstraintValidator<ValidStatisticType, MetricsRequest> {

    @Override
    public boolean isValid(final MetricsRequest request, final ConstraintValidatorContext context) {
        if (request == null || request.getStartDate() == null) {
            return true;
        }

        return request.getStatisticType() != null;
    }
}
