package com.example.cruddata.controller;

import com.example.cruddata.constant.Status;
import com.example.cruddata.dto.business.TenantData;
import com.example.cruddata.dto.web.AccountData;
import com.example.cruddata.entity.authroty.Account;
import com.example.cruddata.entity.authroty.Tenant;
import com.example.cruddata.service.AccountService;
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

@Api(tags ="1_組織資料 - 新增,修改,刪除")
@RestController
@RequestMapping("/api/auth/org")
@RequiredArgsConstructor
public class OrgController {


    private static final Logger log = LoggerFactory.getLogger(OrgController.class);

    private final AccountService accountService;
    private final TenantService tenantService;
    @GetMapping(value="/tenants")
    public ResponseEntity<?> getAllTenantByStatus(@RequestParam(name = "idDelete" ,required = false)String idDelete , @RequestParam(name = "status",required = false) String status) {
        return ResponseEntity.ok(tenantService.getAllTenantByStatus(null == idDelete ? null:Boolean.valueOf(idDelete), Status.getStatus(status)));
    }

    @GetMapping(value="/tenant/{tenantId}")
    public ResponseEntity<?> getTenantById(@PathVariable(name = "tenantId" )String tenantId) {

        Tenant tenant =  tenantService.getTenantById( Long.valueOf(tenantId));
        log.info("[i] Loaded DataSource for tenant '{}'.", tenantId);

        return ResponseEntity.ok(tenant);

    }

    @PostMapping(value="/tenant")
    public ResponseEntity<?> addTenant(@RequestBody TenantData tenantData) throws JsonProcessingException {
        Tenant tenant =new Tenant();
        tenant.setCompanyName(tenantData.getCompanyName());
        tenantService.addTenant( tenant);
        return ResponseEntity.ok( tenant);
    }

    @GetMapping(value="/account/tenant/{tenantId}")
    public ResponseEntity<?> getAccountsByTenant(@PathVariable(name = "tenantId")String tenantId) {
        List<Account> accounts =  accountService.getAccountsByTenant( Long.valueOf(tenantId));
        log.info("[i] Loaded DataSource for tenantId '{}'.", tenantId);

        return ResponseEntity.ok(accounts);
    }

    @PostMapping(value="/account/tenant/{tenantId}")
    public ResponseEntity<?> addAccount(@PathVariable(name = "tenantId")String tenantId, @RequestBody AccountData accountData) {
        Account account = new Account();
        account.setName(accountData.getName());
        account.setPassword(EncryptUtil.getMD5(accountData.getPassword()));
        account.setTenantId(Long.valueOf(tenantId));
        accountService.createAccount(account);
        log.info("[i] Loaded DataSource for tenantId '{}'.", tenantId);

        return ResponseEntity.ok(account);
    }

}
