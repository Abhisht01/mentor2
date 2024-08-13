package com.example.demo.exceptions;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.ALREADY_REPORTED)
public class CustomerAlreadyExistsException extends RuntimeException{
    public CustomerAlreadyExistsException(String message){
        super(message);
    }
}
