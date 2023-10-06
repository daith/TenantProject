package com.example.cruddata.constant;

import com.example.cruddata.config.MultiTenantManager;
import com.example.cruddata.controller.TenantController;
import com.example.cruddata.exception.InvalidDbPropertiesException;
import com.example.cruddata.exception.InvalidTenantIdExeption;
import com.example.cruddata.exception.TenantNotFoundException;
import com.example.cruddata.exception.TenantResolvingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

public class DataSourceInfo {

    private static final Logger log = LoggerFactory.getLogger(DataSourceInfo.class);

    public static final String MSG_INVALID_TENANT_ID = "[!] DataSource not found for given tenant Id '{}'!";
    public static final String MSG_INVALID_DB_PROPERTIES_ID = "[!] DataSource properties related to the given tenant ('{}') is invalid!";
    public static final String MSG_RESOLVING_TENANT_ID = "[!] Could not resolve tenant ID '{}'!";

    public  static void setTenant(MultiTenantManager tenantManager , String tenantId) {
        try {
            tenantManager.setCurrentTenant(tenantId);
        } catch (SQLException e) {
            log.error(MSG_INVALID_DB_PROPERTIES_ID, tenantId);
            throw new InvalidDbPropertiesException();
        } catch (TenantNotFoundException e) {
            log.error(MSG_INVALID_TENANT_ID, tenantId);
            throw new InvalidTenantIdExeption();
        } catch (TenantResolvingException e) {
            log.error(MSG_RESOLVING_TENANT_ID, tenantId);
            throw new InvalidTenantIdExeption();
        }
    }
}
