package com.example.cruddata.service;

import com.example.cruddata.dto.web.AccountConditionData;
import com.example.cruddata.dto.web.AccountData;
import com.example.cruddata.dto.web.TokenRoleFunctionResult;
import com.example.cruddata.entity.authroty.Account;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.List;

public interface AccountService {

    public List<Account> getAccountsByTenant(Long tenantId);

    public Account createAccount(Account account);

    public Account UpdateAccount(Account account);

    public Account getAccountByAccountCondition(AccountConditionData account);

    public String getAccountLoginData(AccountData account) throws JsonProcessingException;

    public Boolean tokenValidationNotExist(String token) throws JsonProcessingException;

    public TokenRoleFunctionResult tokenRoleFunctionValidation(String token, String table) throws JsonProcessingException;

}
