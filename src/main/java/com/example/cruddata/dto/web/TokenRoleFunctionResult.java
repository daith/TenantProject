package com.example.cruddata.dto.web;

import com.example.cruddata.entity.authroty.Function;
import lombok.Data;

import java.util.Map;

@Data
public class TokenRoleFunctionResult {

    boolean isCorrect;
    Map<String , Function> functionMapByToken;
}
