package com.example.cruddata.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Account Error!!")
public class InvalidAccountException extends RuntimeException {
}
