package com.example.mqtt.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import lombok.Getter;
import lombok.Setter;

@ResponseStatus(value =  HttpStatus.NOT_FOUND)
@Getter
@Setter
public class ResourceNotFoundException extends RuntimeException{
    private String resourseName;
    private String fieldName;
    private String fieldValue;
    public ResourceNotFoundException(String resourseName, String fieldName, String fieldValue) {
        super(String.format("%s not found with %s : '%s'", resourseName, fieldName, fieldValue));
        this.resourseName = resourseName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }
}
