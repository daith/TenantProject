package com.example.cruddata.dto.web;

import lombok.Data;

@Data
public class AccountInfoWithRoleData {

    Long id;

    Long tenantId;

    String name;

    String token;

    RoleFunctionData roleFunctionData;
}
