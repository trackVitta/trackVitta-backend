package com.trackNest.trackNest_App.common.globalExceptionHandler;

import com.trackNest.trackNest_App.common.globalExceptionHandler.exceptions.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private ResponseEntity<ErrorResponse> build(HttpStatus status, String message) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .message(message)
                .status(status.value())
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(status).body(errorResponse);
    }

    @ExceptionHandler(BusinessNotFound.class)
    public ResponseEntity<ErrorResponse> handleBusinessNotFoundException(BusinessNotFound beNf) {
        return build(HttpStatus.NOT_FOUND, beNf.getMessage());
    }

    @ExceptionHandler(MemberNotFound.class)
    public ResponseEntity<ErrorResponse> handleMemberNotFound(MemberNotFound mNf) {
        return build(HttpStatus.NOT_FOUND, mNf.getMessage());
    }
    @ExceptionHandler(DuplicateMemberException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateMember(DuplicateMemberException ex) {
        return build(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(InternalServerException.class)
    public ResponseEntity<ErrorResponse> handleInternalError(InternalServerException ise) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR, ise.getMessage());
    }

    // A bad upload is the client's mistake (400), not a missing resource (404).
    @ExceptionHandler(InvalidFileException.class)
    public ResponseEntity<ErrorResponse> handleInvalidFile(InvalidFileException ex) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(ImportMappingException.class)
    public ResponseEntity<ErrorResponse> handleImportMapping(ImportMappingException ex) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorResponse> handleTooLarge(MaxUploadSizeExceededException ex) {
        return build(HttpStatus.CONTENT_TOO_LARGE, "File size cannot exceed 5 MB");
    }

    // Without this, a request that is not multipart (for example a frontend sending the file as JSON)
    // ends as a 500 and a stack trace. Now the client gets a 400 that says what the server really received.
    @ExceptionHandler(MultipartException.class)
    public ResponseEntity<ErrorResponse> handleNotMultipart(MultipartException ex, HttpServletRequest request) {
        return build(HttpStatus.BAD_REQUEST, "The upload was not sent as multipart/form-data (the server received: "
                + request.getContentType() + ")");
    }
}