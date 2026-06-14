package com.assignment.weather.exception;

import com.assignment.weather.model.MetricType;
import com.assignment.weather.model.StatisticType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Global exception handler
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationExceptions(final MethodArgumentNotValidException ex) {
        final BindingResult bindingResult = ex.getBindingResult();
        
        final List<String> errors = bindingResult.getAllErrors().stream()
                .map(error -> {
                    if (error instanceof FieldError fieldError) {
                        return fieldError.getField() + ": " + fieldError.getDefaultMessage();
                    }
                    return error.getDefaultMessage();
                })
                .collect(Collectors.toList());

        final String defaultMessage = errors.isEmpty() 
                ? "Validation failed" 
                : errors.getFirst();

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST, 
                defaultMessage);
        
        problemDetail.setTitle("Bad Request");
        problemDetail.setProperty("errors", errors);
        
        return problemDetail;
    }

    /**
     * Handle exception caused due to payload conversion.
     * @param ex exception
     * @return @ProblemDetail
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ProblemDetail handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        String detail = ex.getMessage();
        String errorType = "Invalid enum value";

        if (detail != null) {
            if (detail.contains("MetricsType")) {
                detail = "Invalid MetricsType value. "
                        + "Valid values are: " + Arrays.toString(MetricType.values());
                errorType = "Invalid MetricsType";
            } else if (detail.contains("StatisticType")) {
                detail = "Invalid StatisticType value. "
                        + "Valid values are: " + Arrays.toString(StatisticType.values());
                errorType = "Invalid StatisticType";
            } else {
                detail = "Invalid request format: " + detail;
            }
        } else {
            detail = "Invalid request format";
        }

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST, 
                detail);
        
        problemDetail.setTitle("Bad Request");
        problemDetail.setProperty("error", errorType);
        
        return problemDetail;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleHttpMessageNotReadable(Exception ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                ex.getMessage());

        problemDetail.setTitle("Unknown Error");
        problemDetail.setProperty("error", "Unknown Error");

        return problemDetail;
    }
}