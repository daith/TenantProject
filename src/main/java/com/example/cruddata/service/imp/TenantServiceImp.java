package com.example.cruddata.service.imp;

import com.example.cruddata.constant.ApiErrorCode;
import com.example.cruddata.constant.RedisKeyConsts;
import com.example.cruddata.entity.account.Tenant;
import com.example.cruddata.exception.BusinessException;
import com.example.cruddata.repository.system.TenantRepository;
import com.example.cruddata.service.TenantService;
import com.example.cruddata.util.RedisUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
@Data
@Transactional
public class TenantServiceImp implements TenantService {

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    TenantRepository tenantRepository;
    @Override
    public void validatedTenantProcess(Long tenantId) {
        Tenant data = null ;
        
        if (null != redisUtil.getHashEntries(RedisKeyConsts.TENANT)) {
             data = (Tenant) redisUtil.getHashEntries(RedisKeyConsts.TENANT).get(tenantId);
        }else{
            Optional<Tenant> tenant = this.tenantRepository.findById(tenantId);
            data = tenant.isPresent() ? tenant.get() : null;
        }

        if( data != null  && data.getIsDeleted() == true){
            throw new BusinessException(ApiErrorCode.VALIDATED_ERROR , "tenantId is not useful");
        }
    }

    @Override
    public List<Tenant> getAllTenantByStatus(Boolean idDeleted, String status) {

        return tenantRepository.findByStatusAndStatus(status,idDeleted);

    }

    @Override
    public void addTenant(Tenant tenant) throws JsonProcessingException {
        this.tenantRepository.save(tenant);
        ObjectMapper mapper = new ObjectMapper();
        String data=  mapper.writeValueAsString(tenant);
        if(null == redisUtil.getHashEntries(RedisKeyConsts.TENANT)){
            redisUtil.add(RedisKeyConsts.TENANT,new HashMap<>());
        }
        redisUtil.add(RedisKeyConsts.TENANT,String.valueOf(tenant.getId()),data);
    }

    @Override
    public void resetTenantRedisData() throws JsonProcessingException {
        List<Tenant> tenantList = this.tenantRepository.findAll();

        ObjectMapper mapper = new ObjectMapper();
        for(Tenant tenant:tenantList){
            String data=  mapper.writeValueAsString(tenant);
            if(null == redisUtil.getHashEntries(RedisKeyConsts.TENANT)){
                redisUtil.add(RedisKeyConsts.TENANT,new HashMap<>());
            }
            redisUtil.add(RedisKeyConsts.TENANT,String.valueOf(tenant.getId()),data);
        }
    }


}
