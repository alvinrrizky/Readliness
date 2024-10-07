package com.gap.readliness.exception;

import com.gap.readliness.dto.ExceptionDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ReadlinessExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ExceptionDto> customException(CustomException e) {
        ExceptionDto exceptionDto = new ExceptionDto(e.getMessage());

        return new ResponseEntity<>(exceptionDto, HttpStatus.BAD_REQUEST);
    }

}
