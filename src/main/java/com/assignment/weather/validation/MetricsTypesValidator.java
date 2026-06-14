package com.assignment.weather.validation;

import com.assignment.weather.dto.MetricsRequest;
import com.assignment.weather.model.MetricType;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.List;

/**
 * Validates that the metricsTypes list is not null and not empty when dates are provided.
 */
public class MetricsTypesValidator implements ConstraintValidator<ValidMetricsTypes, MetricsRequest> {

    @Override
    public boolean isValid(final MetricsRequest request, final ConstraintValidatorContext context) {
        if (request == null || request.getStartDate() == null) {
            return true;
        }

        final List<MetricType> metricsTypes = request.getMetricsTypes();
        return metricsTypes != null && !metricsTypes.isEmpty();
    }
}
