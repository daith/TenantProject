package com.example.cruddata.dto.web;

import lombok.Data;

@Data
public class AccountData {

    Long id;

    Long tenantId;

    String name;

    String password;
}
