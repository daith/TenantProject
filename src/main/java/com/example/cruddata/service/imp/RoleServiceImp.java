package com.example.cruddata.service.imp;

import com.example.cruddata.constant.FunctionGroupType;
import com.example.cruddata.constant.FunctionType;
import com.example.cruddata.dto.web.RoleFunctionData;
import com.example.cruddata.dto.web.RoleFunctionInputData;
import com.example.cruddata.entity.authroty.*;
import com.example.cruddata.exception.BusinessException;
import com.example.cruddata.repository.authroty.AccountRoleConfigRepository;
import com.example.cruddata.repository.authroty.FunctionRepository;
import com.example.cruddata.repository.authroty.RoleFunctionRepository;
import com.example.cruddata.repository.authroty.RoleRepository;
import com.example.cruddata.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleServiceImp implements RoleService {

    private static final Logger log = LoggerFactory.getLogger(DocumentServiceImp.class);


    public  final RoleRepository roleRepository;

    public final FunctionRepository functionRepository;

    public final RoleFunctionRepository roleFunctionRepository;

    public final AccountRoleConfigRepository accountRoleConfigRepository;



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

    @Override
    public List<Function> getFunctionsByTenantIdAndNameAndDataSource(Long tenantId, String tableName ,Long dataSourceId ) {
        return  this.functionRepository.findByTenantIdAndFunctionNameAndGroupTypeAndDataSourceId(tenantId,tableName,FunctionGroupType.DATA_SWAGGER.toString() , dataSourceId);
    }
    @Override
    public List<RoleFunction>  getRoleFunctionsByTenantIdAndNameAndDataSource(Long tenantId  , String functionName,Long dataSourceId){

        List<Function> functions = this.getFunctionsByTenantIdAndNameAndDataSource( tenantId,  functionName , dataSourceId );
        return getRoleFunctionsByTenantIdAndNameAndDataSourceFunctions( tenantId  ,  functionName, dataSourceId ,functions);
    }

    public List<RoleFunction>  getRoleFunctionsByTenantIdAndNameAndDataSourceFunctions(Long tenantId  , String functionName,Long dataSourceId , List<Function> functions){

        List<Long> functionIds = functions.stream()
                .map(Function::getId)
                .collect(Collectors.toList());
        List<RoleFunction> roleFunctions = this.roleFunctionRepository.findByFunctionIdIn(functionIds);
        return roleFunctions;
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
    public RoleFunctionData getRoleFunctionsByRoleId(Long roleId) {

        Role role = this.roleRepository.findById(roleId).get();
        RoleFunctionData roleFunctionData = null;

        if(null !=role){
            roleFunctionData = processSettingRoleFunctions(role );
        }

        return roleFunctionData;
    }

    @Override
    public RoleFunctionData saveRoleFunctions(RoleFunctionInputData roleFunctionInputData , Long tenantId, Long dataSourceId ) {
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
            boolean isTableActionNotCorrect = !isTableNotCorrect? !functionMap.keySet().stream().toList().containsAll(actionList): !functionActions.containsKey(table);
            if(  isTableNotCorrect || isTableActionNotCorrect ) {
                String msg = isTableNotCorrect ? "table("+ table+") not open to is use" : "table("+ table+") action only open "+functionActions.get(table);
                errorMsg.put(table ,msg );
            }
        });

        if(errorMsg.size() >0){
            throw new BusinessException("saveRoleFunctions error happened:: ",errorMsg);
        }

        Role role = new Role();
        if(null != roleFunctionInputData.getRoleId()){
            Optional<Role> roleInDB = this.roleRepository.findById(roleFunctionInputData.getRoleId());
            if( !roleInDB.isPresent()){
                throw new BusinessException("role id not exist ",roleFunctionInputData.getRoleId());
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

        return getRoleFunctionsByRoleId( role.getId());
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
            throw new BusinessException("accountId is not set role, please contact with admin");
        }
        Role role = this.roleRepository.findById(accountRole.getRoleId()).get();
        if(null == role){
            throw new BusinessException("role is not exist, please contact with admin");
        }
        RoleFunctionData roleFunctionData = this.getRoleFunctionsByRoleId(role.getId());
        return roleFunctionData;
    }

    @Override
    public void deleteFunction(String tableName, Long tenantId,Long dataSourceId) {
        List<Function> functions = this.getFunctionsByTenantIdAndNameAndDataSource( tenantId,  tableName , dataSourceId );
        if(functions.size()>0){
            this.functionRepository.deleteAll(functions );
        }else {
            log.info("{} no data delete.",tableName);
        }
        List<RoleFunction> roleFunctions = getRoleFunctionsByTenantIdAndNameAndDataSourceFunctions( tenantId  ,  tableName, dataSourceId , functions);
       if(roleFunctions.size()>0){
           throw new BusinessException("table role setting not clear-{}",tableName);
       }



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
            throw new BusinessException("createFunction...exception For actionType {}", actionTypes);
        }

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
            throw new BusinessException("role is not exist {}", roleId);
        }
    }

}
