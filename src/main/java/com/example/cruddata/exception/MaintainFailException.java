package com.example.cruddata.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.EXPECTATION_FAILED, reason = "Maintain Fail!!")
public class MaintainFailException extends RuntimeException {
}
