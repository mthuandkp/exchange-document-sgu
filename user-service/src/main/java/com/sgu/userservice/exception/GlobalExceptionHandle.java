package com.sgu.userservice.exception;

import com.sgu.userservice.dto.response.HttpResponseEntity;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandle extends ResponseEntityExceptionHandler {
    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        HttpResponseEntity httpResponseEntity = HttpResponseEntity.builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .message(ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(httpResponseEntity);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        List<String> stringList =  ex.getAllErrors().stream().map(err->{
            return err.getDefaultMessage();
        }).collect(Collectors.toList());
        HttpResponseEntity httpResponseEntity = HttpResponseEntity.builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .message(stringList.toString())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(httpResponseEntity);
    }

    @Override
    protected ResponseEntity<Object> handleTypeMismatch(TypeMismatchException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        HttpResponseEntity httpResponseEntity = HttpResponseEntity.builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .message(ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(httpResponseEntity);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        String err = ex.getMessage().split(":")[0];
        HttpResponseEntity httpResponseEntity = HttpResponseEntity.builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .message(ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(httpResponseEntity);
    }


    @ExceptionHandler(value = {
            IllegalArgumentException.class})
    public ResponseEntity<HttpResponseEntity> handleIllegalArgumentException(IllegalArgumentException ex) {
        String err = ex.getMessage();
        HttpResponseEntity httpResponseEntity = HttpResponseEntity.builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .message("Đối số không phù hợp cho phương thức" + ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(httpResponseEntity);

    }

    @ExceptionHandler(value = {
            BadRequestException.class})
    public ResponseEntity<HttpResponseEntity> handleBadRequestException(BadRequestException ex) {
        String err = ex.getMessage();
        HttpResponseEntity httpResponseEntity = HttpResponseEntity.builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .message(ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(httpResponseEntity);

    }

    @ExceptionHandler(value = {
            MaxUploadSizeExceededException.class})
    public ResponseEntity<HttpResponseEntity> handleBadRequestException(MaxUploadSizeExceededException ex) {
        String err = ex.getCause().getCause().getMessage();
        HttpResponseEntity httpResponseEntity = HttpResponseEntity.builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .message(ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(httpResponseEntity);
    }

    @ExceptionHandler(value = {UserNotFoundException.class})
    public ResponseEntity<HttpResponseEntity> handleNotFoundException(UserNotFoundException ex) {
        String err = ex.getMessage();
        HttpResponseEntity httpResponseEntity = HttpResponseEntity.builder()
                .code(HttpStatus.NOT_FOUND.value())
                .message(err)
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(httpResponseEntity);

    }

    @ExceptionHandler(value = {NotFoundException.class})
    public ResponseEntity<HttpResponseEntity> handleNotFoundException(NotFoundException ex) {
        HttpResponseEntity httpResponseEntity = HttpResponseEntity.builder()
                .code(HttpStatus.NOT_FOUND.value())
                .message(ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(httpResponseEntity);

    }

    @ExceptionHandler(value = {UnauthorizedException.class})
    public ResponseEntity<HttpResponseEntity> handleForbiddenException(UnauthorizedException ex) {
        HttpResponseEntity httpResponseEntity = HttpResponseEntity.builder()
                .code(HttpStatus.UNAUTHORIZED.value())
                .message(ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(httpResponseEntity);

    }

    //
    @ExceptionHandler(value = {NullPointerException.class})
    public ResponseEntity<HttpResponseEntity> handleForbiddenException(NullPointerException ex) {

        HttpResponseEntity httpResponseEntity = HttpResponseEntity.builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .message(ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(httpResponseEntity);
    }



    @ExceptionHandler(value={MultipartException.class})
    public ResponseEntity<HttpResponseEntity> handleMultipartException(MultipartException ex) {
        HttpResponseEntity httpResponseEntity = HttpResponseEntity.builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .message(ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(httpResponseEntity);
    }

    @ExceptionHandler(value={ServerInternalException.class})
    public ResponseEntity<HttpResponseEntity> handleMultipartException(ServerInternalException ex) {
        HttpResponseEntity httpResponseEntity = HttpResponseEntity.builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .message(ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(httpResponseEntity);
    }

    @ExceptionHandler(value={ConflictException.class})
    public ResponseEntity<HttpResponseEntity> handle(ConflictException ex) {
        HttpResponseEntity httpResponseEntity = HttpResponseEntity.builder()
                .code(HttpStatus.CONFLICT.value())
                .message(ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.CONFLICT).body(httpResponseEntity);
    }





}
