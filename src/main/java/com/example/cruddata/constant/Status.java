package com.example.cruddata.constant;

import com.example.cruddata.exception.BusinessException;

public class Status {
    public static final String ACTIVE = "ACTIVE";
    public static final String INACTIVE = "INACTIVE";

    public static String getStatus(String value){
        if(value.equals("0") || value.toLowerCase().equals("false") ||value.toLowerCase().equals(INACTIVE)){
            return INACTIVE;
        } else if (value.equals("1") || value.toLowerCase().equals("true") ||value.toLowerCase().equals(ACTIVE)) {
            return ACTIVE;
        }else {
            throw new BusinessException(ApiErrorCode.VALIDATED_ERROR,"Status value not correct", value);
        }

    }
}
