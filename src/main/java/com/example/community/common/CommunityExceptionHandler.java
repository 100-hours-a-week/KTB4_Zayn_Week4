package com.example.community.common;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CommunityExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ResponseFormat<Void>> handleIllegalArgumentException(IllegalArgumentException e) {
        String message = e.getMessage();

        return ResponseEntity.status(getStatus(message)).body(ResponseFormat.of(message));
    }

    private HttpStatus getStatus(String message) {
        // 400
        if (message == null)
            return HttpStatus.BAD_REQUEST;

        // 403
        if (message.contains("forbidden"))
            return HttpStatus.FORBIDDEN;

        // 404
        if (message.contains("not_found"))
            return HttpStatus.NOT_FOUND;

        // 409
        if (message.contains("duplicated") || message.contains("already"))
            return HttpStatus.CONFLICT;

        return HttpStatus.BAD_REQUEST;
    }
}