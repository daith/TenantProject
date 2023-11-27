package com.example.cruddata.service;

import com.fasterxml.jackson.core.JsonProcessingException;

public interface SwaggerDocService {
    public String genSwaggerDoc(Long RoleId) throws JsonProcessingException;
}
