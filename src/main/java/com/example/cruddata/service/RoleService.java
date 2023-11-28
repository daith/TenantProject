package com.example.cruddata.service;

import com.example.cruddata.dto.web.RoleFunctionData;
import com.example.cruddata.dto.web.RoleFunctionInputData;
import com.example.cruddata.entity.authroty.Account;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.List;
import java.util.Map;

public interface RoleService {


    public Map<String,Object> saveRoleFunctions(RoleFunctionInputData roleFunctionData, Long tenantId  , Long dataSourceId) throws JsonProcessingException;

    public void deleteRole(Long roleId);


    public void saveRoleAccount(Account account,Long roleId);

    public void saveAdminRole(Account account);

}
