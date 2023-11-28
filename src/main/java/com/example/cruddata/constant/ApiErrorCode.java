package com.example.cruddata.constant;

import org.springframework.http.HttpStatus;

public class ApiErrorCode {

    public static final String DEFAULT_ERROR = "DEFAULT_ERROR";
    public static final String VALIDATED_ERROR = "VALIDATED_ERROR";
    public static final String SQL_ERROR = "SQL_ERROR";

    public static final String MAINTAIN_FAIL = "MAINTAIN_FAIL";
    public static final String API_RESOURCE_NOT_FOUND = "API_RESOURCE_NOT_FOUND";
    public static final String AUTH_FORBIDDEN = "FORBIDDEN";
    public static final String AUTH_ERROR = "AUTH_ERROR";


    public static String getMessage(String code) {
        return code;
    }

}
