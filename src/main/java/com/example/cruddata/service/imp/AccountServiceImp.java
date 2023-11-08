package com.example.cruddata.service.imp;

import com.example.cruddata.entity.authroty.Account;
import com.example.cruddata.exception.BusinessException;
import com.example.cruddata.repository.authroty.AccountRepository;
import com.example.cruddata.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@RequiredArgsConstructor
public class AccountServiceImp implements AccountService {

    public final AccountRepository accountRepository;
    @Override
    public List<Account> getAccountsByTenant(Long tenantId) {

        return accountRepository.findByTenantId(tenantId);
    }

    @Override
    public Account createAccount(Account account) {
       Account accountInDB  = this.accountRepository.findByTenantIdAndName(account.getTenantId(),account.getName());

       if(null == accountInDB){
           this.accountRepository.save(account);
       }
        return account;
    }

    @Override
    public Account UpdateAccount(Account account) {
        Account accountInDB  = this.accountRepository.findByTenantIdAndName(account.getTenantId(),account.getName());

        if(null != accountInDB ){
            this.accountRepository.save(account);
        }

        return account;
    }

    @Override
    public Account getAccountByAccountCondition(Account account) {
        if(null == account.getTenantId() && null == account.getName() && account.getName().isEmpty()){
            throw new BusinessException("query condition not enough TenantId({}),Name({})",account.getTenantId() , account.getName() );
        }
        return  this.accountRepository.findByTenantIdAndName(account.getTenantId(),account.getName());
    }
}
