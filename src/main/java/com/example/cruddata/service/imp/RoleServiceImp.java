package com.example.cruddata.service.imp;

import com.example.cruddata.constant.ApiErrorCode;
import com.example.cruddata.constant.FunctionGroupType;
import com.example.cruddata.constant.FunctionType;
import com.example.cruddata.constant.SystemConsts;
import com.example.cruddata.dto.web.RoleFunctionData;
import com.example.cruddata.dto.web.RoleFunctionInputData;
import com.example.cruddata.entity.authroty.*;
import com.example.cruddata.exception.BusinessException;
import com.example.cruddata.exception.MaintainFailException;
import com.example.cruddata.repository.authroty.AccountRoleConfigRepository;
import com.example.cruddata.repository.authroty.FunctionRepository;
import com.example.cruddata.repository.authroty.RoleFunctionRepository;
import com.example.cruddata.repository.authroty.RoleRepository;
import com.example.cruddata.service.FunctionService;
import com.example.cruddata.service.RoleService;
import com.example.cruddata.service.SwaggerDocService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RoleServiceImp implements RoleService {

    private static final Logger log = LoggerFactory.getLogger(DocumentServiceImp.class);

    @Autowired
    public   RoleRepository roleRepository;
    @Autowired
    public  FunctionRepository functionRepository;
    @Autowired
    public  RoleFunctionRepository roleFunctionRepository;
    @Autowired
    public  AccountRoleConfigRepository accountRoleConfigRepository;
    @Autowired
    private  SwaggerDocService swaggerDocService;

    @Autowired
    private FunctionService functionService;




    @Override
    public Map<String,Object> saveRoleFunctions(RoleFunctionInputData roleFunctionInputData , Long tenantId, Long dataSourceId ) throws JsonProcessingException {
        List<String> tables =  roleFunctionInputData.getFunctionActions().keySet().stream().toList();
        List<Function> functions =  this.functionRepository.findByFunctionNameInAndGroupTypeAndTenantIdAndDataSourceIdOrderByDisplayOrder(tables,FunctionGroupType.DATA_SWAGGER.name() , tenantId,dataSourceId);

        Map<String,Map<String,Function>> functionActions = new HashMap<>();

        functions.forEach(functionItem -> {
            if(!functionActions.containsKey(functionItem.getFunctionName())){
                functionActions.put(functionItem.getFunctionName(),new HashMap<>());
            }
            functionActions.get(functionItem.getFunctionName()).put(functionItem.getFunctionType(),functionItem );
        });

        Map<String,String> errorMsg = new HashMap<>();

        roleFunctionInputData.getFunctionActions().forEach((table,actionList)->{

            boolean isTableNotCorrect  = !functionActions.containsKey(table);
            Map<String ,Function> functionMap = functionActions.get(table);
            actionList.replaceAll(String::toUpperCase);
            boolean isTableActionNotCorrect = !isTableNotCorrect? !functionMap.keySet().stream().toList().containsAll(actionList): !functionActions.containsKey(table);
            if(  isTableNotCorrect || isTableActionNotCorrect ) {
                String msg = isTableNotCorrect ? "table("+ table+") not open to is use" : "table("+ table+") action only open "+functionActions.get(table);
                errorMsg.put(table ,msg );
            }
        });

        if(errorMsg.size() >0){
            throw new BusinessException(ApiErrorCode.VALIDATED_ERROR,"table error" ,errorMsg);
        }

        Role role = new Role();
        if(null != roleFunctionInputData.getRoleId()){
            Optional<Role> roleInDB = this.roleRepository.findById(roleFunctionInputData.getRoleId());
            if( !roleInDB.isPresent()){
                throw new BusinessException(ApiErrorCode.AUTH_ERROR,"role id not exist ",roleFunctionInputData.getRoleId());
            }
            role = roleInDB.get();
        }

        role.setId(roleFunctionInputData.getRoleId());
        role.setTenantId(tenantId);
        role.setRoleName(roleFunctionInputData.getName());
        this.roleRepository.save(role);
        List<RoleFunction> roleFunctions =  this.roleFunctionRepository.findByRoleId(role.getId());
        if(roleFunctions.size()>0){
            this.roleFunctionRepository.deleteAll(roleFunctions);
        }

        List<RoleFunction> roleFunctionsInsert = new ArrayList<>();
        for(String tableName : roleFunctionInputData.getFunctionActions().keySet()){
            List<String> roleFunctionInsert = roleFunctionInputData.getFunctionActions().get(tableName);
            for(String functionAction:roleFunctionInsert){
                RoleFunction roleFunction = new RoleFunction();
                roleFunction.setRoleId(role.getId());
                Function functionInDb = functionActions.get(tableName).get(functionAction);
                roleFunction.setFunctionId(functionInDb.getId());
                roleFunctionsInsert.add(roleFunction);
            }
        }
        this.roleFunctionRepository.saveAll(roleFunctionsInsert);
        RoleFunctionData roleFunctionData = functionService.getRoleFunctionsByRoleId( role.getId());
        String swagger = swaggerDocService.genSwaggerDoc(Long.valueOf(role.getId()));
        Map<String , Object> result= new HashMap<>();
        result.put("swagger",swagger);
        result.put("roleFunction",roleFunctionData);
        return result;
    }





    @Override
    public void deleteRole(Long roleId) {
        List<AccountRole>  accountRoles = accountRoleConfigRepository.findByRoleId(roleId);
        if(accountRoles.size()>0)
            this.accountRoleConfigRepository.deleteAll(accountRoles);

        List<RoleFunction> roleFunctions = this.roleFunctionRepository.findByRoleId(roleId);
        if(roleFunctions.size()>0)
            this.roleFunctionRepository.deleteAll(roleFunctions);

        Role role = this.roleRepository.findById(roleId).get();
        if(null != role)
            this.roleRepository.delete(role);
    }


    @Override
    public void saveRoleAccount(Account account, Long roleId) {

        Optional<Role> role = this.roleRepository.findById(roleId);
        if(role.isPresent()){
            AccountRole accountRoleInDb = this.accountRoleConfigRepository.findByAccountId(account.getId());
            if(null == accountRoleInDb){
                AccountRole accountRole = new AccountRole();
                accountRole.setAccountId(account.getId());
                accountRole.setRoleId(roleId);
                this.accountRoleConfigRepository.save(accountRole);
            }else {
                accountRoleInDb.setRoleId(roleId);
                accountRoleInDb.setUpdateTime(new Date());
                this.accountRoleConfigRepository.save(accountRoleInDb);
            }
        }else {
            throw new BusinessException(ApiErrorCode.AUTH_ERROR,"role is not exist {}", roleId);
        }
    }

    @Override
    public void saveAdminRole(Account account) {
        if(null == account || null == account.getId() || null == account.getTenantId()){
            throw new BusinessException(ApiErrorCode.AUTH_ERROR,"account is data not correct {}");

        }
        Role role = roleRepository.findByTenantIdAndRoleName(account.getTenantId(), SystemConsts.SYSTEM_ADMIN_ROLE_NAME);

        if(null == role){
            throw new BusinessException(ApiErrorCode.AUTH_ERROR,"admin role is not exist {}");
        }

        RoleFunction roleFunction = new RoleFunction();
        roleFunction.setFunctionId(SystemConsts.SYSTEM_ADMIN);
        roleFunction.setRoleId(role.getId());
        this.roleFunctionRepository.save(roleFunction);

        AccountRole accountRole = new AccountRole();
        accountRole.setRoleId(role.getId());
        accountRole.setAccountId(account.getId());

        this.accountRoleConfigRepository.save(accountRole);

    }

}
