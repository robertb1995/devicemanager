package com.bialas.robert.devicemanager.contoller.handlers;

import com.bialas.robert.devicemanager.service.exception.DeviceAlreadyExistentException;
import com.bialas.robert.devicemanager.service.exception.DeviceNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
@AllArgsConstructor
public class ErrorHandler {

    @ExceptionHandler(DeviceAlreadyExistentException.class)
    @ResponseBody
    public ResponseEntity<String> handleDeviceAlreadyPresentException(DeviceAlreadyExistentException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(DeviceNotFoundException.class)
    @ResponseBody
    public ResponseEntity<String> handleEntityNotFoundForIdException(DeviceNotFoundException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.NOT_FOUND);
    }

}
