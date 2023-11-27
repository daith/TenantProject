package com.example.cruddata.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "The given Tenant are incorrect!")
public class TenantNotFoundException extends RuntimeException  {
}
