package com.example.cruddata.service.imp;

import com.example.cruddata.constant.ApiErrorCode;
import com.example.cruddata.constant.SystemConsts;
import com.example.cruddata.dto.web.AccountConditionData;
import com.example.cruddata.dto.web.AccountData;
import com.example.cruddata.dto.web.RoleFunctionData;
import com.example.cruddata.dto.web.TokenRoleFunctionResult;
import com.example.cruddata.entity.authroty.Account;
import com.example.cruddata.entity.authroty.Function;
import com.example.cruddata.exception.BusinessException;
import com.example.cruddata.repository.authroty.AccountRepository;
import com.example.cruddata.service.AccountService;
import com.example.cruddata.service.FunctionService;
import com.example.cruddata.service.TenantService;
import com.example.cruddata.util.EncryptUtil;
import com.example.cruddata.util.RedisUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AccountServiceImp implements AccountService {
    @Autowired
    public  AccountRepository accountRepository;
    @Autowired
    public  TenantService tenantService;
    @Autowired
    private FunctionService functionService;
    @Autowired
    private  RedisUtil redisUtil;

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
    public Account getAccountByAccountCondition(AccountConditionData account) {

        if(null == account.getTenantId() && null == account.getName() && account.getName().isEmpty()){
            throw new BusinessException(ApiErrorCode.VALIDATED_ERROR,"query condition not enough TenantId({}),Name({})",account.getTenantId() , account.getName() );
        }

        tenantService.validatedTenantProcess(account.getTenantId());

        return  this.accountRepository.findByTenantIdAndName(account.getTenantId(),account.getName());
    }

    @Override
    public String getAccountLoginData(AccountData accountData) throws JsonProcessingException {

        AccountConditionData account =new AccountConditionData();
        account.setName(accountData.getName());
        account.setTenantId(accountData.getTenantId());
        Account accountInDB = this.getAccountByAccountCondition(account);
        if(null!=accountInDB && EncryptUtil.getMD5(accountData.getPassword()).equals(accountInDB.getPassword())){
            RoleFunctionData roleFunctionData =  this.functionService.getRoleFunctionsByAccount( accountInDB.getId());
            StringBuilder tokeData= new StringBuilder();
            tokeData.append("accountId-").append(accountInDB.getId()).append("roleId-").append(roleFunctionData.getRoleId());
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.MONTH, 1);
            long dateTime = cal.getTimeInMillis();

            Map<String ,String > genTokenParameters = new HashMap<>();
            genTokenParameters.put(EncryptUtil.DATA_VALUE,tokeData.toString());
            genTokenParameters.put(EncryptUtil.DATA_DATE,String.valueOf(dateTime));
            genTokenParameters.put(EncryptUtil.DATA_SOAP,EncryptUtil.DATA_SOAP_VALUE);
            String token = EncryptUtil.genToken(genTokenParameters);
            accountInDB.setToken(token);
            accountInDB.setTokenRenewTime(new Date());

            this.UpdateAccount(accountInDB);

            if (redisUtil.getHashEntries(token).size() == 0) {
                redisUtil.add(token, new HashMap<>());
                redisUtil.expire(token ,dateTime );
            }

            redisUtil.add(token, "roleFunction", roleFunctionData);
            return token;
        }else {
            throw new BusinessException(ApiErrorCode.AUTH_ERROR,"account not exist", accountData);
        }

    }

    @Override
    public Boolean tokenValidationNotExist(String token) throws JsonProcessingException {
        return redisUtil.getHashEntries(token).size() == 0;
    }

    @Override
    public TokenRoleFunctionResult tokenRoleFunctionValidation(String token, String tableName) throws JsonProcessingException {

        TokenRoleFunctionResult tokenRoleFunctionResult = new TokenRoleFunctionResult();
        if (null == redisUtil.getHashEntries(token)) {
            throw new BusinessException(ApiErrorCode.AUTH_ERROR,"token is validator correct.");
        }

        RoleFunctionData roleFunction = (RoleFunctionData) redisUtil.getHashEntries(token).get("roleFunction");
        Map<String , Function> functionMap;

        if(!roleFunction.getName().equals(SystemConsts.SYSTEM_ADMIN_ROLE_NAME)){
            functionMap = roleFunction.getFunctionActions().get(tableName);
            if(null == functionMap){
                throw new BusinessException(ApiErrorCode.AUTH_ERROR,"you cant use this table.");
            }
        } else {
            functionMap = null;
        }

        if(null == functionMap){
           List<Function> functions =  this.functionService.getFunctionByTableName(tableName);
            functions.forEach(item->{
                functionMap.put(item.getFunctionType(),item);
            });
        }

        tokenRoleFunctionResult.setCorrect(Boolean.TRUE);
        tokenRoleFunctionResult.setFunctionMapByToken(functionMap);
        return tokenRoleFunctionResult;
    }
}
