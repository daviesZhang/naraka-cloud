package com.davies.naraka.admin.controller;


import com.davies.naraka.cloud.common.domain.ErrorResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * @author davies
 * @date 2022/2/19 1:48 PM
 */
@ControllerAdvice
public class ExceptionControllerAdvice {


    /**
     * 参数验证错误
     *
     * @param e MethodArgumentNotValidException
     * @return
     */
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> methodArgumentNotValid(MethodArgumentNotValidException e) {
        ErrorResponseDTO response= new ErrorResponseDTO();
      /*  if (null != e.getFieldError()) {
            FieldError fieldError = e.getFieldError();
            String message = Strings.lenientFormat("[%s]%s", fieldError.getField(), fieldError.getDefaultMessage());
            response = new ErrorResponseDTO(message);
        } else {
            response = new ErrorResponseDTO();
        }*/
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponseDTO> methodArgumentNotValid(HttpMessageNotReadableException e) {

        return new ResponseEntity<>(new ErrorResponseDTO(), HttpStatus.BAD_REQUEST);
    }

}
