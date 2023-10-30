package com.example.cruddata.dto.web;

import lombok.Data;

@Data
public class SystemConfigEntityData {


    public Long id;
    public String name;
    public Boolean isDeleted;
    public Boolean isDefault;
}
