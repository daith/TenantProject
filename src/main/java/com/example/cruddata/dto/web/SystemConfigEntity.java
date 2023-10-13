package com.example.cruddata.dto.web;

import lombok.Data;

@Data
public class SystemConfigEntity {


    public Long id;
    public String name;
    public Boolean isDeleted;
    public Boolean isDefault;
}
