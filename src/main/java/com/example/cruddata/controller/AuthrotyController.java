package com.example.cruddata.controller;

import com.example.cruddata.dto.business.TenantData;
import com.example.cruddata.dto.web.AccountConditionData;
import com.example.cruddata.dto.web.AccountData;
import com.example.cruddata.dto.web.RoleFunctionData;
import com.example.cruddata.dto.web.RoleFunctionInputData;
import com.example.cruddata.entity.authroty.Account;
import com.example.cruddata.entity.authroty.Tenant;
import com.example.cruddata.exception.BusinessException;
import com.example.cruddata.service.AccountService;
import com.example.cruddata.service.RoleService;
import com.example.cruddata.service.TenantService;
import com.example.cruddata.util.EncryptUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags ="帳號權限 - 新增,修改,刪除")
@RestController
@RequestMapping("/api/auth/data")
@RequiredArgsConstructor
public class AuthrotyController {

    private static final Logger log = LoggerFactory.getLogger(AuthrotyController.class);
    private final TenantService tenantService;
    private final AccountService accountService;

    private final RoleService roleService;



    @GetMapping(value="/tenants")
    public ResponseEntity<?> getAllTenantByStatus(@RequestParam(name = "idDelete" ,required = false)String idDelete , @RequestParam(name = "status",required = false) String status) {
        return ResponseEntity.ok(tenantService.getAllTenantByStatus(null == idDelete ? null:Boolean.valueOf(idDelete),status));
    }

    @GetMapping(value="/tenant/{tenantId}")
    public ResponseEntity<?> getTenantById(@PathVariable(name = "tenantId" ,required = true)String tenantId) {

        try {
            Tenant tenant =  tenantService.getTenantById( Long.valueOf(tenantId));
            log.info("[i] Loaded DataSource for tenant '{}'.", tenantId);


            return ResponseEntity.ok(tenant);
        } catch (Exception e) {
            return ResponseEntity.ok(e);
        }

    }

    @PostMapping(value="/tenant")
    public ResponseEntity<?> addTenant(@RequestBody TenantData tenantData) {
        Tenant tenant =new Tenant();
        tenant.setCompanyName(tenantData.getCompanyName());
        try {
            tenantService.addTenant( tenant);
        } catch (JsonProcessingException e) {
            return ResponseEntity.ok( e);
        }
        return ResponseEntity.ok( tenant);
    }

    @GetMapping(value="/account/tenant/{tenantId}")
    public ResponseEntity<?> getAccountsByTenant(@PathVariable(name = "tenantId" ,required = true)String tenantId) {
        try {
            List<Account> accounts =  accountService.getAccountsByTenant( Long.valueOf(tenantId));
            log.info("[i] Loaded DataSource for tenantId '{}'.", tenantId);

            return ResponseEntity.ok(accounts);
        } catch (Exception e) {
            return ResponseEntity.ok(e);
        }
    }

    @PostMapping(value="/account/tenant/{tenantId}")
    public ResponseEntity<?> addAccount(@PathVariable(name = "tenantId" ,required = true)String tenantId, @RequestBody AccountData accountData) {
        try {

            Account account = new Account();
            account.setName(accountData.getName());
            account.setPassword(EncryptUtil.getMD5(accountData.getPassword()));
            account.setTenantId(Long.valueOf(tenantId));
            accountService.createAccount(account);
            log.info("[i] Loaded DataSource for tenantId '{}'.", tenantId);

            return ResponseEntity.ok(account);
        } catch (Exception e) {
            return ResponseEntity.ok(e);
        }
    }


    @GetMapping(value="/rolesFunctions")
    public ResponseEntity<?> getRolesFunctions() {
        try {
            return ResponseEntity.ok(this.roleService.getRoleFunctions());
        } catch (Exception e) {
            return ResponseEntity.ok(e);
        }
    }

    @GetMapping(value="/rolesFunction/{roleId}")
    public ResponseEntity<?> getRolesFunction(@PathVariable(name = "roleId" ,required = true)String roleId) {
        try {
            return ResponseEntity.ok(this.roleService.getRoleFunctionsByRoleId(Long.valueOf(roleId)));
        } catch (Exception e) {
            return ResponseEntity.ok(e);
        }
    }

    @PostMapping(value="/rolesFunction/{tenantId}/datasource/{datasourceId}")
    public ResponseEntity<?> createRolesFunction(@PathVariable(name = "datasourceId" ,required = true)String datasourceId,@PathVariable(name = "tenantId" ,required = true)String tenantId,@RequestBody RoleFunctionInputData roleFunctionData) {
        try {
            return ResponseEntity.ok(this.roleService.saveRoleFunctions(roleFunctionData,Long.valueOf(tenantId),Long.valueOf(datasourceId)));
        } catch (Exception e) {
            return ResponseEntity.ok(e);
        }
    }

    @DeleteMapping(value="/role/{roleId}")
    public ResponseEntity<?> createRolesFunction(@PathVariable(name = "roleId" ,required = true)String roleId) {
        try {
            this.roleService.deleteRole( Long.valueOf(roleId));
            return ResponseEntity.ok(roleId);
        } catch (Exception e) {
            return ResponseEntity.ok(e);
        }
    }

    @PostMapping(value="/tenant/{tenantId}/loginAccount")
    public ResponseEntity<?> loginAccount(@PathVariable(name = "tenantId" ,required = true)String tenantId,@RequestBody AccountData accountData) {
        try {
            accountData.setTenantId(Long.valueOf(tenantId));
            return ResponseEntity.ok(this.accountService.getAccountLoginData(accountData));
        } catch (Exception e) {
            return ResponseEntity.ok(e);
        }
    }

    @PostMapping(value="/tenant/{tenantId}/accountRole")
    public ResponseEntity<?> accountRoleMaintain(@PathVariable(name = "tenantId" ,required = true)String tenantId,@RequestParam("accountName") String accountName,@RequestParam("roleId") String roleId) {
        try {
                AccountConditionData account = new AccountConditionData();
                account.setTenantId(Long.valueOf(tenantId));
                account.setName(accountName);
                Account accountInDB = this.accountService.getAccountByAccountCondition(account);
                if(null==accountInDB){
                    throw new BusinessException("account no exist {}",account);
                }

                this.roleService.saveRoleAccount(accountInDB , Long.valueOf(roleId));
                RoleFunctionData roleFunctionData =this.roleService.getRoleFunctionsByAccount(accountInDB.getId());

            return ResponseEntity.ok(roleFunctionData);
        } catch (Exception e) {
            return ResponseEntity.ok(e);
        }
    }
}
