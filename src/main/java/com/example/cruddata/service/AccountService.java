package com.example.cruddata.service;

import com.example.cruddata.entity.authroty.Account;

import java.util.List;

public interface AccountService {

    public List<Account> getAccountsByTenant(Long tenantId);

    public Account createAccount(Account account);

    public Account UpdateAccount(Account account);

    public Account getAccountByAccountCondition(Account account);
}
