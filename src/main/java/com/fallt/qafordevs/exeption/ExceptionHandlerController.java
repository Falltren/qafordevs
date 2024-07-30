package com.fallt.qafordevs.exeption;

import com.fallt.qafordevs.dto.ErrorDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionHandlerController {

    @ExceptionHandler(DeveloperWithDuplicateEmailException.class)
    public ResponseEntity<ErrorDto> handleDuplicateEmailException(Exception e) {
        ErrorDto errorDto = ErrorDto.builder()
                .status(400)
                .message(e.getMessage())
                .build();
        return new ResponseEntity<>(errorDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DeveloperNotFoundException.class)
    public ResponseEntity<ErrorDto> handleDeveloperNotFoundException(Exception e) {
        ErrorDto errorDto = ErrorDto.builder()
                .status(404)
                .message(e.getMessage())
                .build();
        return new ResponseEntity<>(errorDto, HttpStatus.NOT_FOUND);
    }
}
