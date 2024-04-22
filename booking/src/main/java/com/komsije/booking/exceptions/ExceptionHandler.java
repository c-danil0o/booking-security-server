package com.komsije.booking.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.io.IOException;

@ControllerAdvice
@Order(2)
public class ExceptionHandler extends ResponseEntityExceptionHandler {

    @AllArgsConstructor
    @Data
    public static class ApiError {

        private Integer status;
        private String message;
        private String requestedUri;

    }

    @org.springframework.web.bind.annotation.ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ResponseEntity<String> argumentTypeMismatch(MethodArgumentTypeMismatchException exception) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        return new ResponseEntity<>("Field " + exception.getName() + " is not valid!", headers, HttpStatus.BAD_REQUEST);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(ElementNotFoundException.class)
    public ResponseEntity<ApiError> resourceNotFoundException(ElementNotFoundException ex, HttpServletRequest request) {
        ApiError message = new ApiError(HttpStatus.NOT_FOUND.value(), ex.getMessage(), request.getRequestURI());
        return new ResponseEntity<ApiError>(message, HttpStatus.NOT_FOUND);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(IOException.class)
    public ResponseEntity<ApiError> dataNotFoundException(IOException ex, HttpServletRequest request) {
        ApiError message = new ApiError(HttpStatus.NOT_FOUND.value(), ex.getMessage(), request.getRequestURI());
        return new ResponseEntity<ApiError>(message, HttpStatus.NOT_FOUND);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(UploadFileException.class)
    public ResponseEntity<ApiError> uploadFileException(UploadFileException ex, HttpServletRequest request) {
        ApiError message = new ApiError(HttpStatus.I_AM_A_TEAPOT.value(), ex.getMessage(), request.getRequestURI());
        return new ResponseEntity<ApiError>(message, HttpStatus.I_AM_A_TEAPOT);
    }



    @org.springframework.web.bind.annotation.ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> illegalArgumentException(IllegalArgumentException ex, HttpServletRequest request) {
        ApiError message = new ApiError(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), request.getRequestURI());
        return new ResponseEntity<ApiError>(message, HttpStatus.BAD_REQUEST);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiError> illegalStateException(IllegalStateException ex, HttpServletRequest request) {
        ApiError message = new ApiError(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), request.getRequestURI());
        return new ResponseEntity<ApiError>(message, HttpStatus.BAD_REQUEST);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(HasActiveReservationsException.class)
    public ResponseEntity<ApiError> hasActiveReservationsException(HasActiveReservationsException ex, HttpServletRequest request) {
        ApiError message = new ApiError(HttpStatus.I_AM_A_TEAPOT.value(), ex.getMessage(), request.getRequestURI());
        return new ResponseEntity<ApiError>(message, HttpStatus.I_AM_A_TEAPOT);
    }
    @org.springframework.web.bind.annotation.ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ApiError> usernameNotFoundException(UsernameNotFoundException ex, HttpServletRequest request) {
        ApiError message = new ApiError(HttpStatus.I_AM_A_TEAPOT.value(), ex.getMessage(), request.getRequestURI());
        return new ResponseEntity<ApiError>(message, HttpStatus.I_AM_A_TEAPOT);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(AccountNotActivatedException.class)
    public ResponseEntity<ApiError> usernameNotFoundException(AccountNotActivatedException ex, HttpServletRequest request) {
        ApiError message = new ApiError(HttpStatus.I_AM_A_TEAPOT.value(), ex.getMessage(), request.getRequestURI());
        return new ResponseEntity<ApiError>(message, HttpStatus.I_AM_A_TEAPOT);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiError> authenticationException(AuthenticationException ex, HttpServletRequest request) {
        ApiError message = new ApiError(HttpStatus.I_AM_A_TEAPOT.value(), ex.getMessage(), request.getRequestURI());
        return new ResponseEntity<ApiError>(message, HttpStatus.I_AM_A_TEAPOT);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler({EmailAlreadyExistsException.class})
    public ResponseEntity<ApiError> authenticationException1(EmailAlreadyExistsException ex, HttpServletRequest request) {
        ApiError message = new ApiError(HttpStatus.I_AM_A_TEAPOT.value(), ex.getMessage(), request.getRequestURI());
        return new ResponseEntity<ApiError>(message, HttpStatus.I_AM_A_TEAPOT);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(InvalidConfirmationTokenException.class)
    public ResponseEntity<ApiError> authenticationException2(InvalidConfirmationTokenException ex, HttpServletRequest request) {
        ApiError message = new ApiError(HttpStatus.I_AM_A_TEAPOT.value(), ex.getMessage(), request.getRequestURI());
        return new ResponseEntity<ApiError>(message, HttpStatus.I_AM_A_TEAPOT);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(IncorrectPasswordException.class)
    public ResponseEntity<ApiError> authenticationException3(IncorrectPasswordException ex, HttpServletRequest request) {
        ApiError message = new ApiError(HttpStatus.I_AM_A_TEAPOT.value(), ex.getMessage(), request.getRequestURI());
        return new ResponseEntity<ApiError>(message, HttpStatus.I_AM_A_TEAPOT);
    }
    @org.springframework.web.bind.annotation.ExceptionHandler(PendingReservationException.class)
    public ResponseEntity<ApiError> cancellationException2(PendingReservationException ex, HttpServletRequest request) {
        ApiError message = new ApiError(HttpStatus.I_AM_A_TEAPOT.value(), ex.getMessage(), request.getRequestURI());
        return new ResponseEntity<ApiError>(message, HttpStatus.I_AM_A_TEAPOT);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(CancellationDeadlineExpiredException.class)
    public ResponseEntity<ApiError> cancellationException(CancellationDeadlineExpiredException ex, HttpServletRequest request) {
        ApiError message = new ApiError(HttpStatus.I_AM_A_TEAPOT.value(), ex.getMessage(), request.getRequestURI());
        return new ResponseEntity<ApiError>(message, HttpStatus.I_AM_A_TEAPOT);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(ReviewAlreadyExistsException.class)
    public ResponseEntity<ApiError> reviewException(ReviewAlreadyExistsException ex, HttpServletRequest request) {
        ApiError message = new ApiError(HttpStatus.I_AM_A_TEAPOT.value(), ex.getMessage(), request.getRequestURI());
        return new ResponseEntity<ApiError>(message, HttpStatus.I_AM_A_TEAPOT);
    }
    @org.springframework.web.bind.annotation.ExceptionHandler(ReviewNotFoundException.class)
    public ResponseEntity<ApiError> reviewException2(ReviewNotFoundException ex, HttpServletRequest request) {
        ApiError message = new ApiError(HttpStatus.I_AM_A_TEAPOT.value(), ex.getMessage(), request.getRequestURI());
        return new ResponseEntity<ApiError>(message, HttpStatus.I_AM_A_TEAPOT);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(ReviewAlreadyReportedException.class)
    public ResponseEntity<ApiError> reviewException3(ReviewAlreadyReportedException ex, HttpServletRequest request) {
        ApiError message = new ApiError(HttpStatus.I_AM_A_TEAPOT.value(), ex.getMessage(), request.getRequestURI());
        return new ResponseEntity<ApiError>(message, HttpStatus.I_AM_A_TEAPOT);
    }
    @org.springframework.web.bind.annotation.ExceptionHandler(FavoriteAlreadyExistsException.class)
    public ResponseEntity<ApiError> favoriteException3(FavoriteAlreadyExistsException ex, HttpServletRequest request) {
        ApiError message = new ApiError(HttpStatus.I_AM_A_TEAPOT.value(), ex.getMessage(), request.getRequestURI());
        return new ResponseEntity<ApiError>(message, HttpStatus.I_AM_A_TEAPOT);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(InvalidUserReportException.class)
    public ResponseEntity<ApiError> reportException(InvalidUserReportException ex, HttpServletRequest request) {
        ApiError message = new ApiError(HttpStatus.I_AM_A_TEAPOT.value(), ex.getMessage(), request.getRequestURI());
        return new ResponseEntity<ApiError>(message, HttpStatus.I_AM_A_TEAPOT);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(AccountBlockedException.class)
    public ResponseEntity<ApiError> reportException(AccountBlockedException ex, HttpServletRequest request) {
        ApiError message = new ApiError(HttpStatus.I_AM_A_TEAPOT.value(), ex.getMessage(), request.getRequestURI());
        return new ResponseEntity<ApiError>(message, HttpStatus.I_AM_A_TEAPOT);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(ReservationAlreadyExistsException.class)
    public ResponseEntity<ApiError> reportException(ReservationAlreadyExistsException ex, HttpServletRequest request) {
        ApiError message = new ApiError(HttpStatus.I_AM_A_TEAPOT.value(), ex.getMessage(), request.getRequestURI());
        return new ResponseEntity<ApiError>(message, HttpStatus.I_AM_A_TEAPOT);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(InvalidDigitalSignatureException.class)
    public ResponseEntity<ApiError> invalidDigitalSignatureException(InvalidDigitalSignatureException ex, HttpServletRequest request) {
        ApiError message = new ApiError(HttpStatus.I_AM_A_TEAPOT.value(), ex.getMessage(), request.getRequestURI());
        return new ResponseEntity<ApiError>(message, HttpStatus.I_AM_A_TEAPOT);
    }



}
