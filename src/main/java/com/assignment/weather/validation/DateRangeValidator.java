package com.assignment.weather.validation;

import com.assignment.weather.dto.MetricsRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

/**
 * Validates that the contains a valid date range.
 */
public class DateRangeValidator implements ConstraintValidator<ValidDateRange, MetricsRequest> {

    @Override
    public boolean isValid(final MetricsRequest request, final ConstraintValidatorContext context) {
        if (request == null) {
            return true;
        }

        final LocalDate currentDate = LocalDate.now();
        final LocalDate startDate = request.getStartDate();
        final LocalDate endDate = request.getEndDate();

        if (startDate == null && endDate == null) {
            return true;
        }

        // Both dates must be provided together
        if (startDate == null || endDate == null) {
            addConstraintViolation(context, "Both startDate and endDate must be provided.");
            return false;
        }

        // End date must be after or equal to start date
        if (endDate.isBefore(startDate)) {
            addConstraintViolation(context, "End date must be after or equal to start date.");
            return false;
        }

        // Dates must not be in the future
        if (startDate.isAfter(currentDate) || endDate.isAfter(currentDate)) {
            addConstraintViolation(context, "Dates cannot be in the future.");
            return false;
        }

        // Dates must not be older than 1 month
        final LocalDate oneMonthAgo = currentDate.minusMonths(1);
        if (startDate.isBefore(oneMonthAgo) || endDate.isBefore(oneMonthAgo)) {
            addConstraintViolation(context, "Dates cannot be older than 1 month.");
            return false;
        }

        return true;
    }
    private void addConstraintViolation(final ConstraintValidatorContext context, final String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
    }
}