package ru.develop.bank.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler({AlreadyExistsException.class})
    @ResponseStatus(HttpStatus.ALREADY_REPORTED)
    public ErrorResponse handleAlreadyExists(final RuntimeException exception) {
        return new ErrorResponse(exception.getMessage());
    }
}
