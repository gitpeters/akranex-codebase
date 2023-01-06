package com.akraness.akranesswaitlist.controller.advice;

import com.akraness.akranesswaitlist.dto.Response;
import com.akraness.akranesswaitlist.exception.DuplicateException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class ControllerAdvice {
    private final MessageSource messageSource;
    @ExceptionHandler(DuplicateException.class)
    public ResponseEntity<Response> handleDuplicateException(DuplicateException e) {
        HttpStatus status = HttpStatus.CONFLICT;
        Response response = new Response(String.valueOf(status.value()),
                messageSource.getMessage("duplicate.email.message", e.getArgs(),
                        LocaleContextHolder.getLocale()), null);
        return ResponseEntity.status(status).body(response);
    }
}
