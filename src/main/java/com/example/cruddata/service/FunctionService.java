package com.example.cruddata.service;

import com.example.cruddata.dto.web.RoleFunctionData;
import com.example.cruddata.entity.authroty.Function;
import com.example.cruddata.entity.authroty.RoleFunction;
import com.example.cruddata.entity.authroty.Tenant;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.List;

public interface FunctionService {

    public List<Function>  getFunctionsByTenantIdAndNameAndDataSource(Long tenantId  , String functionName, Long dataSourceId);

    public void createFunction(String tableName ,String description, Long tenantId , List<String> actionTypes,Long dataSourceId,Long tableId);

    public List<Function>  getFunctionByTableName(String functionName);

    public List<RoleFunction>  getRoleFunctionsByTenantIdAndNameAndDataSourceFunctions(Long tenantId  , String functionName, Long dataSourceId , List<Function> functionList);

    public List<RoleFunction>  getRoleFunctionsByTenantIdAndNameAndDataSource(Long tenantId  , String functionName, Long dataSourceId);

    public RoleFunctionData getRoleFunctionsByRoleId(Long roleId);

    public List<RoleFunctionData> getRoleFunctionsByTenantId(Long tenantId );

    public RoleFunctionData getRoleFunctionsByAccount(Long accountId );
    public void deleteFunction(String tableName , Long tenantId,Long dataSourceId);

    public List<RoleFunctionData> getRoleFunctions();


}
