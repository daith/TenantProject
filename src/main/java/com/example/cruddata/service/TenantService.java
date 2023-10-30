package com.example.cruddata.service;

import com.example.cruddata.entity.account.Tenant;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.List;

public interface TenantService {

    void validatedTenantProcess(Long tenantId);

    List<Tenant> getAllTenantByStatus(Boolean idDelete,String Status);

    void addTenant(Tenant tenantData) throws JsonProcessingException;

    void resetTenantRedisData() throws JsonProcessingException;
}
