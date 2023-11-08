package com.example.cruddata.constant;

import org.hibernate.jdbc.Expectation;

import java.util.ArrayList;
import java.util.List;

public enum FunctionType {

    UPDATE , CREATE , DELETE, QUERY , FILE_UPLOAD;


    public static List<String> getApiCommonFuncType(){
        List<String> funType = new ArrayList<>();
        funType.add(UPDATE.name());
        funType.add(CREATE.name());
        funType.add(DELETE.name());
        funType.add(QUERY.name());
        return funType;
    }

    public static String getUrlAction(String action){

        if(action.equals(UPDATE.toString())){
            return "post";
        }else if(action.equals(CREATE.toString())){
            return "put";
        }else if(action.equals(DELETE.toString())) {
            return "delete";
        }else if(action.equals(QUERY.toString())){
            return "get";
        }else {
            return null;
        }
    }
}
