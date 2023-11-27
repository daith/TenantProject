package com.example.cruddata.service;

import com.example.cruddata.dto.web.AccountConditionData;
import com.example.cruddata.dto.web.RoleFunctionData;
import com.example.cruddata.dto.web.RoleFunctionInputData;
import com.example.cruddata.entity.authroty.Account;
import com.example.cruddata.entity.authroty.Function;
import com.example.cruddata.entity.authroty.Role;
import com.example.cruddata.entity.authroty.RoleFunction;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.List;
import java.util.Map;

public interface RoleService {

    public List<RoleFunctionData> getRoleFunctions();

    public List<Function>  getFunctionsByTenantIdAndNameAndDataSource(Long tenantId  , String functionName,Long dataSourceId);

    public List<RoleFunction>  getRoleFunctionsByTenantIdAndNameAndDataSourceFunctions(Long tenantId  , String functionName,Long dataSourceId , List<Function> functionList);

    public List<RoleFunction>  getRoleFunctionsByTenantIdAndNameAndDataSource(Long tenantId  , String functionName, Long dataSourceId);

    public RoleFunctionData getRoleFunctionsByRoleId(Long roleId);

    public Map<String,Object> saveRoleFunctions(RoleFunctionInputData roleFunctionData, Long tenantId  , Long dataSourceId) throws JsonProcessingException;

    public List<RoleFunctionData> getRoleFunctionsByTenantId(Long tenantId );

    public RoleFunctionData getRoleFunctionsByAccount(Long accountId );

    public void deleteFunction(String tableName , Long tenantId,Long dataSourceId);

    public void deleteRole(Long roleId);

    public void createFunction(String tableName ,String description, Long tenantId , List<String> actionTypes,Long dataSourceId,Long tableId);

    public void saveRoleAccount(Account account,Long roleId);

}
