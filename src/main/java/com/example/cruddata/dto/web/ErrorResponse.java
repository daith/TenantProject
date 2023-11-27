package com.example.cruddata.dto.web;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    private  int status;

    private  String  code;

    private String message;
    private String stackTrace;
    private List<String> errors;

    public  ErrorResponse(){
        errors  = new ArrayList<>();
    }
}
