package com.example.cruddata.dto;

import lombok.Data;

@Data
public class SystemConfigDto {


    public Long id;
    public String name;
    public Boolean isDeleted;
    public Boolean isDefault;
}
