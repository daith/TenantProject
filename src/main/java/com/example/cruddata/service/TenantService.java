package com.example.cruddata.service;

import com.example.cruddata.entity.authroty.Tenant;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.List;

public interface TenantService {

    void validatedTenantProcess(Long tenantId);

    List<Tenant> getAllTenantByStatus(Boolean idDelete,String Status);

    Tenant getTenantById(Long tenantId);

    Tenant getTenantByName(String tenantName);

    Tenant updateTenant(Tenant tenant);

    void addTenant(Tenant tenantData) throws JsonProcessingException;

    void resetTenantRedisData() throws JsonProcessingException;
}
