package com.example.cruddata.service;

import com.fasterxml.jackson.core.JsonProcessingException;

public interface SwaggerDocService {
    public void genSwaggerDoc(Long RoleId) throws JsonProcessingException;
}
