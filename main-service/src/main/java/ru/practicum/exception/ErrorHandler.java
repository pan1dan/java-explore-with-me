package ru.practicum.exception;

import jakarta.validation.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleMissingParam(MissingServletRequestParameterException e) {
        List<String> errors = Arrays.stream(e.getStackTrace())
                .map(StackTraceElement::toString)
                .toList();
        return ApiError.builder()
                .errors(errors)
                .status(HttpStatus.BAD_REQUEST.toString())
                .reason("Not enough required query params")
                .message(e.getMessage())
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .build();
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, ValidationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleValidation(final MethodArgumentNotValidException e) {
        List<String> errors = e.getBindingResult().getFieldErrors().stream()
                .map(fieldError ->
                                String.format("Field: %s. Error: %s. Value: %s",
                                              fieldError.getField(),
                                              fieldError.getDefaultMessage(),
                                              fieldError.getRejectedValue() != null ?
                                                                                fieldError.getRejectedValue() : "null"))
                .toList();
        return ApiError.builder()
                       .errors(errors)
                       .status(HttpStatus.BAD_REQUEST.toString())
                       .reason("Incorrectly made request.")
                       .message(e.getMessage())
                       .timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                       .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNotFound(final NotFoundException e) {
        List<String> errors = Arrays.stream(e.getStackTrace())
                .map(StackTraceElement::toString)
                .toList();
        return ApiError.builder()
                       .errors(errors)
                       .status(HttpStatus.NOT_FOUND.toString())
                       .reason("The required object was not found.")
                       .message(e.getMessage())
                       .timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                       .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleConflict(final ConflictException e) {
        List<String> errors = Arrays.stream(e.getStackTrace())
                .map(StackTraceElement::toString)
                .toList();
        return ApiError.builder()
                       .errors(errors)
                       .status(HttpStatus.CONFLICT.toString())
                       .reason("Integrity constraint has been violated.")
                       .message(e.getMessage())
                       .timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                       .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ApiError handleForbidden(final ForbiddenException e) {
        List<String> errors = Arrays.stream(e.getStackTrace())
                .map(StackTraceElement::toString)
                .toList();
        return ApiError.builder()
                       .errors(errors)
                       .status(HttpStatus.FORBIDDEN.toString())
                       .reason("For the requested operation the conditions are not met.")
                       .message(e.getMessage())
                       .timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                       .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleBadRequest(final BadRequestException e) {
        List<String> errors = Arrays.stream(e.getStackTrace())
                .map(StackTraceElement::toString)
                .toList();
        return ApiError.builder()
                       .errors(errors)
                       .status(HttpStatus.BAD_REQUEST.toString())
                       .reason("Incorrectly made request.")
                       .message(e.getMessage())
                       .timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                       .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleException(final Exception e) {
        List<String> errors = Arrays.stream(e.getStackTrace())
                .map(StackTraceElement::toString)
                .toList();
        return ApiError.builder()
                       .errors(errors)
                       .status(HttpStatus.INTERNAL_SERVER_ERROR.toString())
                       .reason("Something went wrong")
                       .message(e.getMessage())
                       .timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                       .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleConditionsNotMetException(final ConditionsNotMetException e) {
        List<String> errors = Arrays.stream(e.getStackTrace())
                .map(StackTraceElement::toString)
                .toList();
        return ApiError.builder()
                .errors(errors)
                .status(HttpStatus.CONFLICT.toString())
                .reason("For the requested operation the conditions are not met.")
                .message(e.getMessage())
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .build();
    }
}
