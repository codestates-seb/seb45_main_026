package com.server.global.exception.exceptionhandler;


import com.server.global.exception.businessexception.BusinessException;
import com.server.global.exception.businessexception.memberexception.MemberAccessDeniedException;
import com.server.global.exception.businessexception.requestexception.RequestMediaTypeMismatchException;
import com.server.global.exception.businessexception.requestexception.RequestTypeMismatchException;
import com.server.global.reponse.ApiSingleResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


import javax.validation.ConstraintViolationException;
import java.util.List;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ApiSingleResponse<List<ApiSingleResponse.ErrorResponse>>> handleBindException(
            BindException e) {

        return ResponseEntity.badRequest().body(ApiSingleResponse.fail(e));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiSingleResponse<List<ApiSingleResponse.ErrorResponse>>> handleConstraintViolationException(
            ConstraintViolationException e) {

        return ResponseEntity.badRequest().body(ApiSingleResponse.fail(e));
    }

    @ExceptionHandler(TypeMismatchException.class) //ex. url 에 int 가 들어와야하는데 string 이 들어올때
    public ResponseEntity<ApiSingleResponse<Void>> handleTypeMismatchException(
            TypeMismatchException e) {

        Object value = e.getValue();

        String stringValue = value == null ? "null" : value.toString();

        return new ResponseEntity<>(ApiSingleResponse.fail(new RequestTypeMismatchException(stringValue)), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageConversionException.class) //ex. json 요청값이 string 인데 array, object 등으로 들어올때
    public ResponseEntity<ApiSingleResponse<Void>> handleHttpMessageConversionException(
            HttpMessageConversionException e) {

        return new ResponseEntity<>(ApiSingleResponse.fail(new RequestTypeMismatchException()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class) //요청 미디어 타입이 잘못됐을 때 ex. json 요청인데 xml 로 들어올때
    public ResponseEntity<ApiSingleResponse<Void>> handleHttpMediaTypeNotSupportedException (
            HttpMediaTypeNotSupportedException e) {

        String contentType = e.getContentType().toString();

        return new ResponseEntity<>(ApiSingleResponse.fail(new RequestMediaTypeMismatchException(contentType)), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiSingleResponse<Void>> handleAccessDeniedException(AccessDeniedException e) {

        ApiSingleResponse.fail(new MemberAccessDeniedException());

        return new ResponseEntity<>(ApiSingleResponse.fail(new MemberAccessDeniedException()), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiSingleResponse<Void>> handleBusinessException(BusinessException e) {

        return new ResponseEntity<>(ApiSingleResponse.fail(e), e.getHttpStatus());
    }

    //잘못된 메서드 요청
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiSingleResponse<String>> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {

        return new ResponseEntity<>(ApiSingleResponse.fail(e), HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiSingleResponse<Void>> handleException(Exception e) {

        log.error("Unknown error {} happened: {}", e.getClass().getName(), e.getMessage());
        e.printStackTrace();

        return new ResponseEntity<>(ApiSingleResponse.fail(e), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
