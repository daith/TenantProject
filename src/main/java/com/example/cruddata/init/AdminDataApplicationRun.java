package com.example.cruddata.init;

import com.example.cruddata.constant.SystemConsts;
import com.example.cruddata.dto.web.AccountConditionData;
import com.example.cruddata.dto.web.AccountData;
import com.example.cruddata.entity.authroty.Account;
import com.example.cruddata.entity.authroty.Tenant;
import com.example.cruddata.service.AccountService;
import com.example.cruddata.service.RoleService;
import com.example.cruddata.service.TenantService;
import com.example.cruddata.util.EncryptUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(1)
public class AdminDataApplicationRun implements ApplicationRunner {
    private static final Logger log = LoggerFactory.getLogger(AdminDataApplicationRun.class);
    @Autowired
    AccountService accountService;
    @Autowired
    TenantService tenantService;

    @Autowired
    RoleService roleService;
    public void run(ApplicationArguments args) throws Exception {

        Tenant admintTenant = tenantService.getTenantByName(SystemConsts.SYSTEM_ADMIN_TENANT_NAME);

        AccountConditionData accountConditionData = new AccountConditionData();
        accountConditionData.setName(SystemConsts.SYSTEM_ADMIN_NAME);
        accountConditionData.setTenantId(admintTenant.getId());
        Account account = accountService.getAccountByAccountCondition(accountConditionData);

        if(null == account){
            account = new Account();
            account.setName(SystemConsts.SYSTEM_ADMIN_NAME);
            account.setPassword(EncryptUtil.getMD5(SystemConsts.SYSTEM_ADMIN_PASSWORD));
            account.setTenantId(admintTenant.getId());
            accountService.createAccount(account);
            roleService.saveAdminRole(account);
        }

        AccountData accountData = new AccountData();
        accountData.setTenantId(admintTenant.getId());
        accountData.setName(SystemConsts.SYSTEM_ADMIN_NAME);
        accountData.setPassword(SystemConsts.SYSTEM_ADMIN_PASSWORD);
        String token =  this.accountService.getAccountLoginData(accountData);

       log.info("admin account token is::{}" , token);

    }
}
