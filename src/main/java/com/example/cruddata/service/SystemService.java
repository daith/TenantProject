package com.example.cruddata.service;

import com.example.cruddata.dto.SystemConfigDto;

import java.util.List;

public interface SystemService {

    public List<SystemConfigDto> getSystemConfigs();

}
