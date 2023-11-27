package com.example.cruddata.exception;

import com.example.cruddata.constant.ApiErrorCode;
import com.example.cruddata.constant.DataSourceInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.text.MessageFormat;
import java.util.Arrays;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class BusinessException extends RuntimeException {



    private static final long serialVersionUID = 1L;

    private String code;

    private String message;

    private Object[] args;

    public BusinessException(String code,String errorMsg, Object... args) {
        super();
        this.code = code;
        this.message = errorMsg;
        this.args = args;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }


    @Override
    public String toString() {
        return getMessage();
    }


    @Override
    public String getMessage() {
        message = code +":"+message;
        if (null !=  args && args.length > 0) {
            message = MessageFormat.format("{0}", args);
        }

        return message;
    }
}
