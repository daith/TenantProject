package com.example.cruddata.dto.web;

import com.example.cruddata.entity.authroty.Function;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class RoleFunctionInputData {

    public Long roleId;

    public String name;

    public Map<String, List<String>> functionActions;
}
