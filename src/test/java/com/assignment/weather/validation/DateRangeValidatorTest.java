package com.assignment.weather.validation;

import com.assignment.weather.dto.MetricsRequest;
import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DateRangeValidatorTest {
    private DateRangeValidator validator;
    private ConstraintValidatorContext context;

    @BeforeEach
    void setUp() {
        validator = new DateRangeValidator();
        context = mock(ConstraintValidatorContext.class);

        // Stub the builder to prevent NPE when validator calls buildConstraintViolationWithTemplate()
        ConstraintValidatorContext.ConstraintViolationBuilder builder =
                mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
    }

    @Test
    void shouldReturnTrueWhenBothDatesAreNotProvided() {
        final MetricsRequest request = new MetricsRequest();
        assertTrue(validator.isValid(request, context));
    }

    @Test
    void shouldReturnFalseWhenOnlyOneDateIsProvided() {
        final MetricsRequest request = new MetricsRequest();
        request.setStartDate(LocalDate.now());
        request.setEndDate(null);
        assertFalse(validator.isValid(request, context));
    }

    @Test
    void shouldReturnFalseWhenStartDateIsInFuture() {
        final LocalDate startDate = LocalDate.now().plusDays(1);
        final LocalDate endDate = LocalDate.now().plusDays(2);

        final MetricsRequest request = new MetricsRequest();
        request.setStartDate(startDate);
        request.setEndDate(endDate);

        assertFalse(validator.isValid(request, context));
    }

    @Test
    void shouldReturnFalseWhenEndDateIsInFuture() {
        final LocalDate startDate = LocalDate.now().minusDays(1);
        final LocalDate endDate = LocalDate.now().plusDays(1);

        final MetricsRequest request = new MetricsRequest();
        request.setStartDate(startDate);
        request.setEndDate(endDate);

        assertFalse(validator.isValid(request, context));
    }

    @Test
    void shouldReturnFalseWhenBothDatesAreInFuture() {
        final LocalDate startDate = LocalDate.now().plusDays(1);
        final LocalDate endDate = LocalDate.now().plusDays(2);

        final MetricsRequest request = new MetricsRequest();
        request.setStartDate(startDate);
        request.setEndDate(endDate);

        assertFalse(validator.isValid(request, context));
    }

    @Test
    void shouldReturnTrueWhenDatesAreExactlyOneMonthOld() {
        final LocalDate startDate = LocalDate.now().minusMonths(1);
        final LocalDate endDate = LocalDate.now().minusMonths(1).plusDays(1);

        final MetricsRequest request = new MetricsRequest();
        request.setStartDate(startDate);
        request.setEndDate(endDate);

        assertTrue(validator.isValid(request, context));
    }

    @Test
    void shouldReturnFalseWhenStartDateIsExactlyOneMonthAndOneDayOld() {
        final LocalDate startDate = LocalDate.now().minusMonths(1).minusDays(1);
        final LocalDate endDate = LocalDate.now();

        final MetricsRequest request = new MetricsRequest();
        request.setStartDate(startDate);
        request.setEndDate(endDate);

        assertFalse(validator.isValid(request, context));
    }
}