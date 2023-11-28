package com.example.cruddata.controller;

import com.example.cruddata.constant.ApiErrorCode;
import com.example.cruddata.dto.web.AccountConditionData;
import com.example.cruddata.dto.web.AccountData;
import com.example.cruddata.dto.web.RoleFunctionData;
import com.example.cruddata.dto.web.RoleFunctionInputData;
import com.example.cruddata.entity.authroty.Account;
import com.example.cruddata.exception.BusinessException;
import com.example.cruddata.service.AccountService;
import com.example.cruddata.service.FunctionService;
import com.example.cruddata.service.RoleService;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Api(tags ="帳號權限 - 新增,修改,刪除")
@RestController
@RequestMapping("/api/auth/data")
@RequiredArgsConstructor
public class AuthrotyController {

    private static final Logger log = LoggerFactory.getLogger(AuthrotyController.class);

    private final AccountService accountService;

    private final RoleService roleService;

    private final FunctionService functionService;


    @GetMapping(value="/rolesFunctions")
    public ResponseEntity<?> getRolesFunctions() {
        return ResponseEntity.ok(this.functionService.getRoleFunctions());
    }

    @GetMapping(value="/rolesFunction/{roleId}")
    public ResponseEntity<?> getRolesFunction(@PathVariable(name = "roleId")String roleId) {
        return ResponseEntity.ok(this.functionService.getRoleFunctionsByRoleId(Long.valueOf(roleId)));
    }

    @PostMapping(value="/rolesFunction/{tenantId}/datasource/{datasourceId}")
    public ResponseEntity<?> createRolesFunction(@PathVariable(name = "datasourceId")String datasourceId,@PathVariable(name = "tenantId")String tenantId,@RequestBody RoleFunctionInputData roleFunctionData) throws JsonProcessingException {
        return ResponseEntity.ok(this.roleService.saveRoleFunctions(roleFunctionData,Long.valueOf(tenantId),Long.valueOf(datasourceId)));
    }

    @DeleteMapping(value="/role/{roleId}")
    public ResponseEntity<?> createRolesFunction(@PathVariable(name = "roleId")String roleId) {
        this.roleService.deleteRole( Long.valueOf(roleId));
        return ResponseEntity.ok(roleId);
    }

    @PostMapping(value="/tenant/{tenantId}/loginAccount")
    public ResponseEntity<?> loginAccount(@PathVariable(name = "tenantId")String tenantId,@RequestBody AccountData accountData) throws JsonProcessingException {

            accountData.setTenantId(Long.valueOf(tenantId));
            return ResponseEntity.ok(this.accountService.getAccountLoginData(accountData));

    }

    @PostMapping(value="/tenant/{tenantId}/accountRole")
    public ResponseEntity<?> accountRoleMaintain(@PathVariable(name = "tenantId")String tenantId,@RequestParam("accountName") String accountName,@RequestParam("roleId") String roleId) {
        AccountConditionData account = new AccountConditionData();
        account.setTenantId(Long.valueOf(tenantId));
        account.setName(accountName);
        Account accountInDB = this.accountService.getAccountByAccountCondition(account);
        if(null==accountInDB){
            throw new BusinessException(ApiErrorCode.AUTH_ERROR,"account no exist {}",account);
        }

        this.roleService.saveRoleAccount(accountInDB , Long.valueOf(roleId));
        RoleFunctionData roleFunctionData =this.functionService.getRoleFunctionsByAccount(accountInDB.getId());

        return ResponseEntity.ok(roleFunctionData);
    }
}
