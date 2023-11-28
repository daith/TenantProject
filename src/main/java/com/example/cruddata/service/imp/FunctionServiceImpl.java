package com.example.cruddata.service.imp;

import com.example.cruddata.constant.ApiErrorCode;
import com.example.cruddata.constant.FunctionGroupType;
import com.example.cruddata.constant.FunctionType;
import com.example.cruddata.dto.web.RoleFunctionData;
import com.example.cruddata.entity.authroty.AccountRole;
import com.example.cruddata.entity.authroty.Function;
import com.example.cruddata.entity.authroty.Role;
import com.example.cruddata.entity.authroty.RoleFunction;
import com.example.cruddata.exception.BusinessException;
import com.example.cruddata.repository.authroty.AccountRoleConfigRepository;
import com.example.cruddata.repository.authroty.FunctionRepository;
import com.example.cruddata.repository.authroty.RoleFunctionRepository;
import com.example.cruddata.repository.authroty.RoleRepository;
import com.example.cruddata.service.FunctionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FunctionServiceImpl implements FunctionService {
    private static final Logger log = LoggerFactory.getLogger(DocumentServiceImp.class);

    @Autowired
    public FunctionRepository functionRepository;

    @Autowired
    public RoleFunctionRepository roleFunctionRepository;

    @Autowired
    public RoleRepository roleRepository;

    @Autowired
    public AccountRoleConfigRepository accountRoleConfigRepository;

    @Override
    public List<Function> getFunctionsByTenantIdAndNameAndDataSource(Long tenantId, String tableName, Long dataSourceId) {
        return  this.functionRepository.findByTenantIdAndFunctionNameAndGroupTypeAndDataSourceId(tenantId,tableName, FunctionGroupType.DATA_SWAGGER.toString() , dataSourceId);
    }

    @Override
    public void deleteFunction(String tableName, Long tenantId, Long dataSourceId) {
        List<Function> functions = this.getFunctionsByTenantIdAndNameAndDataSource( tenantId,  tableName , dataSourceId );
        if(functions.size()>0){
            this.functionRepository.deleteAll(functions );
        }else {
            log.info("{} no data delete.",tableName);
        }
        List<RoleFunction> roleFunctions = this.getRoleFunctionsByTenantIdAndNameAndDataSourceFunctions( tenantId  ,  tableName, dataSourceId , functions);
        if(roleFunctions.size()>0){
            throw new BusinessException("table role setting not clear-{}",tableName);
        }

    }

    @Override
    public void createFunction(String tableName,String description, Long tenantId, List<String> actionTypes,Long dataSourceId,Long tableId)  {

        try{
            List<Function> functions = new ArrayList<>();
            actionTypes.forEach(item->{
                Function function = new Function();
                function.setFunctionName(tableName);
                function.setDescription(description);
                function.setDataSourceId(dataSourceId);
                function.setDisplayOrder(0);
                function.setTenantId(tenantId);
                function.setTableId(tableId);
                function.setGroupType(FunctionGroupType.DATA_SWAGGER.toString());
                function.setFunctionType(String.valueOf(FunctionType.valueOf(item)));
                functions.add(function);
            });
            this.functionRepository.saveAll(functions);
        }catch (Exception exception){
            throw new BusinessException(ApiErrorCode.MAINTAIN_FAIL, "createFunction...exception For actionType {}", actionTypes);
        }

    }

    @Override
    public List<Function> getFunctionByTableName(String functionName) {
        return this.functionRepository.findByFunctionName(functionName);
    }

    @Override
    public List<RoleFunction> getRoleFunctionsByTenantIdAndNameAndDataSourceFunctions(Long tenantId, String functionName, Long dataSourceId, List<Function> functionList) {
        List<Long> functionIds = functionList.stream()
                .map(Function::getId)
                .collect(Collectors.toList());
        List<RoleFunction> roleFunctions = this.roleFunctionRepository.findByFunctionIdIn(functionIds);
        return roleFunctions;
    }

    @Override
    public List<RoleFunction> getRoleFunctionsByTenantIdAndNameAndDataSource(Long tenantId, String functionName, Long dataSourceId) {
        List<Function> functions = this.getFunctionsByTenantIdAndNameAndDataSource( tenantId,  functionName , dataSourceId );
        return getRoleFunctionsByTenantIdAndNameAndDataSourceFunctions( tenantId  ,  functionName, dataSourceId ,functions);
    }

    @Override
    public RoleFunctionData getRoleFunctionsByRoleId(Long roleId) {
        Optional<Role> role = this.roleRepository.findById(roleId);

        if(!role.isPresent()){
            throw new BusinessException(ApiErrorCode.DEFAULT_ERROR,"Role not exist!",roleId);
        }
        RoleFunctionData roleFunctionData = processSettingRoleFunctions(role.get() );

        return roleFunctionData;
    }

    private RoleFunctionData processSettingRoleFunctions(Role item) {
        List<RoleFunction> roleFunctions =this.roleFunctionRepository.findByRoleId(item.getId());
        List<Long> funstionIdList = roleFunctions.stream().map(RoleFunction::getFunctionId).collect(Collectors.toList());
        List<Function> functions = this.functionRepository.findByIdInAndGroupTypeOrderByDisplayOrder(funstionIdList, FunctionGroupType.DATA_SWAGGER.name());
        RoleFunctionData roleFunctionData = new RoleFunctionData();
        roleFunctionData.setRoleId(item.getId());
        roleFunctionData.setName(item.getRoleName());
        roleFunctionData.setFunctionActions(new HashMap<>());
        functions.forEach(function -> {
            if(!roleFunctionData.getFunctionActions().containsKey(function.getFunctionName())){
                roleFunctionData.getFunctionActions().put(function.getFunctionName(),new HashMap<>());
            }
            roleFunctionData.getFunctionActions().get(function.getFunctionName()).put(function.getFunctionType(),function);
        });

        return roleFunctionData;
    }
    @Override
    public List<RoleFunctionData> getRoleFunctionsByTenantId(Long tenantId) {
        List<Role> roles = this.roleRepository.findByTenantId(tenantId);
        List<RoleFunctionData> roleFunctionDataList = new ArrayList<>();

        if(0 !=roles.size()){
            roles.forEach(role->{
                roleFunctionDataList.add(processSettingRoleFunctions(role ));
            });
        }

        return roleFunctionDataList;
    }

    @Override
    public RoleFunctionData getRoleFunctionsByAccount(Long accountId) {
        AccountRole accountRole = this.accountRoleConfigRepository.findByAccountId(accountId);
        if(null == accountRole){
            throw new BusinessException(ApiErrorCode.AUTH_ERROR,"accountId is not set role, please contact with admin",accountId);
        }
        Role role = this.roleRepository.findById(accountRole.getRoleId()).get();
        if(null == role){
            throw new BusinessException(ApiErrorCode.AUTH_ERROR,"role is not exist, please contact with admin",accountId);
        }
        RoleFunctionData roleFunctionData = this.getRoleFunctionsByRoleId(role.getId());
        return roleFunctionData;
    }


    @Override
    public List<RoleFunctionData> getRoleFunctions() {

        List<RoleFunctionData>  roleFunctionDataList= new ArrayList<>();

        List<Role> roles = this.roleRepository.findAll();
        roles.forEach(item->{
            RoleFunctionData roleFunctionData =processSettingRoleFunctions(item);
            roleFunctionDataList.add(roleFunctionData);
        });
        return roleFunctionDataList;
    }
}
